# GitHub Access Report Service

A Spring Boot backend service that connects to GitHub and generates an aggregated access report showing which users have access to which repositories in a given organization.

## Features

- Authenticate with GitHub using a Personal Access Token
- Fetch organization repositories
- Fetch repository collaborators
- Aggregate data as user -> repositories
- Expose the report through a REST API
- Handle errors gracefully
- Support concurrent collaborator fetches for better performance

## API Endpoint

`GET /api/v1/orgs/{organization}/access-report`

Example:
`http://localhost:8080/api/v1/orgs/my-org/access-report`

## Authentication

Configure a GitHub Personal Access Token using either:

- `application.yml`
- environment variable `GITHUB_TOKEN`

Example:

```yaml
github:
  token: YOUR_TOKEN_HERE
```
