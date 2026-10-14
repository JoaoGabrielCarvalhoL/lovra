
# ADR-0001 — Authentication with Keycloak

## Status

Accepted

## Context

Lovra requires authentication to restrict access to its business management functionality.

The application consists of:

- A Vue 3 Single Page Application (SPA).
- A Quarkus backend exposing REST APIs.
- A PostgreSQL database containing business information.

The application initially serves a small number of users.

Although the initial user base is limited, authentication must be implemented using established security practices.

Authentication and business authorization are separate concerns.

The application must identify authenticated users and protect backend resources without implementing its own credential management infrastructure.

## Decision Drivers

The authentication solution should:

- Avoid implementing password storage and verification manually.
- Support standards-based authentication.
- Integrate with Vue and Quarkus.
- Support secure authentication for a browser-based SPA.
- Centralize user identity management.
- Allow future authorization requirements to be introduced incrementally.
- Remain suitable for local development and future deployment environments.
- Avoid unnecessary custom security infrastructure.

## Alternatives Considered

### 1. Custom Authentication with Application-Managed JWT

Lovra could implement its own authentication endpoints and issue JWT access tokens.

This approach would require the application to manage:

- User credentials.
- Password hashing.
- Login and logout behavior.
- Token generation and validation.
- Token expiration and renewal.
- Credential recovery.
- Authentication-related security controls.

Advantages:

- Direct control over authentication behavior.
- No separate identity provider.
- Potentially simpler infrastructure for very limited scenarios.

Disadvantages:

- Additional security-sensitive code.
- Greater responsibility for credential management.
- Increased maintenance and testing requirements.
- Risk of implementing established security functionality incorrectly.

This alternative was not selected.

### 2. External Identity Provider with Keycloak

Keycloak provides identity management and supports OpenID Connect.

The frontend authenticates users through Keycloak.

The backend validates access tokens issued by the configured identity provider.

Advantages:

- Standards-based authentication.
- Centralized identity management.
- No need for Lovra to store user passwords.
- Integration with Quarkus OIDC.
- Support for future identity and access management requirements.
- Reduced custom authentication code.

Disadvantages:

- Additional infrastructure dependency.
- Separate configuration and deployment requirements.
- Identity provider availability becomes relevant to authentication workflows.
- Additional operational complexity compared with a fully self-contained application.

This alternative was selected.

## Decision

Lovra uses Keycloak as its identity provider.

Authentication is implemented using OpenID Connect.

The Vue SPA uses the OAuth 2.0 Authorization Code Flow with PKCE.

The frontend client is configured as a public client and does not use a client secret.

The Quarkus backend acts as a resource server and validates bearer access tokens.

Keycloak is responsible for:

- User authentication.
- Credential management.
- Identity management.
- Token issuance.

Lovra is responsible for:

- Protecting business API endpoints.
- Validating authenticated access through Quarkus security integration.
- Enforcing application-specific authorization rules.
- Managing business data independently of Keycloak.

## Authentication Flow

```text
+----------------------+
|       Browser        |
|       Vue SPA        |
+----------+-----------+
           |
           | 1. Authentication request
           |    Authorization Code + PKCE
           v
+----------------------+
|       Keycloak       |
|                      |
| Login and Identity   |
+----------+-----------+
           |
           | 2. Authorization code
           v
+----------------------+
|       Browser        |
|       Vue SPA        |
+----------+-----------+
           |
           | 3. Token exchange using
           |    PKCE code verifier
           v
+----------------------+
|       Keycloak       |
+----------+-----------+
           |
           | 4. Access token
           v
+----------------------+
|       Browser        |
|       Vue SPA        |
+----------+-----------+
           |
           | 5. REST request
           |    Authorization: Bearer <token>
           v
+----------------------+
|    Quarkus Backend   |
|                      |
| OIDC Token Validation|
| Protected Resources  |
+----------------------+
```

The backend does not authenticate users by receiving their passwords directly.

The backend validates access tokens and uses the authenticated identity to authorize access to protected resources.

## Implementation Details

### Keycloak

The development environment uses Keycloak running through Docker Compose.

Keycloak has a dedicated PostgreSQL database.

The frontend client is named:

`lovra-app`

The client uses:

- Public client configuration.
- Standard authorization flow.
- PKCE with S256.
- Development redirect URIs for the Vue application.

Development frontend origin:

`http://localhost:5173`

### Frontend

The frontend integrates with Keycloak through `keycloak-js`.

After successful authentication, the SPA can obtain an access token for authenticated API requests.

The application uses Pinia to maintain frontend state related to the current user.

The access token must not be written to application logs or exposed unnecessarily.

Token expiration and refresh handling must be considered when implementing the shared HTTP client.

### Backend

Quarkus uses its OIDC integration to validate access tokens.

The application is configured as an OIDC service.

Protected endpoints require authenticated access.

An existing endpoint provides information about the authenticated user:

`GET /api/me`

The endpoint obtains identity information from the validated security context and token claims.

The frontend uses this endpoint to load the current user's application-facing profile.

### User Information

The current user response includes:

- User identifier.
- Username.
- Given name.
- Family name.
- Email.
- Roles.

This response is a representation of the authenticated identity.

It is not a Customer record.

Customers are business entities and do not automatically correspond to Keycloak users.

## Authorization

Authentication confirms the identity of the user.

Authorization determines which operations the user may perform.

These responsibilities must remain separate.

The initial authentication integration does not require a complete role-based authorization model.

Application roles and permissions should be introduced when concrete business requirements justify them.

The frontend may use authorization information to control navigation and component visibility.

However, backend authorization must remain authoritative.

Hiding a frontend button is not a security boundary.

## Consequences

### Positive

- Lovra does not manage user passwords.
- Authentication follows established standards.
- Identity management is centralized.
- The frontend and backend have clearly separated security responsibilities.
- Future authorization requirements can build upon the existing authentication infrastructure.

### Negative

- Keycloak must be configured and maintained.
- The application depends on an external identity provider.
- Local development requires additional infrastructure.
- Token lifecycle handling must be implemented correctly in the frontend.
- Deployment environments must provide appropriate OIDC configuration.

## Security Considerations

The following practices are required:

- Use PKCE for the public SPA client.
- Do not embed client secrets in frontend code.
- Do not log access tokens.
- Validate access tokens on the backend.
- Enforce business authorization on backend endpoints.
- Restrict allowed redirect URIs and web origins.
- Use HTTPS in production environments.
- Review token expiration and refresh behavior.
- Avoid storing sensitive authentication information unnecessarily.

The development configuration must not be treated as a production-ready security configuration.

## Future Considerations

The following concerns may require additional decisions:

- Detailed role and permission modeling.
- User provisioning and deactivation.
- Production Keycloak deployment.
- Backup and recovery of identity data.
- Session expiration behavior.
- Logout behavior across the SPA and identity provider.
- Authentication error handling.
- Token refresh and HTTP retry strategy.

These concerns do not invalidate the current authentication decision.

## Related Documentation

- `docs/architecture/overview.md`
- `docs/architecture/domain-model.md`

## Decision Summary

Keycloak was selected to provide standards-based authentication and centralized identity management.

The Vue SPA authenticates using Authorization Code Flow with PKCE.

The Quarkus backend validates access tokens and remains responsible for enforcing application-specific authorization rules.

Lovra does not implement its own password-based authentication infrastructure.
