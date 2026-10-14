
# ADR-0002 — Filesystem Storage for 3D Printing Models

## Status

Accepted

## Context

Lovra needs to preserve digital 3D printing models used by its business operation.

Currently, digital models are primarily discovered through the Bambu Lab ecosystem.

However, the availability of a model on an external platform does not guarantee its permanent accessibility.

Models may be removed, become unavailable, or change over time.

Lovra must not depend exclusively on external URLs to reproduce products that have already been evaluated and commercialized.

Therefore, the application needs to preserve its own copy of relevant digital model files.

The storage solution must support:

- Uploading and preserving digital model files.
- Downloading previously stored files.
- Associating files with business entities.
- Maintaining file metadata.
- Detecting identical binary content.
- Supporting local development.
- Allowing the application to be executed by other developers.
- Avoiding unnecessary infrastructure costs.

The application is initially intended for a small-scale operation.

File sizes, storage volume, and access frequency have not yet been measured sufficiently to justify a distributed storage infrastructure.

## Decision Drivers

The storage strategy should:

- Preserve digital models independently of their original platforms.
- Avoid unnecessary external infrastructure.
- Keep the initial implementation operationally simple.
- Support potentially large binary files.
- Separate file metadata from binary storage.
- Allow storage configuration across different environments.
- Support future replacement of the storage implementation.
- Avoid coupling domain entities directly to filesystem APIs.
- Make storage consistency and deletion responsibilities explicit.

## Alternatives Considered

### 1. PostgreSQL BYTEA

Digital model files could be stored directly in PostgreSQL using the BYTEA data type.

Advantages:

- Binary content and metadata can participate in the same database transaction.
- Database backup and restore can include both metadata and content.
- No separate filesystem storage location is required.
- Referential and transactional consistency are easier to manage.

Disadvantages:

- Large binary content increases database storage requirements.
- Database backups may become larger.
- Database I/O and storage resources are shared with transactional business data.
- Large file operations may increase pressure on database infrastructure.

BYTEA remains a technically valid alternative.

However, the initial design favors separating potentially large digital model binaries from the relational database.

### 2. PostgreSQL Large Objects

PostgreSQL provides a Large Object facility for storing binary content.

Advantages:

- Supports large binary objects.
- Provides an API for accessing large objects in portions.
- Keeps binary content within PostgreSQL infrastructure.

Disadvantages:

- Introduces additional lifecycle management concerns.
- Large Object references and business entity relationships require careful coordination.
- Orphaned Large Objects may require explicit cleanup.
- Adds database-specific implementation considerations.

This alternative was not selected for the initial implementation.

### 3. Object Storage — Amazon S3

Digital models could be stored in Amazon S3 or a compatible object storage service.

Advantages:

- Designed for object storage.
- Supports large files.
- Separates binary storage from application database resources.
- Can support future scaling and distributed deployments.
- Provides mature object management capabilities.

Disadvantages:

- Introduces an additional infrastructure dependency.
- May involve storage, request, and transfer costs.
- Requires credentials, permissions, and environment configuration.
- Adds operational complexity to local development and self-hosted installations.

S3 remains a possible future alternative.

It is not justified by the current operational requirements.

### 4. Local Filesystem

Digital model files can be stored in a configurable filesystem directory.

Advantages:

- Simple initial infrastructure.
- No external object storage service required.
- Suitable for local development and small-scale deployment.
- Supports binary files without storing their contents in relational tables.
- Can be adapted to containerized environments using persistent volumes.

Disadvantages:

- Filesystem operations are not part of PostgreSQL transactions.
- Backup and restore must coordinate database metadata and stored files.
- Multiple application instances require an appropriate shared storage strategy.
- File permissions and storage capacity must be managed.
- File deletion and failure recovery require explicit application logic.

This alternative was selected.

## Decision

Lovra will initially store digital 3D printing model files in the filesystem.

PostgreSQL will store file metadata and a logical storage reference.

The domain model separates:

- PrintModel: the business representation of a digital printing model.
- StoredFile: metadata and storage reference of its preserved binary file.
- Filesystem storage: the actual binary content.

The application must not store environment-specific absolute filesystem paths as permanent business data.

The filesystem storage root must be externally configurable.

The implementation should allow the storage mechanism to be replaced in the future without requiring business entities to depend directly on filesystem APIs.

## Domain Model

The current relationship is:

```text
+----------------------+
|      PrintModel      |
+----------------------+
| id                   |
| name                 |
| description          |
| sourceUrl            |
| status               |
| storedFile           |
| createdAt            |
| updatedAt            |
+-----------+----------+
            |
            | 1:1
            v
+----------------------+
|      StoredFile      |
+----------------------+
| id                   |
| originalFilename     |
| contentType          |
| sizeInBytes          |
| fileFormat           |
| sha256               |
| storageReference     |
| createdAt            |
| updatedAt            |
+-----------+----------+
            |
            | Logical reference
            v
+----------------------+
|      Filesystem      |
+----------------------+
| Binary model content |
+----------------------+
```

The current persistence model uses a one-to-one relationship from PrintModel to StoredFile.

A PrintModel references one preserved file.

This decision reflects the current understanding that a registered digital model has one associated downloadable artifact.

Support for multiple file artifacts, revisions, or model versions would require a separate domain decision.

## StoredFile Responsibilities

StoredFile represents metadata about the preserved binary content.

Its responsibilities include maintaining:

- Original filename.
- Content type.
- File format.
- File size.
- SHA-256 digest.
- Logical storage reference.
- Audit timestamps.

StoredFile does not directly perform filesystem operations.

Filesystem access belongs to infrastructure components.

## File Formats

The current FileFormat enumeration supports:

- STL
- THREE_MF

Additional formats must be introduced according to actual requirements.

A file extension alone must not be considered sufficient proof that uploaded content is valid.

The exact upload validation strategy remains to be defined.

## Content Identification

Lovra records a SHA-256 digest for stored files.

The digest is intended to support identification of identical binary content.

The current metadata model treats the digest as unique.

This prevents the creation of multiple StoredFile records representing identical binary content.

However, SHA-256 identifies binary content, not business meaning.

Two files may represent the same printable object while containing different bytes.

Likewise, two models with similar names are not necessarily identical.

The upload workflow must define how an attempted duplicate is handled.

A uniqueness constraint in PostgreSQL provides a final integrity safeguard, but does not replace application-level validation.

## Storage References

StoredFile contains a storageReference attribute.

This value identifies the stored content independently of the filesystem root used by a particular environment.

Example:

```text
Storage root:
C:\lovra\storage

Stored reference:
models/4c93b2a8-7d1e-4b02-9a3d-0e78e1123456.3mf
```

The physical location is resolved by combining the configured storage root with the logical reference.

For another environment:

```text
Storage root:
/var/lib/lovra

Stored reference:
models/4c93b2a8-7d1e-4b02-9a3d-0e78e1123456.3mf
```

The same stored reference can be resolved without modifying the database record.

Storage references must be generated and validated by the application.

User-provided filenames must not be used directly as trusted filesystem paths.

The implementation must prevent path traversal and access outside the configured storage root.

## Configuration

The storage root must be externally configurable.

A possible Quarkus configuration is:

```properties
lovra.storage.root=${LOVRA_STORAGE_ROOT}
```

Example development values:

Windows:

```text
LOVRA_STORAGE_ROOT=C:\lovra\storage
```

Linux:

```text
LOVRA_STORAGE_ROOT=/var/lib/lovra
```

The exact configuration property and environment variable must be confirmed during infrastructure implementation.

The configured directory must be writable by the application process.

## Containerized Execution

If Lovra is executed inside a container, persistent model files must not depend on the container's ephemeral writable layer.

A persistent volume or bind mount should be used.

Example conceptual mapping:

```text
Host or persistent volume
        |
        v
/app/storage
        |
        v
Lovra application
```

The storage root inside the container can remain stable while the underlying persistent storage is managed externally.

This supports container recreation without intentionally discarding preserved model files.

Containerization does not automatically solve backup, sharing, or consistency requirements.

## Application Architecture

Filesystem access should be encapsulated behind an infrastructure boundary.

A possible future abstraction is:

```java
public interface FileStorage {

    String store(...);

    ... load(...);

    void delete(...);
}
```

The example is conceptual and does not define a final Java API.

A filesystem implementation may use Java NIO APIs internally.

Business services should depend on storage capabilities rather than directly constructing filesystem paths.

A future S3 implementation could provide equivalent storage operations without requiring the domain model to depend on AWS SDK types.

The abstraction should remain limited to capabilities actually required by application use cases.

## Persistence Lifecycle

The current PrintModel relationship uses:

- One-to-one association.
- Lazy fetching.
- Non-optional association.
- Unique foreign key.
- CascadeType.PERSIST.

CascadeType.REMOVE is intentionally not included.

Removing a StoredFile database record through JPA does not automatically remove its physical binary file.

Similarly, removing a physical file does not automatically remove its database metadata.

File deletion must be explicitly coordinated by an application service.

## Transaction Consistency

PostgreSQL transactions and filesystem operations do not share a single atomic transaction.

This creates possible partial-failure scenarios.

### Scenario 1 — File Written, Database Transaction Fails

1. The application writes the binary file.
2. The database operation fails.
3. The transaction is rolled back.
4. The file remains in the filesystem.

The application may need to remove the newly written file or record it for later cleanup.

### Scenario 2 — Database Record Created, File Write Fails

1. The application prepares or persists file metadata.
2. The filesystem operation fails.
3. The application must avoid committing metadata that references unavailable content.

The operation should fail consistently and the database transaction should be rolled back when appropriate.

### Scenario 3 — Database Deletion Succeeds, File Deletion Fails

1. The database metadata is removed.
2. The physical file cannot be deleted.
3. An orphaned file remains.

This situation requires an explicit recovery or cleanup strategy.

### Consistency Requirements

The application must not assume that a database rollback reverses filesystem operations.

Upload and deletion workflows must define:

- Operation ordering.
- Failure handling.
- Cleanup responsibilities.
- Retry behavior where appropriate.
- Recovery from partially completed operations.

The exact strategy will be designed when the corresponding application services are implemented.

## Backup and Recovery

A complete Lovra backup must include:

- PostgreSQL application data.
- Preserved digital model files.

Backing up only the database is insufficient to restore preserved model content.

Backing up only the filesystem is insufficient to restore business relationships and metadata.

Backup and recovery procedures must account for consistency between these two resources.

The exact backup strategy remains outside the current implementation scope.

## Security Considerations

Filesystem storage introduces security responsibilities.

The implementation must:

- Validate storage references.
- Prevent path traversal.
- Avoid trusting original filenames.
- Restrict storage directories to the application.
- Validate file size and accepted formats.
- Protect file download endpoints with appropriate authorization.
- Avoid exposing arbitrary filesystem paths through API responses.
- Handle storage failures without leaking sensitive infrastructure details.

Stored files must not become publicly accessible merely because they exist on the server filesystem.

## Consequences

### Positive

- Digital model files remain independent of their original external platforms.
- Binary content does not increase PostgreSQL storage requirements.
- The initial solution does not require S3 or another object storage service.
- Storage locations can vary across development and deployment environments.
- File metadata remains queryable through PostgreSQL.
- Future storage implementations can be introduced behind an infrastructure abstraction.

### Negative

- Database and filesystem operations require explicit consistency handling.
- Backups must include two storage resources.
- Filesystem capacity and permissions must be managed.
- Multiple backend instances may require shared storage.
- File deletion requires more than JPA cascading.
- Additional recovery logic may be necessary for partial failures.

## Future Considerations

The following requirements may justify revisiting this decision:

- Increased file storage volume.
- Multiple backend instances.
- Distributed deployment.
- Remote file access.
- Cloud hosting requirements.
- Higher availability requirements.
- Automated backup and recovery.
- Versioning of digital models.
- Multiple files per model.

Possible future alternatives include Amazon S3 and compatible object storage services.

A migration must preserve the logical relationship between PrintModel, StoredFile, and stored binary content.

## Related Documentation

- `docs/architecture/overview.md`
- `docs/architecture/domain-model.md`
- `docs/adr/0003-product-photo-storage.md`

## Decision Summary

Lovra will initially preserve digital 3D printing models in a configurable filesystem directory.

PostgreSQL will maintain file metadata, content digests, and logical storage references.

PrintModel and StoredFile remain separate domain entities.

Filesystem operations must be coordinated explicitly because they do not participate in PostgreSQL transactions.

The solution prioritizes operational simplicity while preserving the possibility of adopting another storage implementation when concrete requirements justify the change.
