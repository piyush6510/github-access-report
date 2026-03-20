package com.example.githubaccess.dto;

import java.util.Map;

public record RepositoryAccessDto(
        String repository,
        String repositoryUrl,
        boolean privateRepository,
        String visibility,
        String roleName,
        Map<String, Boolean> permissions
) {
}
