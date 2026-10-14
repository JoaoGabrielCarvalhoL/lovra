
# ADR-0003 — PostgreSQL BYTEA Storage for Product Photographs

## Status

Accepted

## Context

Lovra needs to store photographs of physical products that have been printed and validated.

These photographs serve as visual references for the product catalog and may be used in future quotations, order details, and customer-facing interfaces.

The application also stores digital 3D printing models.

However, digital model files and product photographs have different characteristics and storage requirements.

Digital printing models:

- May have varying and potentially significant file sizes.
- Must be preserved independently of external model platforms.
- Are primarily accessed for download and reproduction.
- Are stored in the filesystem according to ADR-0002.

Product photographs:

- Represent visual documentation of physical products.
- Are expected to have controlled file sizes.
- May be displayed frequently in catalog interfaces.
- Must be associated with their respective products.
- Can be managed within the same database as other business information.

The original proposal considered reusing StoredFile for product photographs.

This proposal was reconsidered because the reasons for introducing filesystem storage for digital models do not automatically apply to product images.

## Decision Drivers

The storage strategy should:

- Support photographs associated with registered products.
- Keep the initial implementation simple.
- Allow image content and metadata to be managed transactionally.
- Avoid unnecessary filesystem consistency handling.
- Support controlled image uploads.
- Allow product photographs to be removed with their owning product.
- Avoid retrieving binary content unnecessarily during metadata queries.
- Remain appropriate for the expected small-scale operation.

## Alternatives Considered

### 1. Reuse StoredFile and Filesystem Storage

ProductPhoto could reference StoredFile, using the same filesystem infrastructure as PrintModel.

Advantages:

- Reuses an existing storage mechanism.
- Keeps binary content outside PostgreSQL.
- Avoids increasing database size with image content.
- Provides a common abstraction for externally stored files.

Disadvantages:

- Introduces filesystem consistency concerns for product photographs.
- Requires coordination between database transactions and filesystem operations.
- Complicates backup and recovery of product data.
- Couples photograph management to a storage strategy originally selected for different requirements.
- Adds storage references and lifecycle coordination that may not be necessary for controlled images.

This alternative was not selected.

### 2. PostgreSQL BYTEA in ProductPhoto

ProductPhoto could store image metadata and binary content directly in PostgreSQL.

Advantages:

- Simple persistence model.
- Image content and metadata participate in the same database transaction.
- Product photograph deletion can be handled through relational persistence operations.
- Database backup and restore can include photographs.
- No additional filesystem storage workflow is required.
- Appropriate for controlled image sizes and limited catalog volume.

Disadvantages:

- Binary content increases database storage requirements.
- Database backups may become larger.
- Queries must avoid loading image content unnecessarily.
- Large or excessive uploads may increase database I/O and memory usage.
- Upload limits and validation become important operational controls.

This alternative was selected.

### 3. PostgreSQL BYTEA in a Separate Content Entity

ProductPhoto could store metadata while a separate ProductPhotoContent entity stores the binary content.

Conceptual structure:

```text
Product
   |
   +--- ProductPhoto
             |
             +--- ProductPhotoContent
                       |
                       +--- BYTEA
```

Advantages:

- Separates metadata from binary content structurally.
- Allows metadata queries to avoid the binary-content table.
- Makes different access patterns explicit.
- Reduces the risk of accidentally loading binary content through ordinary entity queries.

Disadvantages:

- Introduces another entity and database table.
- Requires an additional relationship.
- Increases persistence mapping complexity.
- May be unnecessary for the expected image sizes and access patterns.

This alternative was not selected for the initial implementation.

It may be reconsidered if actual query behavior or performance requirements justify the separation.

### 4. External Object Storage

Product photographs could be stored in Amazon S3 or another object storage service.

Advantages:

- Separates binary storage from PostgreSQL.
- Supports scalable object storage.
- Can support future image delivery and processing architectures.

Disadvantages:

- Introduces an additional infrastructure dependency.
- Requires storage credentials and configuration.
- Adds operational complexity.
- Requires explicit consistency handling between database records and external objects.
- Is not justified by the current scale of the application.

This alternative was not selected.

## Decision

Lovra will initially store product photographs directly in PostgreSQL using the BYTEA data type.

The ProductPhoto entity will contain both image metadata and binary content.

ProductPhoto will not reference StoredFile.

The current design will not introduce a separate ProductPhotoContent entity.

Photograph content will be stored in the same relational database as Product and other business entities.

The implementation must ensure that ordinary metadata queries do not unnecessarily retrieve binary image content.

## Domain Model

The current relationship is:

```text
+----------------------+
|       Product        |
+----------------------+
| id                   |
| name                 |
| description          |
| printModel           |
| materials            |
| photos               |
| createdAt            |
| updatedAt            |
+-----------+----------+
            |
            | 1:N
            v
+----------------------+
|     ProductPhoto     |
+----------------------+
| id                   |
| product              |
| originalFilename     |
| contentType          |
| sizeInBytes          |
| content              |
| displayOrder         |
| createdAt            |
| updatedAt            |
+----------------------+
            |
            v
+----------------------+
|      PostgreSQL      |
|        BYTEA         |
+----------------------+
```

Each ProductPhoto belongs to one Product.

A Product may have multiple photographs.

The photograph has no independent business purpose outside its associated Product.

## ProductPhoto Attributes

The current entity contains:

| Attribute | Type | Description |
|---|---|---|
| id | UUID | Unique identifier |
| product | Product | Owning product |
| originalFilename | String | Original uploaded filename |
| contentType | String | Image media type |
| sizeInBytes | Long | Binary content size |
| content | byte[] | Image binary content |
| displayOrder | Integer | Presentation order |
| createdAt | Instant | Creation timestamp |
| updatedAt | Instant | Last update timestamp |

The content field is intended to map to PostgreSQL BYTEA.

The generated database schema must be inspected to confirm the actual column type.

The implementation should not assume that adding @Lob to byte[] is necessary or appropriate for BYTEA mapping.

## Relationship and Lifecycle

Product manages the lifecycle of its photographs.

The current Product mapping uses:

- @OneToMany
- CascadeType.ALL
- orphanRemoval = true

ProductPhoto references Product through a non-optional many-to-one association.

Conceptually:

```text
Product
   |
   +--- ProductPhoto A
   +--- ProductPhoto B
   +--- ProductPhoto C
```

When a photograph is removed from the managed Product collection, the corresponding ProductPhoto record may be deleted through orphan removal.

When a Product is removed, its associated photographs may be removed through cascading persistence operations.

Because photograph content is stored in PostgreSQL, these database operations can participate in the same transaction.

No external filesystem deletion is required for ProductPhoto.

Bidirectional associations must be synchronized by application or domain operations.

## Binary Mapping

The Java representation of image content is:

```java
@Column(nullable = false)
private byte[] content;
```

The intended PostgreSQL representation is BYTEA.

The actual Hibernate ORM mapping and generated schema must be verified during implementation.

The use of @Lob must not be introduced automatically because PostgreSQL Large Objects and BYTEA have different persistence characteristics.

The selected storage strategy is specifically PostgreSQL BYTEA.

## Metadata and Content Access

Product photographs have at least two relevant access patterns.

### Metadata Access

Catalog and administrative screens may need:

- Photograph identifier.
- Product identifier.
- Original filename.
- Content type.
- File size.
- Display order.

These operations should not require loading the image binary content.

### Content Access

The frontend may request the actual image content through a dedicated endpoint.

A possible API is:

```http
GET /api/product-photos/{id}/content
```

The response should provide the appropriate image media type and binary content.

The exact API contract has not yet been finalized.

### Query Strategy

The application should use appropriate queries or projections for metadata-only operations.

A basic JPA byte[] attribute must not be assumed to be lazily loaded merely because the surrounding entity relationship uses FetchType.LAZY.

The fetch strategy of an association and the loading behavior of basic binary attributes are separate concerns.

If metadata queries become difficult to optimize or binary loading becomes a measurable problem, the separate ProductPhotoContent alternative should be reconsidered.

## Display Order

ProductPhoto contains a displayOrder attribute.

This value determines the intended presentation order of photographs associated with a Product.

Example:

```text
Product: Kurama

Photo A -> displayOrder = 1
Photo B -> displayOrder = 2
Photo C -> displayOrder = 3
```

The current design does not introduce an isPrimary attribute.

A cover photograph may be derived from the first photograph in the defined order.

This avoids maintaining two potentially conflicting representations of photograph priority.

The following rules remain to be defined:

- Whether display order starts at zero or one.
- Whether duplicate positions are allowed.
- How positions are updated after deletion.
- Whether order changes must be atomic.

## Upload Validation

The application must not accept arbitrary binary content without validation.

The following policies remain pending:

- Maximum photograph size.
- Accepted image formats.
- Maximum number of photographs per product.
- Image dimensions.
- Image compression or resizing.
- Handling of malformed image files.

The contentType supplied by a client must not be treated as sufficient proof that uploaded content is a valid image.

Validation must be implemented at the backend.

Upload restrictions should be established before exposing photograph upload functionality.

## File Size Metadata

ProductPhoto contains sizeInBytes.

Although the size can be derived from the binary content, storing it as metadata allows the application to report file size without retrieving the complete image.

The application must keep sizeInBytes consistent with the stored content.

The size must not be accepted blindly from untrusted client input.

## Transaction Consistency

ProductPhoto metadata and binary content are stored in the same PostgreSQL database.

This allows their persistence operations to participate in the same database transaction.

Example:

1. The application creates a ProductPhoto.
2. The binary content is persisted.
3. The database transaction commits.
4. Both metadata and content become persistent.

If the transaction fails, the database changes can be rolled back together.

This differs from PrintModel storage, where filesystem operations require explicit coordination with PostgreSQL transactions.

The transactional advantage does not eliminate the need for validation, error handling, and resource limits.

## Backup and Recovery

Product photographs are included in PostgreSQL database storage.

A database backup that includes the relevant tables and binary data can preserve ProductPhoto content.

This simplifies backup coordination compared with externally stored digital printing models.

However, larger amounts of binary content may increase:

- Database size.
- Backup duration.
- Restore duration.
- Storage requirements.

The operational impact must be evaluated as data volume grows.

## Security Considerations

The implementation must:

- Validate uploaded image content.
- Enforce file size limits.
- Restrict accepted formats.
- Protect upload and deletion endpoints.
- Apply backend authorization rules.
- Avoid exposing unnecessary file metadata.
- Prevent unrestricted binary uploads.
- Return appropriate content types when serving images.
- Avoid trusting client-supplied filenames or media types.

Photographs must not become publicly accessible unless a future business requirement explicitly establishes public access.

## Consequences

### Positive

- Product photographs are managed entirely within PostgreSQL.
- Metadata and binary content share database transaction boundaries.
- No additional filesystem cleanup is required.
- Product lifecycle operations remain relatively simple.
- The design avoids introducing another entity solely for binary storage.
- Product photographs remain independent of StoredFile and PrintModel storage concerns.

### Negative

- PostgreSQL stores additional binary content.
- Database size and backup requirements increase.
- Metadata queries require care to avoid unnecessary binary loading.
- Upload validation and resource limits are necessary.
- The approach may require revision if image volume or size grows substantially.

## Conditions for Reconsideration

This decision should be revisited if:

- Product photographs become significantly larger.
- Catalog volume increases substantially.
- Metadata queries frequently retrieve unnecessary binary content.
- Database storage or backup costs become problematic.
- Image processing and thumbnail generation become major requirements.
- Public image delivery requires a different infrastructure strategy.
- Multiple application instances or deployment environments introduce new constraints.

Possible future strategies include:

- Separating ProductPhotoContent into another database table.
- Moving image binaries to filesystem storage.
- Adopting object storage.
- Introducing derived thumbnails or optimized image representations.

Any migration must preserve associations between Product and its photographs.

## Related Documentation

- `docs/architecture/overview.md`
- `docs/architecture/domain-model.md`
- `docs/adr/0002-print-model-storage.md`

## Decision Summary

Lovra will store product photographs directly in PostgreSQL using BYTEA.

ProductPhoto contains both metadata and binary content.

The entity belongs to the Product lifecycle and does not reuse StoredFile.

This decision reflects the expected controlled size and limited volume of product photographs.

The design prioritizes transactional consistency and implementation simplicity while requiring efficient metadata queries and explicit upload validation.
