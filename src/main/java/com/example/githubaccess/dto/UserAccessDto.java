package com.example.githubaccess.dto;

import java.util.List;

public record UserAccessDto(
        long userId,
        String username,
        String profileUrl,
        String type,
        boolean siteAdmin,
        int repositoryCount,
        List<RepositoryAccessDto> repositories
) {
}
