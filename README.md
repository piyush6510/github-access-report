# 🚀 GitHub Access Report Service

A Spring Boot backend service that connects to GitHub and generates an **aggregated access report** showing which users have access to which repositories in a given organization.

---

## 📌 Features

- 🔐 Authenticate with GitHub using a Personal Access Token  
- 📦 Fetch all repositories of an organization  
- 👥 Fetch collaborators for each repository  
- 🔄 Aggregate data into **user → repositories mapping**  
- ⚡ Concurrent API calls for better performance  
- 🌐 Expose the report via REST API  
- ❗ Proper error handling (401, 403, 404)  

---

## 🛠️ Tech Stack

- Java 17  
- Spring Boot 3  
- Spring Web + WebFlux (WebClient)  
- Maven  
- Caffeine Cache  

---

## 🔗 API Endpoint

```http
GET /api/v1/orgs/{organization}/access-report

## Example
curl http://localhost:8080/api/v1/orgs/my-org/access-report

📊 Sample Response
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
            "admin": false
          }
        }
      ]
    }
  ]
}

🔑 Authentication

Set GitHub token before running:

Linux / Mac
export GITHUB_TOKEN=your_token_here
Windows PowerShell
$env:GITHUB_TOKEN="your_token_here"
Required Permissions

Repository metadata: read

Organization members: read (if required)

For classic tokens: repo, read:org
▶️ How to Run
1. Clone the project
git clone https://github.com/piyush6510/github-access-report.git
cd github-access-report
2. Set GitHub token
export GITHUB_TOKEN=your_token_here
3. Run the application
mvn spring-boot:run
4. Call API
http://localhost:8080/api/v1/orgs/{org-name}/access-report
🧠 Design Decisions
🔹 Aggregation Logic

GitHub provides data as:

repository → collaborators

Converted into:

user → repositories
🔹 Scalability Handling

Pagination (per_page=100)

Concurrent API calls

In-memory aggregation

Optional caching

🔹 Error Handling

401 → Invalid token

403 → Permission issue

404 → Organization not found

⚠️ Limitations

GitHub restricts access to repository collaborators

Collaborator data is only available if the token has sufficient permissions

For public orgs like github, API may return 403 Forbidden

👉 This is a GitHub API limitation, not a code issue

🧪 Testing Instructions

Create your own GitHub organization

Add repositories

Add collaborators

Use your organization name in API

🚀 Future Improvements

Add pagination support

Add caching optimization

Add contributors fallback

Improve logging & monitoring

Add automated tests
