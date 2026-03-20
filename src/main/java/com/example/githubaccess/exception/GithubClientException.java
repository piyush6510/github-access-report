package com.example.githubaccess.exception;

import org.springframework.http.HttpStatusCode;

public class GithubClientException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public GithubClientException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
