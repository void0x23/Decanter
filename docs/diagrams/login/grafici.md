# Login Flow with Google

```mermaid

User->>Frontend: Clicks "Sign in with Google"
Frontend->>Google: Authentication request (client_id, scope)
Google-->>User: Displays login screen
User->>Google: Enters credentials and confirms
Google-->>Frontend: Returns ID Token

Frontend->>Backend: Sends ID Token to /auth/login
Backend->>Google: Verifies ID Token with Google
Google-->>Backend: Returns user info (if token is valid)

Backend->>Microservice: Sends user info for verification
Microservice->>Database: Checks if user exists
alt Existing user
Database-->>Microservice: Returns existing user data
Microservice-->>Backend: Returns user data
else Non-existing user
Microservice->>Database: Creates new user
Database-->>Microservice: Confirms user creation
Microservice-->>Backend: Returns new user data
end

Backend-->>Frontend: Returns user data and JWT
Frontend-->>User: Displays authenticated user interface


```
