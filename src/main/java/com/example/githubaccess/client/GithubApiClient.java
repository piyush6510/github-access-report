package com.example.githubaccess.client;

import com.example.githubaccess.config.GithubProperties;
import com.example.githubaccess.dto.GithubCollaboratorDto;
import com.example.githubaccess.dto.GithubRepositoryDto;
import com.example.githubaccess.exception.GithubClientException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class GithubApiClient {

    private final WebClient webClient;
    private final GithubProperties properties;

    public GithubApiClient(WebClient githubWebClient, GithubProperties properties) {
        this.webClient = githubWebClient;
        this.properties = properties;
    }

    public List<GithubRepositoryDto> getOrganizationRepositories(String organization) {
        List<GithubRepositoryDto> repositories = new ArrayList<>();
        int page = 1;

        while (true) {
            final int currentPage = page;

            GithubRepositoryDto[] response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/orgs/{org}/repos")
                            .queryParam("type", "all")
                            .queryParam("sort", "full_name")
                            .queryParam("per_page", properties.pageSize())
                            .queryParam("page", currentPage)
                            .build(organization))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, this::toException)
                    .bodyToMono(GithubRepositoryDto[].class)
                    .timeout(Duration.ofSeconds(properties.requestTimeoutSeconds()))
                    .block();

            List<GithubRepositoryDto> pageItems =
                    response == null ? List.of() : Arrays.asList(response);

            repositories.addAll(pageItems);

            if (pageItems.size() < properties.pageSize()) {
                break;
            }

            page++;
        }

        return repositories;
    }

    public List<GithubCollaboratorDto> getRepositoryCollaborators(String owner, String repo) {
        List<GithubCollaboratorDto> collaborators = new ArrayList<>();
        int page = 1;

        while (true) {
            final int currentPage = page;

            GithubCollaboratorDto[] response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/repos/{owner}/{repo}/collaborators")
                            .queryParam("affiliation", "all")
                            .queryParam("per_page", properties.pageSize())
                            .queryParam("page", currentPage)
                            .build(owner, repo))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, this::toException)
                    .bodyToMono(GithubCollaboratorDto[].class)
                    .timeout(Duration.ofSeconds(properties.requestTimeoutSeconds()))
                    .block();

            List<GithubCollaboratorDto> pageItems =
                    response == null ? List.of() : Arrays.asList(response);

            collaborators.addAll(pageItems);

            if (pageItems.size() < properties.pageSize()) {
                break;
            }

            page++;
        }

        return collaborators;
    }

    private Mono<? extends Throwable> toException(ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("GitHub API request failed")
                .map(body -> new GithubClientException(body, response.statusCode()));
    }
}