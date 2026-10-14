
# ADR-0004 — Material Costing Policy for Selling Price Formation

## Status

Proposed

No material costing policy has been formally selected.

This ADR documents the decision context, candidate approaches, and evaluation criteria.

The status must be changed to Accepted only after the business rules have been studied, validated, and explicitly approved.

## Context

Lovra needs to estimate material costs when calculating selling prices for 3D printed products.

Filament is a relevant component of production cost.

The business purchases physical filament spools and uses their material to produce items for customers.

The purchase price of filament may change over time.

Different spools of the same material may have different acquisition prices.

Consequently, the application must distinguish between:

- The historical cost of acquiring filament.
- The cost attributed to material consumed in production.
- The reference material cost used to calculate a new selling price.

These values may differ depending on the selected costing policies.

The current domain model contains:

### Filament

Represents a reusable material identity.

Attributes include:

- Name.
- Brand.
- Color.
- Material type.

### FilamentSpool

Represents an individual physical acquisition of filament.

Relevant attributes include:

- Filament.
- Purchase price.
- Initial weight.
- Purchase date.

### ProductMaterial

Represents the material requirements of a Product.

Relevant attributes include:

- Product.
- Filament.
- Required weight in grams.

ProductMaterial references Filament rather than FilamentSpool.

This allows a product's material requirements to remain independent of a particular purchased spool.

However, the pricing process must determine which monetary value should be assigned to each required gram of filament.

The current model does not yet implement inventory consumption, remaining spool quantities, or stock movements.

## Decision Drivers

The selected policy should:

- Support consistent selling price calculations.
- Reduce the risk of underpricing products.
- Reflect the economic reality of the business.
- Preserve historical acquisition information.
- Support changing material prices.
- Remain understandable to the business operator.
- Avoid unnecessary inventory complexity.
- Support reproducible pricing calculations.
- Allow historical quotations and orders to preserve their original values.
- Remain feasible for a small-scale operation.

The policy should be selected according to business requirements rather than implementation convenience alone.

## Scope of the Decision

This ADR concerns the material cost reference used when forming a new selling price.

It does not automatically determine:

- The accounting valuation of inventory.
- The historical cost of goods sold.
- The physical order in which spools are consumed.
- The inventory movement strategy.
- The treatment of taxes or supplier freight.
- The final markup or profit margin.
- The complete production cost formula.

These concerns may interact with material costing but require their own business rules.

A pricing reference policy and an inventory valuation policy do not necessarily need to be identical.

## Fundamental Distinctions

### Acquisition Cost

Acquisition cost represents the amount paid to obtain filament.

Example:

A spool containing 1,000 g was purchased for BRL 80.00.

Historical acquisition unit cost:

80.00 / 1,000 = BRL 0.08/g

This value describes the recorded acquisition.

### Replacement Cost

Replacement cost represents the estimated current cost of acquiring equivalent filament.

Example:

An equivalent 1,000 g spool currently costs BRL 110.00.

Current replacement unit cost:

110.00 / 1,000 = BRL 0.11/g

This value may be relevant when estimating the cost of replenishing material used in production.

### Pricing Reference Cost

Pricing reference cost is the unit material cost selected by the pricing policy.

It is the value applied to the material quantity required by a product.

Basic calculation:

Material Cost = Required Weight × Reference Unit Cost

The policy for determining Reference Unit Cost is the subject of this ADR.

## Alternatives Considered

### Alternative 1 — Latest Acquisition Cost

Use the unit acquisition cost from the most recent relevant FilamentSpool purchase.

Example:

Purchase A:
- 1,000 g.
- BRL 80.00.

Purchase B:
- 1,000 g.
- BRL 100.00.

The latest acquisition cost is:

100.00 / 1,000 = BRL 0.10/g

For a product requiring 300 g:

300 × 0.10 = BRL 30.00

#### Advantages

- Uses information already recorded by Lovra.
- Simple to calculate.
- Easy for the business operator to understand.
- Does not require a separate current-price catalog.

#### Disadvantages

- The latest purchase may not represent the current market price.
- Promotional purchases may temporarily reduce the reference cost.
- An unusually expensive acquisition may increase the reference disproportionately.
- The policy depends on the availability of historical purchases.

#### Questions

- What happens when a Filament has no registered spool?
- Should purchase date determine the latest acquisition?
- How should purchases recorded retrospectively be handled?
- Should supplier freight be included in the acquisition cost?

### Alternative 2 — Weighted Average Acquisition Cost

Calculate a weighted average using recorded acquisition costs and quantities.

Example:

Purchase A:
- 1,000 g.
- BRL 80.00.

Purchase B:
- 1,000 g.
- BRL 120.00.

Simplified weighted average:

(80.00 + 120.00) / (1,000 + 1,000)

= BRL 0.10/g

For a product requiring 300 g:

300 × 0.10 = BRL 30.00

#### Advantages

- Reduces sensitivity to individual acquisition prices.
- Uses historical purchase information.
- Can provide a stable reference when purchases have different prices.

#### Disadvantages

- May not reflect current replacement prices.
- Historical purchases can influence the reference long after they are consumed.
- A true inventory-weighted average requires inventory quantity tracking.
- Different averaging scopes produce different results.

#### Important Distinction

A weighted average calculated from all historical purchases is not necessarily the same as the weighted-average cost of current inventory.

The application must not present these calculations as equivalent.

#### Questions

- Should the average consider all historical purchases?
- Should it consider only available inventory?
- How would consumed quantities be tracked?
- When should the average be recalculated?

### Alternative 3 — FIFO Inventory Cost

FIFO means First In, First Out.

Under a FIFO inventory costing policy, earlier acquisition costs are assigned to consumption before later acquisition costs.

Example:

Spool A:
- 1,000 g.
- BRL 80.00.

Spool B:
- 1,000 g.
- BRL 120.00.

If 300 g are assigned entirely to Spool A:

300 × 0.08 = BRL 24.00

If earlier inventory has already been consumed, a later operation may be assigned material from Spool B.

#### Advantages

- Supports historical allocation of acquisition costs.
- Can be used in inventory valuation and cost-of-goods-sold calculations.
- Provides a defined sequence for cost allocation.

#### Disadvantages

- Requires reliable inventory movements.
- Requires tracking available quantities.
- Introduces additional implementation and operational complexity.
- Does not necessarily reflect the current cost of replacing material.
- May not be the most suitable reference for future selling prices.

#### Important Distinction

FIFO is primarily an inventory cost allocation policy.

It does not require the operator to physically consume the oldest spool first.

Likewise, selecting a particular physical spool does not automatically establish FIFO accounting.

#### Questions

- Does Lovra currently need inventory valuation?
- Is spool-level consumption tracking operationally practical?
- Would FIFO improve selling price formation?
- Would the additional complexity provide sufficient business value?

### Alternative 4 — Current Replacement Cost

Use an estimated current acquisition price for equivalent filament.

Example:

Historical purchase:
- 1,000 g.
- BRL 80.00.

Current replacement reference:
- 1,000 g.
- BRL 110.00.

For a product requiring 300 g:

300 × 0.11 = BRL 33.00

#### Advantages

- Reflects current material replenishment conditions.
- Can reduce the risk of forming new prices from outdated purchase costs.
- Separates historical acquisition information from current pricing assumptions.
- Does not require complete inventory consumption tracking to calculate a new quotation.

#### Disadvantages

- Requires a current reference price.
- Reference prices may become outdated.
- Market conditions and suppliers may vary.
- The application needs a policy for updating and validating reference prices.
- Replacement cost is an estimate and may differ from actual future acquisition cost.

#### Questions

- Who maintains the current replacement price?
- How frequently should it be updated?
- Should the reference be associated with Filament?
- Should freight and other acquisition expenses be included?
- What happens when no current reference is available?
- Should the application record when the reference was last updated?

## Comparative Evaluation

| Criterion | Latest Acquisition | Weighted Average | FIFO | Replacement Cost |
|---|---|---|---|---|
| Uses recorded purchases | Yes | Yes | Yes | Not necessarily |
| Requires current market reference | No | No | No | Yes |
| Requires inventory movements | No | Depends on definition | Yes | No |
| Reflects current replenishment cost | Not necessarily | Not necessarily | Not necessarily | Intended to |
| Implementation complexity | Low | Low to moderate | Higher | Low to moderate |
| Supports historical inventory valuation directly | Limited | Depends on implementation | Yes | No |
| Suitable as a candidate for new quotations | Yes | Yes | Requires evaluation | Yes |

This comparison is qualitative.

It does not establish a preferred alternative.

## Illustrative Business Scenario

Consider a product that requires 300 g of PLA filament.

The business has recorded two purchases:

Purchase A:
- 1,000 g.
- BRL 80.00.
- Purchased earlier.

Purchase B:
- 1,000 g.
- BRL 100.00.
- Purchased later.

The current market reference for equivalent filament is:

- 1,000 g.
- BRL 110.00.

The resulting material costs could be:

| Policy | Reference Unit Cost | Material Cost |
|---|---:|---:|
| Latest acquisition | BRL 0.10/g | BRL 30.00 |
| Historical purchase weighted average | BRL 0.09/g | BRL 27.00 |
| FIFO, assuming allocation from Purchase A | BRL 0.08/g | BRL 24.00 |
| Replacement cost | BRL 0.11/g | BRL 33.00 |

The differences arise from the costing policy, not from a change in the product's physical material requirements.

The weighted-average example uses all recorded purchases and does not represent a verified current inventory valuation.

The FIFO example assumes that sufficient quantity from Purchase A remains available under the applicable inventory records.

## Preliminary Architectural Direction

A possible direction is to use replacement cost for forming new selling prices while preserving actual acquisition costs in FilamentSpool.

This approach could separate two questions:

1. How much did the business pay for the material?
2. How much should the business consider the material to cost when preparing a new quotation?

However, this direction has not been accepted.

Before selecting it, the business must evaluate whether maintaining current replacement prices is practical and whether that information meaningfully improves pricing decisions.

No additional pricing reference entity or attribute should be introduced solely on the basis of this hypothesis.

## Historical Pricing Requirements

Regardless of the selected costing policy, historical pricing calculations must preserve the material cost reference actually used.

Example:

A quotation is prepared with:

- Filament: PLA Black.
- Required quantity: 300 g.
- Reference unit cost: BRL 0.10/g.
- Calculated material cost: BRL 30.00.

Later, the reference unit cost changes to BRL 0.13/g.

The original quotation must continue to represent the calculation performed at its creation.

It must not silently change to BRL 39.00.

A future pricing snapshot should preserve sufficient information to explain and reproduce the original calculation.

Potential fields include:

- Filament identifier.
- Filament description at calculation time.
- Required quantity.
- Reference unit cost.
- Material cost subtotal.
- Costing policy.
- Reference timestamp.
- Calculation timestamp.

The exact persistence model remains undecided.

## Relationship to ProductMaterial

ProductMaterial represents the material composition of a Product.

It records:

- Which Filament is required.
- How much of that Filament is required.

It does not record:

- Which purchased spool will be used.
- The historical purchase price.
- The current replacement price.
- A permanent selling price.

This separation is intentional.

Product composition should not be redefined every time filament prices change.

The pricing process will resolve a monetary reference independently.

## Relationship to FilamentSpool

FilamentSpool preserves acquisition information.

Its purchasePrice must retain its historical meaning.

A change in current market price must not overwrite the amount originally paid for a spool.

If the selected pricing policy requires current replacement prices, that information must be represented separately from historical acquisition data.

The exact location of that information in the domain model remains open.

## Error and Edge Cases

The selected policy must define behavior for situations such as:

- A Filament without any registered purchase.
- A missing replacement price.
- An invalid or zero reference weight.
- A zero or negative purchase price.
- A missing purchase date.
- Multiple purchases on the same date.
- Changes to historical acquisition records.
- Material quantities with fractional grams.
- Reference prices with more than two decimal places.
- A product requiring multiple different filaments.

The application should not silently substitute arbitrary values when required pricing information is missing.

Fallback behavior must be explicitly defined.

## Numerical Precision

Financial and quantity calculations should use BigDecimal.

A reference unit cost may require more precision than the purchase price itself.

Example:

BRL 89.90 / 1,000 g = BRL 0.0899/g

Prematurely rounding this value to BRL 0.09/g would change the resulting material cost.

The final implementation must define:

- Calculation precision.
- Division scale.
- Rounding mode.
- Monetary output scale.
- Rounding stage.

These rules must be consistent and covered by automated tests.

## Evaluation Criteria

Before accepting this ADR, the alternatives should be evaluated against the real business workflow.

The evaluation should answer:

1. What problem should the material cost reference solve?
2. Is the priority historical cost measurement, selling price formation, or both?
3. How frequently do filament prices change?
4. How frequently are new spools purchased?
5. Can the operator maintain a current reference price?
6. Does the business need detailed spool consumption tracking now?
7. How should quotations behave when material prices change?
8. Which policy provides understandable and reliable results without excessive operational work?

A small set of realistic product examples should be used to compare the alternatives.

## Decision

Pending.

No material costing policy has been approved.

The selected alternative, rationale, and rejected options will be recorded after evaluation.

## Consequences

Pending.

The positive and negative consequences of the selected policy will be documented once the decision is accepted.

## Implementation Guidance

Implementation should not begin with a hardcoded material costing policy.

The next steps are:

1. Review the business meaning of acquisition cost and replacement cost.
2. Compare candidate policies using realistic examples.
3. Confirm the information the business can reliably maintain.
4. Select the policy for new selling price calculations.
5. Define historical snapshot requirements.
6. Update this ADR to Accepted.
7. Design the necessary domain model changes.
8. Implement calculation logic with deterministic tests.

Any inventory valuation policy should be evaluated separately if inventory accounting becomes a concrete requirement.

## Related Documentation

- `docs/architecture/overview.md`
- `docs/architecture/domain-model.md`
- `docs/architecture/pricing.md`

## Decision Summary

Lovra must establish a consistent policy for determining the unit material cost used in selling price calculations.

The current alternatives include:

- Latest acquisition cost.
- Weighted average acquisition cost.
- FIFO inventory cost.
- Current replacement cost.

Replacement cost is a candidate for new quotation pricing, but it has not been selected.

Historical acquisition costs will remain preserved in FilamentSpool.

The final policy will be chosen after evaluating the alternatives against the actual business workflow.
