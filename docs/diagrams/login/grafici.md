# Login Flow with Google

```mermaid
sequenceDiagram
User->>Frontend: Clicks "Sign in with Google"
Frontend->>Google: Authentication request (client_id, scope, redirect_uri)
Google-->>User: Displays login screen
User->>Google: Enters credentials and confirms
Google-->>Frontend: Redirects to redirect_uri (authorization code)

Frontend->>Backend: Sends authorization_code
Backend->>Google: Token request (authorization code)
Google-->>Backend: Returns id_token and access_token
Backend->>Backend: Decodes id_token and verifies user
Backend->>Database: Checks if user exists
alt Existing user
Database-->>Backend: Returns user data
else Non-existing user
Backend->>Database: Creates new user
Database-->>Backend: Confirms creation
Backend-->>Frontend: Returns user data
end
Backend-->>Frontend: Returns user data
Frontend-->>User: Displays authenticated user interface

