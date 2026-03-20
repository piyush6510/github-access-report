package com.example.githubaccess.service;

import com.example.githubaccess.client.GithubApiClient;
import com.example.githubaccess.config.GithubProperties;
import com.example.githubaccess.dto.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.*;

@Service
public class AccessReportService {

    private final GithubApiClient githubApiClient;
    private final GithubProperties properties;

    public AccessReportService(GithubApiClient githubApiClient, GithubProperties properties) {
        this.githubApiClient = githubApiClient;
        this.properties = properties;
    }

    @Cacheable(cacheNames = "access-reports", key = "#organization", condition = "#root.target.isCacheEnabled()")
    public AccessReportResponse buildAccessReport(String organization) {
        List<GithubRepositoryDto> repositories = githubApiClient.getOrganizationRepositories(organization);

        Map<Long, MutableUserAccess> accessMap = Flux.fromIterable(repositories)
                .flatMap(repository -> Mono.fromCallable(() -> githubApiClient.getRepositoryCollaborators(organization, repository.name()))
                                .subscribeOn(Schedulers.boundedElastic())
                                .map(collaborators -> Map.entry(repository, collaborators)),
                        properties.collaboratorConcurrency())
                .reduce(new HashMap<Long, MutableUserAccess>(), (accumulator, entry) -> {
                    GithubRepositoryDto repository = entry.getKey();
                    List<GithubCollaboratorDto> collaborators = entry.getValue();

                    for (GithubCollaboratorDto collaborator : collaborators) {
                        MutableUserAccess userAccess = accumulator.computeIfAbsent(collaborator.id(), id ->
                                new MutableUserAccess(
                                        collaborator.id(),
                                        collaborator.login(),
                                        collaborator.htmlUrl(),
                                        collaborator.type(),
                                        collaborator.siteAdmin(),
                                        new ArrayList<>())
                        );

                        userAccess.repositories().add(new RepositoryAccessDto(
                                repository.fullName(),
                                repository.html_url(),
                                repository.privateRepo(),
                                repository.visibility(),
                                collaborator.roleName(),
                                collaborator.permissions() == null ? Map.of() : collaborator.permissions()
                        ));
                    }
                    return accumulator;
                })
                .blockOptional()
                .orElseGet(HashMap::new);

        List<UserAccessDto> users = accessMap.values().stream()
                .map(user -> new UserAccessDto(
                        user.userId(),
                        user.username(),
                        user.profileUrl(),
                        user.type(),
                        user.siteAdmin(),
                        user.repositories().size(),
                        user.repositories().stream()
                                .sorted(Comparator.comparing(RepositoryAccessDto::repository))
                                .toList()))
                .sorted(Comparator.comparing(UserAccessDto::username))
                .toList();

        return new AccessReportResponse(
                organization,
                Instant.now(),
                repositories.size(),
                users.size(),
                users
        );
    }
    public boolean isCacheEnabled() {
        return properties.enableCache();
    }

    private record MutableUserAccess(
            long userId,
            String username,
            String profileUrl,
            String type,
            boolean siteAdmin,
            List<RepositoryAccessDto> repositories
    ) {
    }
}
