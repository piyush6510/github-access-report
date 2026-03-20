# GitHub Access Report Service

A Spring Boot service that connects to GitHub and generates a report showing which users have access to which repositories in a GitHub organization.

## What it does

- Authenticates to GitHub using a token passed securely via environment variable
- Fetches all repositories for a GitHub organization
- Fetches collaborators for each repository
- Aggregates the data into a **user -> repositories** access report
- Exposes the report as a JSON API

## Tech stack

- Java 17
- Spring Boot 3
- Spring Web + WebFlux `WebClient`
- Maven
- Caffeine cache

## API

### Get access report

```http
GET /api/v1/orgs/{organization}/access-report
```

### Example

```bash
curl http://localhost:8080/api/v1/orgs/github/access-report
```

### Example response shape

```json
{
  "organization": "my-org",
  "generatedAt": "2026-03-20T10:15:30Z",
  "repositoryCount": 120,
  "userCount": 340,
  "users": [
    {
      "userId": 123,
      "username": "alice",
      "profileUrl": "https://github.com/alice",
      "type": "User",
      "siteAdmin": false,
      "repositoryCount": 2,
      "repositories": [
        {
          "repository": "my-org/api-service",
          "repositoryUrl": "https://github.com/my-org/api-service",
          "privateRepository": true,
          "visibility": "private",
          "roleName": "write",
          "permissions": {
            "pull": true,
            "push": true,
            "admin": false,
            "maintain": false,
            "triage": false
          }
        }
      ]
    }
  ]
}
```

## Authentication

This app uses a GitHub token from an environment variable.

### Recommended options

- Fine-grained personal access token
- GitHub App installation token

### Minimum permissions for this implementation

For a fine-grained token, configure permissions that allow:

- **Repository metadata: read**
- **Organization members: read** if your org visibility policy requires it

Depending on org/repository settings, a classic token may also need `repo` and `read:org`.

Set the token before running:

```bash
export GITHUB_TOKEN=your_token_here
```

On Windows PowerShell:

```powershell
$env:GITHUB_TOKEN="your_token_here"
```

## How to run

### 1. Clone the project

```bash
git clone <your-public-repo-url>
cd github-access-report
```

### 2. Set the GitHub token

```bash
export GITHUB_TOKEN=your_token_here
```

### 3. Run the app

```bash
./mvnw spring-boot:run
```

Or if Maven is installed:

```bash
mvn spring-boot:run
```

The service starts on:

```text
http://localhost:8080
```

## Design decisions

### 1. Aggregation model

GitHub naturally exposes collaborator data per repository, but the requirement asks for a **user-to-repositories** report. So the service:

1. fetches all repositories in the org
2. fetches collaborators for each repo
3. aggregates into a map keyed by user id

### 2. Scale handling

To support organizations with 100+ repos and 1000+ users:

- GitHub requests are paginated with `per_page=100`
- Collaborator fetches are executed concurrently with bounded concurrency
- Results are aggregated in memory only once
- A short-lived cache avoids hammering the GitHub API for repeated requests

### 3. Error handling

- GitHub API errors are returned as structured API error responses
- Timeouts are applied to upstream GitHub calls

## Assumptions

- The GitHub token has enough permissions to view the organization repositories and their collaborators
- The organization is accessible to the authenticated token
- The access report is generated on demand rather than stored in a database

## Possible improvements

- Use a GitHub App instead of PAT for stronger production security
- Add rate-limit aware retry/backoff handling
- Add ETag/conditional requests to reduce GitHub API usage
- Persist snapshots in a database for historical comparisons
- Add tests with WireMock
