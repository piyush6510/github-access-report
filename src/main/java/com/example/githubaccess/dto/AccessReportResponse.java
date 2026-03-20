package com.example.githubaccess.dto;

import java.time.Instant;
import java.util.List;

public record AccessReportResponse(
        String organization,
        Instant generatedAt,
        int repositoryCount,
        int userCount,
        List<UserAccessDto> users
) {
}
