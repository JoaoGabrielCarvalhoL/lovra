
# Lovra — Domain Model

## 1. Purpose

This document describes the current domain model of Lovra, including its main entities, relationships, responsibilities, and known business constraints.

The model is based on the actual operation of a small 3D printing business.

Its purpose is to establish a shared understanding of the domain and support the implementation of business use cases.

This document distinguishes between:

- Established domain concepts.
- Current persistence modeling decisions.
- Business rules that still require implementation.
- Open questions that must be resolved before expanding the model.

The existence of an entity or relationship does not imply that its complete business behavior has already been implemented.

## 2. Domain Boundaries

The current domain is organized into four conceptual areas.

### 2.1 Digital Model Management

Responsible for preserving and evaluating digital models used for 3D printing.

Entities:

- PrintModel
- StoredFile

### 2.2 Product Catalog

Responsible for representing validated physical products and their production requirements.

Entities:

- Product
- ProductMaterial
- ProductPhoto

### 2.3 Material Management

Responsible for representing filament definitions and their physical acquisitions.

Entities:

- Filament
- FilamentSpool

### 2.4 Customer Management

Responsible for identifying customers and maintaining their contact information.

Entities:

- Customer
- CustomerContact

These areas are conceptual domain boundaries, not independent microservices or necessarily separate application modules.

Pricing, quotations, and orders will be incorporated after their business rules have been defined.

## 3. Domain Relationship Diagram

```text
PrintModel (1) -------- (1) StoredFile
     |
     | (1)
     |
     +----------------- (N) Product
                              |
                              +---- (N) ProductMaterial
                              |             |
                              |             +---- (1) Filament
                              |                       |
                              |                       +---- (N) FilamentSpool
                              |
                              +---- (N) ProductPhoto


Customer (1) ---------- (N) CustomerContact
```

The diagram represents the current relationship structure.

It does not imply that every possible association is navigable in both directions in the Java object model.

## 4. PrintModel

### 4.1 Definition

PrintModel represents a digital 3D printing model selected for preservation and potential use by Lovra.

A PrintModel is not necessarily a validated product.

Models discovered through external platforms may be evaluated before they become suitable for commercial production.

Only models considered relevant for preservation need to be registered.

### 4.2 Attributes

| Attribute | Type | Description |
|---|---|---|
| id | UUID | Unique identifier |
| name | String | Model name |
| description | String | Optional model description |
| sourceUrl | String | Original source reference |
| status | PrintModelStatus | Validation status |
| storedFile | StoredFile | Preserved digital file |
| createdAt | Instant | Creation timestamp |
| updatedAt | Instant | Last update timestamp |

### 4.3 Validation Status

PrintModelStatus contains:

- PENDING_VALIDATION
- VALIDATED
- REJECTED

A newly registered model starts as PENDING_VALIDATION.

VALIDATED means that a physical print was successfully produced and evaluated as suitable.

REJECTED means that a successfully completed print was evaluated as unsuitable.

An operational failure, such as a power outage or filament shortage, must not automatically reject the digital model.

### 4.4 Relationships

A PrintModel references one StoredFile.

A PrintModel may be referenced by multiple Products.

Different physical sizes or configurations derived from the same digital model may become distinct Products.

### 4.5 Pending Business Behavior

Future domain operations may include:

- validate()
- reject()

These operations should enforce valid state transitions.

A generic unrestricted status setter should not be the primary mechanism for changing validation state.

## 5. StoredFile

### 5.1 Definition

StoredFile represents the metadata and logical storage reference of a digital file preserved outside PostgreSQL.

The actual binary content is stored in the filesystem.

### 5.2 Attributes

| Attribute | Type | Description |
|---|---|---|
| id | UUID | Unique identifier |
| originalFilename | String | Original uploaded filename |
| contentType | String | Media type |
| sizeInBytes | Long | File size |
| fileFormat | FileFormat | Supported 3D model format |
| sha256 | String | SHA-256 content digest |
| storageReference | String | Logical or relative storage reference |
| createdAt | Instant | Creation timestamp |
| updatedAt | Instant | Last update timestamp |

### 5.3 File Formats

The current FileFormat enumeration contains:

- STL
- THREE_MF

Additional formats should be introduced only when supported by actual requirements.

### 5.4 Storage Rules

- File content is stored outside the relational database.
- PostgreSQL stores metadata and storage references.
- The storage root must be externally configurable.
- Storage references should not depend on absolute host paths.
- Original filenames must not be trusted as filesystem paths.
- SHA-256 identifies identical binary content.
- Identical file content does not necessarily imply identical business models.

The SHA-256 digest is intended to be unique in the current storage metadata model.

### 5.5 Lifecycle

StoredFile persistence may cascade when a PrintModel is initially persisted.

Physical file deletion must be coordinated explicitly by application services.

JPA cascading alone cannot guarantee consistency between PostgreSQL transactions and filesystem operations.

## 6. Product

### 6.1 Definition

Product represents a physical item that has already been printed, evaluated, and considered suitable for reproduction or commercialization.

A Product is not merely a digital model available for download.

### 6.2 Attributes

| Attribute | Type | Description |
|---|---|---|
| id | UUID | Unique identifier |
| name | String | Product name |
| description | String | Product description |
| printModel | PrintModel | Digital model used as reference |
| materials | List<ProductMaterial> | Material requirements |
| photos | List<ProductPhoto> | Photographs of the physical product |
| createdAt | Instant | Creation timestamp |
| updatedAt | Instant | Last update timestamp |

### 6.3 Relationships

Each Product references one PrintModel.

Multiple Products may reference the same PrintModel.

A Product may have multiple ProductMaterial records.

A Product may have multiple ProductPhoto records.

### 6.4 Business Considerations

Product describes a reproducible physical item.

Its identity should remain independent of changes in material prices, electricity costs, labor rates, and other economic variables.

A mutable current selling price is not part of the current Product model.

Pricing calculations and historical commercial values will be modeled separately.

Product categories and tags remain outside the current scope until their requirements are understood.

## 7. ProductMaterial

### 7.1 Definition

ProductMaterial represents a material requirement in a Product's composition.

It is an association entity because the relationship between Product and Filament contains additional business information.

### 7.2 Attributes

| Attribute | Type | Description |
|---|---|---|
| id | UUID | Unique identifier |
| product | Product | Associated product |
| filament | Filament | Required filament |
| weightInGrams | BigDecimal | Required material quantity |
| createdAt | Instant | Creation timestamp |
| updatedAt | Instant | Last update timestamp |

### 7.3 Relationships

Each ProductMaterial belongs to one Product.

Each ProductMaterial references one Filament.

A Product may contain multiple different filaments.

A Filament may be referenced by multiple ProductMaterial records.

### 7.4 Constraints

The combination of product_id and filament_id must be unique.

This prevents the same Filament from appearing more than once in the material composition of the same Product.

Different filaments may coexist within the same Product.

The material weight must be positive. This business validation is not yet fully implemented.

### 7.5 Lifecycle

ProductMaterial is part of the Product lifecycle.

The current mapping uses:

- CascadeType.ALL
- orphanRemoval = true

Removing a ProductMaterial from the Product's managed collection may result in deletion of the corresponding database record.

Bidirectional relationships must remain synchronized when business operations are implemented.

### 7.6 Important Distinction

ProductMaterial references Filament, not FilamentSpool.

It describes what material is required to reproduce a product.

It does not identify which purchased spool will be consumed during a particular production operation.

## 8. Filament

### 8.1 Definition

Filament represents a reusable material identity.

It describes the material independently of individual purchases and physical spools.

### 8.2 Attributes

| Attribute | Type | Description |
|---|---|---|
| id | UUID | Unique identifier |
| name | String | Commercial material name |
| brand | String | Manufacturer or brand |
| color | String | Material color |
| filamentType | FilamentType | Base material classification |
| createdAt | Instant | Creation timestamp |
| updatedAt | Instant | Last update timestamp |

### 8.3 Filament Types

The current FilamentType enumeration contains:

- PLA
- ABS
- PETG
- TPU
- ASA
- NYLON
- HIPS
- PVA

Commercial variants such as PLA Silk should not automatically become separate base material types.

Variant modeling will be reconsidered when concrete requirements arise.

### 8.4 Relationships

A Filament may have multiple FilamentSpool records.

A Filament may be used by multiple Products through ProductMaterial.

The current model does not require a bidirectional collection of spools inside Filament.

Spools may be queried independently through the persistence layer.

## 9. FilamentSpool

### 9.1 Definition

FilamentSpool represents an individual physical acquisition of filament.

Multiple spools may reference the same Filament while having different purchase prices, weights, and acquisition dates.

### 9.2 Attributes

| Attribute | Type | Description |
|---|---|---|
| id | UUID | Unique identifier |
| filament | Filament | Material identity |
| purchasePrice | BigDecimal | Acquisition price |
| initialWeightInGrams | BigDecimal | Initial material weight |
| purchasedAt | LocalDate | Acquisition date |
| createdAt | Instant | Record creation timestamp |
| updatedAt | Instant | Last update timestamp |

### 9.3 Relationships

Each FilamentSpool references one Filament.

A Filament may have multiple purchased spools.

The association from FilamentSpool to Filament does not cascade persistence or removal operations.

### 9.4 Monetary and Quantity Representation

Monetary values use BigDecimal to avoid floating-point rounding issues.

Material quantities also use BigDecimal to support fractional gram measurements.

The current persistence precision and scale are:

- purchasePrice: NUMERIC(12,2)
- initialWeightInGrams: NUMERIC(10,2)

### 9.5 Open Questions

The following rules have not yet been defined:

- Material costing policy.
- Remaining spool quantity.
- Inventory consumption.
- Stock adjustment.
- Supplier tracking.
- Acquisition batches.
- Historical material cost allocation.

These concerns must not be inferred from the existence of FilamentSpool alone.

## 10. ProductPhoto

### 10.1 Definition

ProductPhoto represents an image of a physical Product.

Unlike digital 3D printing files, product photographs are stored directly in PostgreSQL.

### 10.2 Attributes

| Attribute | Type | Description |
|---|---|---|
| id | UUID | Unique identifier |
| product | Product | Associated product |
| originalFilename | String | Original filename |
| contentType | String | Image media type |
| sizeInBytes | Long | Binary content size |
| content | byte[] | Image content |
| displayOrder | Integer | Presentation order |
| createdAt | Instant | Creation timestamp |
| updatedAt | Instant | Last update timestamp |

### 10.3 Storage Strategy

The binary content is intended to use PostgreSQL BYTEA.

The current design does not introduce a separate ProductPhotoContent entity.

Image metadata and binary content are managed within the same database.

### 10.4 Relationships and Lifecycle

Each ProductPhoto belongs to one Product.

A Product may have multiple photographs.

The current Product mapping uses:

- CascadeType.ALL
- orphanRemoval = true

Removing a photograph from its Product may delete its metadata and binary content within the database transaction.

### 10.5 Display Order

The displayOrder attribute defines the presentation order of photographs.

The current design does not include a separate isPrimary attribute.

A primary image may be derived from the ordering convention when needed.

### 10.6 Open Questions

- Maximum image size.
- Supported media types.
- Maximum number of photos per product.
- Image resizing or compression.
- Thumbnail generation.
- Validation of display order.
- Efficient metadata-only queries.

Photo listing operations should not unnecessarily retrieve binary content.

## 11. Customer

### 11.1 Definition

Customer represents a person with whom Lovra maintains a commercial relationship.

A Customer does not need to have completed a purchase.

The entity may represent someone requesting a quotation for the first time.

### 11.2 Attributes

| Attribute | Type | Description |
|---|---|---|
| id | UUID | Unique identifier |
| name | String | Customer identification |
| notes | String | Optional commercial observations |
| contacts | List<CustomerContact> | Communication methods |
| createdAt | Instant | Creation timestamp |
| updatedAt | Instant | Last update timestamp |

### 11.3 Relationships

A Customer may have multiple CustomerContact records.

Future quotations and orders may reference Customer.

These relationships have not yet been implemented.

### 11.4 Business Considerations

The current operation primarily serves known customers through direct contact.

Products are currently delivered in person.

Customer addresses, shipping information, tax identification, and customer acquisition channels are not part of the current model.

The notes field allows useful commercial context to be recorded without prematurely introducing additional structured attributes.

## 12. CustomerContact

### 12.1 Definition

CustomerContact represents a communication channel associated with a Customer.

Contact information is modeled separately to allow multiple contact methods per customer.

### 12.2 Attributes

| Attribute | Type | Description |
|---|---|---|
| id | UUID | Unique identifier |
| customer | Customer | Contact owner |
| type | ContactType | Communication channel |
| value | String | Contact value |
| createdAt | Instant | Creation timestamp |
| updatedAt | Instant | Last update timestamp |

### 12.3 Contact Types

The current ContactType enumeration contains:

- WHATSAPP
- PHONE
- EMAIL

The type represents the communication channel.

The same telephone number may be associated with different channels when necessary.

### 12.4 Relationships and Lifecycle

Each CustomerContact belongs to one Customer.

The current Customer mapping uses:

- CascadeType.ALL
- orphanRemoval = true

CustomerContact does not have an independent lifecycle outside Customer.

### 12.5 Validation and Uniqueness

Contact values must eventually be validated according to their ContactType.

No global uniqueness constraint is currently applied to contact values.

Different customers may share contact information.

Canonical telephone formatting and email validation remain to be defined.

## 13. Aggregate and Lifecycle Considerations

The current persistence model suggests the following ownership relationships.

### 13.1 Product Aggregate

Product manages the lifecycle of:

- ProductMaterial
- ProductPhoto

These records are dependent on Product.

PrintModel and Filament are referenced entities with independent lifecycles.

Removing a Product must not automatically delete its PrintModel or Filament records.

### 13.2 Customer Aggregate

Customer manages the lifecycle of CustomerContact.

Contact records are dependent on their associated Customer.

### 13.3 PrintModel and StoredFile

PrintModel references a StoredFile.

The relationship uses a restricted persistence cascade.

The filesystem requires explicit coordination for storage and deletion operations.

Database cascade removal must not be treated as sufficient for deleting externally stored files.

### 13.4 Filament and FilamentSpool

Filament and FilamentSpool have separate lifecycle concerns.

A purchased spool references a material identity, but deleting or persisting a spool must not automatically remove or create the associated Filament.

### 13.5 Aggregate Boundaries

These ownership relationships are useful starting points for aggregate design.

Formal aggregate roots, transaction boundaries, and cross-aggregate invariants must be confirmed as application use cases are implemented.

JPA relationships alone do not define complete Domain-Driven Design aggregate boundaries.

## 14. Persistence and Modeling Conventions

### 14.1 Identifiers

Entities currently use UUID identifiers generated through GenerationType.UUID.

### 14.2 Audit Timestamps

Entities use:

- createdAt
- updatedAt

The current implementation uses JPA lifecycle callbacks:

- @PrePersist
- @PreUpdate

Timestamps are represented using Instant.

### 14.3 Associations

Lazy fetching is preferred for associations where eager loading is not required.

Bidirectional associations are introduced only when domain navigation justifies them.

Cascading operations are defined according to lifecycle ownership rather than applied indiscriminately.

### 14.4 Enumerations

Enumerations use:

- Integer id
- String description
- getId()
- getDescription()
- fromId(Integer id)

Persistence mappings currently use EnumType.STRING.

The enum's numeric identifier is not the value persisted by EnumType.STRING.

### 14.5 Domain Behavior

The current development stage prioritizes establishing the persistence model.

Domain methods, invariants, and state transitions will be implemented alongside their corresponding use cases.

Public setters should not become the default mechanism for bypassing domain invariants once those behaviors are established.

## 15. Future Domain Concepts

The following concepts are recognized but not yet fully modeled.

### 15.1 Pricing

Represents the calculation of production costs and selling prices under a specific set of economic conditions.

Potential inputs include:

- Material cost.
- Electricity cost.
- Machine depreciation or wear.
- Labor.
- Packaging.
- Loss allowances.
- Pricing margin or markup.

The material costing policy remains undecided.

### 15.2 Quote

Represents a commercial offer presented to a customer.

A Quote may be required before a Product has been formally registered.

The system is expected to support both internal and customer-facing representations of quotation information.

Historical pricing information must be preserved.

### 15.3 Order

Represents a confirmed commercial transaction.

An Order may originate from a Quote or be created directly.

A formal Quote must not be mandatory for every Order.

Historical commercial values must remain stable even when current prices and costs change.

### 15.4 Production and Inventory

Future concepts may include:

- Print attempts.
- Production records.
- Spool consumption.
- Stock movements.
- Production failures.
- Material waste.

These concepts must be introduced according to concrete operational requirements.

## 16. Open Domain Questions

The following questions remain unresolved:

1. How should material acquisition costs be converted into pricing costs?
2. Should pricing use historical cost, replacement cost, or another policy?
3. Which cost components must be included in every calculation?
4. How should quotations preserve historical calculation inputs?
5. How should quotations represent products that do not yet exist in the catalog?
6. Which events establish the transition from a digital model to a validated product?
7. How should production failures and material waste be recorded?
8. Which inventory operations are necessary for the initial business workflow?
9. What validation limits should apply to uploaded product photographs?
10. Which business rules should govern customer contact management?

These questions should be resolved through business analysis and documented decisions before introducing unnecessary domain complexity.

## 17. Document Maintenance

This document describes the current domain understanding and persistence model.

It must be updated when:

- New business concepts are introduced.
- Entity responsibilities change.
- Relationships or cardinalities change.
- New invariants are established.
- Existing assumptions are invalidated by real business requirements.

Architectural decisions with meaningful alternatives should also be recorded in the ADR directory.

The high-level system architecture is described in `overview.md`.

Pricing rules and calculations are documented in `pricing.md`.
