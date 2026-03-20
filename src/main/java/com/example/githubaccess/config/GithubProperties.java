package com.example.githubaccess.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "github")
public record GithubProperties(
        @NotBlank String apiBaseUrl,
        @NotBlank String token,
        @Min(1) @Max(100) int pageSize,
        @Min(1) @Max(50) int collaboratorConcurrency,
        @Min(1) @Max(60) int requestTimeoutSeconds,
        boolean enableCache
) {
}
