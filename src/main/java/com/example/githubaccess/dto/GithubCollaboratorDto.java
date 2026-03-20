package com.example.githubaccess.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubCollaboratorDto(
        long id,
        String login,
        String type,
        @JsonProperty("site_admin") boolean siteAdmin,
        Map<String, Boolean> permissions,
        @JsonProperty("role_name") String roleName,
        @JsonProperty("html_url") String htmlUrl
) {
}
