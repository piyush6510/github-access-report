package com.example.githubaccess.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRepositoryDto(
        long id,
        String name,
        @JsonProperty("full_name") String fullName,
        boolean privateRepo,
        String visibility,
        String html_url,
        OwnerDto owner
) {
    @JsonProperty("private")
    public boolean privateRepo() {
        return privateRepo;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OwnerDto(String login) {}
}
