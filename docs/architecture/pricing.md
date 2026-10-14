
# Lovra — Pricing Model

## 1. Purpose

This document describes the business concepts, calculation requirements, and unresolved decisions related to production costing and selling price formation in Lovra.

Its objectives are to:

- Establish a consistent vocabulary for costing and pricing.
- Identify the economic inputs required to estimate production costs.
- Distinguish acquisition costs from pricing reference costs.
- Evaluate alternative material costing policies.
- Explain markup, margin, profit, and loss allowances.
- Define requirements for preserving historical pricing calculations.
- Support future domain modeling and implementation.

This document is a working specification.

Unless explicitly marked as accepted, calculation policies and examples are illustrative and must not be treated as implemented business rules.

## 2. Business Context

Lovra supports a small-scale 3D printing operation.

Products are generally printed after a customer has expressed interest and a selling price has been agreed upon.

The current operation uses information provided by Bambu Lab software, including estimated printing time and filament consumption.

Pricing information is currently maintained through spreadsheets and manual calculations.

The business needs a more consistent way to estimate production costs and determine selling prices.

The system should reduce the risk of underpricing products and make the assumptions behind each price visible.

## 3. Pricing Objectives

The future pricing functionality should:

1. Estimate the cost of producing a physical item.
2. Account for relevant materials and production resources.
3. Support configurable economic assumptions.
4. Calculate a suggested selling price.
5. Allow the business operator to review the calculation.
6. Preserve the values used in historical quotations and orders.
7. Distinguish estimated costs from actual expenses when applicable.
8. Support products already registered in the catalog and custom requests.

The system should not assume that the suggested price must always become the final selling price.

The rules for manual price adjustments and their justification remain to be defined.

## 4. Fundamental Concepts

### 4.1 Acquisition Cost

Acquisition cost represents the amount paid to obtain a resource.

For filament, the current model records:

- Purchase price.
- Initial weight.
- Purchase date.

Example:

A 1,000 g filament spool purchased for BRL 80.00 has a unit acquisition cost of:

80.00 / 1,000 = BRL 0.08/g

This is the historical unit cost of that acquisition.

It does not necessarily represent the cost of purchasing the same material today.

### 4.2 Replacement Cost

Replacement cost represents the amount required to acquire an equivalent resource under current purchasing conditions.

Example:

A filament spool originally purchased for BRL 80.00 may currently cost BRL 110.00.

Its current replacement unit cost would be:

110.00 / 1,000 = BRL 0.11/g

Replacement cost can be useful for pricing decisions because future production may require purchasing material at current prices.

However, replacement cost is not necessarily the historical cost of material already held in inventory.

### 4.3 Production Cost

Production cost represents the economic resources consumed or allocated to producing an item.

Potential components include:

- Filament consumption.
- Electricity.
- Machine depreciation or wear.
- Direct labor.
- Packaging.
- Expected production losses.

The exact composition of production cost remains to be defined.

### 4.4 Selling Price

Selling price represents the amount charged to the customer.

It is distinct from production cost.

The selling price may account for production costs, operating expenses, taxes, payment fees, profit targets, and commercial adjustments.

Not every component is necessarily applicable to the current Lovra operation.

### 4.5 Profit

In simplified examples:

Profit = Selling Price - Total Relevant Costs and Expenses

The meaning of profit depends on which costs and expenses are included.

A calculation that subtracts only filament cost does not represent the complete economic profit of a sale.

### 4.6 Markup

Markup expresses an increase relative to a cost base.

For a cost of BRL 50.00 and a markup of 40%:

Selling Price = 50.00 × (1 + 0.40)

Selling Price = BRL 70.00

The resulting profit relative to the simplified cost base is BRL 20.00.

### 4.7 Profit Margin

Profit margin expresses profit relative to the selling price.

For the previous example:

Profit Margin = (70.00 - 50.00) / 70.00

Profit Margin ≈ 28.57%

Therefore:

A 40% markup does not produce a 40% profit margin.

For a target margin of 40%, assuming the cost base includes all relevant costs and there are no additional charges:

Selling Price = Cost / (1 - Target Margin)

Selling Price = 50.00 / (1 - 0.40)

Selling Price ≈ BRL 83.33

These formulas are simplified educational examples.

The final pricing formula must account for the business's actual cost and expense structure.

## 5. Material Costing

### 5.1 Available Domain Data

The current domain model contains:

Filament:
- Material identity.
- Brand.
- Name.
- Color.
- Material type.

FilamentSpool:
- Purchase price.
- Initial weight.
- Purchase date.

ProductMaterial:
- Product reference.
- Filament reference.
- Required weight in grams.

ProductMaterial references Filament rather than an individual FilamentSpool.

Therefore, pricing must determine which unit material cost to apply.

### 5.2 Basic Material Cost Formula

For a single material:

Unit Material Cost = Reference Material Cost / Reference Material Weight

Material Cost = Required Weight × Unit Material Cost

For multiple materials:

Total Material Cost = Sum of Individual Material Costs

Example:

A product requires 82.40 g of filament.

Reference cost: BRL 100.00 per 1,000 g.

Unit cost:

100.00 / 1,000 = BRL 0.10/g

Material cost:

82.40 × 0.10 = BRL 8.24

This formula is independent of the policy used to determine the reference material cost.

## 6. Candidate Material Costing Policies

The following policies are candidates for evaluation.

No policy has been formally selected.

### 6.1 Latest Acquisition Cost

Uses the unit acquisition cost from the most recent relevant spool purchase.

Advantages:

- Simple to understand.
- Uses recorded purchase information.
- Requires relatively little additional data.

Limitations:

- The latest purchase may not reflect the current market price.
- Temporary promotions may distort the reference.
- A single unusual purchase may significantly change the calculated cost.

### 6.2 Weighted Average Acquisition Cost

Calculates a weighted average using purchase quantities and acquisition costs.

Simplified example:

Spool A:
- 1,000 g
- BRL 100.00

Spool B:
- 1,000 g
- BRL 130.00

Weighted average unit cost:

(100.00 + 130.00) / (1,000 + 1,000)

= BRL 0.115/g

Advantages:

- Reduces sensitivity to individual purchase prices.
- Can represent a blended acquisition cost.

Limitations:

- The average may differ from current replacement cost.
- Accurate inventory valuation requires an appropriate stock accounting policy.
- A weighted average of purchases is not automatically equivalent to the current weighted-average inventory cost when consumption has occurred.

### 6.3 FIFO

FIFO means First In, First Out.

Under a FIFO inventory costing policy, earlier inventory acquisition costs are assigned to consumption before later acquisition costs.

Advantages:

- Provides an established approach for allocating historical inventory costs.
- Can support inventory valuation and cost-of-goods-sold calculations.

Limitations:

- Requires reliable inventory movements and quantity tracking.
- Introduces additional operational complexity.
- Does not necessarily represent the current replacement cost used for a new quotation.

FIFO inventory costing should not be confused with physically selecting a particular spool for printing.

### 6.4 Replacement Cost

Uses an estimated current cost of acquiring equivalent filament.

Advantages:

- Reflects current purchasing conditions.
- Can help form prices that support future material replenishment.
- Does not depend exclusively on old acquisition prices.

Limitations:

- Requires a reasonably current and reliable reference price.
- Historical purchase records alone may not provide this information.
- Market prices, discounts, shipping, and supplier availability can affect the reference.

### 6.5 Preliminary Direction

Replacement cost is a candidate for selling price formation.

Actual acquisition costs should remain preserved in FilamentSpool records.

This is a working hypothesis, not an accepted policy.

The application must distinguish historical acquisition data from any current pricing reference introduced later.

## 7. Electricity Cost

Electricity cost estimates the energy expense associated with producing an item.

A simplified formula is:

Energy Consumption (kWh) = Average Power (W) × Duration (h) / 1,000

Electricity Cost = Energy Consumption (kWh) × Electricity Tariff (BRL/kWh)

Example:

Average power: 120 W

Printing duration: 5 hours

Electricity tariff: BRL 0.90/kWh

Energy consumption:

120 × 5 / 1,000 = 0.60 kWh

Electricity cost:

0.60 × 0.90 = BRL 0.54

This example assumes a constant average power value.

Actual consumption may vary throughout the printing process.

The source and maintenance of average power assumptions must be defined.

## 8. Machine Depreciation and Wear

The use of a 3D printer has an economic cost beyond electricity and filament.

A possible approach is to allocate an estimated machine cost over its expected productive lifetime.

Simplified formula:

Machine Cost per Hour = Depreciable Machine Value / Estimated Productive Hours

Machine Cost per Product = Machine Cost per Hour × Printing Duration

Example:

Depreciable machine value: BRL 3,000.00

Estimated productive lifetime: 3,000 hours

Machine cost per hour:

3,000.00 / 3,000 = BRL 1.00/h

For a five-hour print:

Machine cost = BRL 5.00

This is a simplified allocation model.

It does not automatically cover maintenance, repairs, replacement parts, downtime, or financing costs.

The distinction between depreciation and maintenance reserves must be addressed before implementing the final calculation.

## 9. Labor Cost

Labor cost represents the value assigned to human work associated with production.

Potential activities include:

- Preparing the model.
- Configuring the print.
- Preparing the printer.
- Removing the completed piece.
- Cleaning or finishing.
- Packaging.

A simplified formula is:

Labor Cost = Active Labor Hours × Labor Rate per Hour

Example:

Active labor: 30 minutes

Labor rate: BRL 20.00/hour

Labor cost:

0.5 × 20.00 = BRL 10.00

Printing duration and active labor duration must be treated as different quantities.

A printer operating unattended for five hours does not necessarily imply five hours of direct labor.

The appropriate labor rate remains a business decision.

## 10. Packaging Cost

Packaging cost represents materials used to prepare the product for delivery.

Potential examples include:

- Boxes.
- Bags.
- Protective materials.
- Labels.

The initial implementation may use a standard packaging cost when appropriate.

Product-specific packaging requirements may require different values.

No fixed packaging policy has been approved.

## 11. Production Losses

3D printing may involve unsuccessful prints, wasted filament, and other production losses.

Potential causes include:

- Adhesion failures.
- Printing defects.
- Incorrect settings.
- Material problems.
- Operational interruptions.

Loss allowances may be considered when forming prices.

### 11.1 Percentage Reserve

One possible simplified approach is:

Loss Reserve = Eligible Cost Base × Loss Rate

Example:

Eligible cost base: BRL 40.00

Loss rate: 5%

Loss reserve:

40.00 × 0.05 = BRL 2.00

The eligible cost base must be explicitly defined.

Applying the percentage to material cost alone produces a different result from applying it to total production cost.

### 11.2 Expected Loss Cost

A more detailed approach could estimate expected losses from production failure probabilities and recovery costs.

This approach requires reliable operational data that Lovra does not currently maintain.

### 11.3 Pending Decisions

- Whether a loss reserve is required.
- Which costs form its calculation base.
- How the reserve percentage is established.
- Whether actual production losses should be recorded separately.

An estimated loss reserve must not be confused with an actual recorded production failure.

## 12. Simplified Production Cost Example

The following example combines hypothetical values.

Product: Example 3D Printed Figure

Material:
- 300 g
- BRL 0.11/g
- Cost: BRL 33.00

Electricity:
- 120 W average power
- 5 hours
- BRL 0.90/kWh
- Cost: BRL 0.54

Machine allocation:
- BRL 1.00/hour
- 5 hours
- Cost: BRL 5.00

Labor:
- 0.5 hours
- BRL 20.00/hour
- Cost: BRL 10.00

Packaging:
- BRL 3.00

Subtotal:

33.00 + 0.54 + 5.00 + 10.00 + 3.00

= BRL 51.54

Illustrative loss reserve:

5% of subtotal

51.54 × 0.05 = BRL 2.577

Rounded to two decimal places:

BRL 2.58

Illustrative total:

51.54 + 2.58 = BRL 54.12

This total excludes other possible business expenses, taxes, sales fees, and profit.

It is not a validated Lovra production cost formula.

## 13. Suggested Selling Price

The future pricing engine may calculate a suggested selling price from a defined cost base and pricing policy.

The system must distinguish between:

- Calculated production cost.
- Minimum acceptable selling price, if defined.
- Suggested selling price.
- Final agreed selling price.

A suggested price is not necessarily the price charged to the customer.

The business operator may need to apply commercial adjustments.

The following rules remain undefined:

- Target margin.
- Markup policy.
- Minimum margin.
- Price rounding.
- Discounts.
- Taxes and selling fees.
- Manual price override restrictions.

The implementation must not assume that markup and margin are interchangeable.

## 14. Historical Pricing Snapshots

### 14.1 Requirement

Historical commercial calculations must remain stable.

Changes to current material prices, electricity tariffs, labor rates, or other configuration values must not silently modify previously issued quotations or confirmed orders.

### 14.2 Snapshot Concept

A pricing snapshot represents the economic inputs and calculation results used at a particular moment.

Potential snapshot information includes:

- Material identities and quantities.
- Material unit costs used.
- Material costing policy.
- Electricity assumptions.
- Machine cost assumptions.
- Labor assumptions.
- Packaging costs.
- Loss allowances.
- Pricing margin or markup.
- Calculated totals.
- Final selling price.
- Calculation timestamp.

The exact schema has not yet been defined.

### 14.3 Example

A quotation created in October uses:

- Material unit cost: BRL 0.10/g
- Electricity tariff: BRL 0.90/kWh
- Labor rate: BRL 20.00/hour

A later quotation uses:

- Material unit cost: BRL 0.13/g
- Electricity tariff: BRL 1.05/kWh
- Labor rate: BRL 25.00/hour

Both calculations may be valid for their respective dates.

The historical quotation must retain its original values.

### 14.4 Product Independence

Product describes a reproducible item.

Pricing describes its economic valuation under particular conditions.

Therefore, Product must not rely on a single mutable selling price as the authoritative record of all historical commercial transactions.

## 15. Quotations and Orders

### 15.1 Quotations

A quotation represents a commercial offer made to a customer.

It should preserve the relevant pricing calculation and agreed commercial conditions.

A quotation may be created for a request that has not yet become a registered Product.

The application is expected to support:

- An internal quotation representation containing calculation details.
- A customer-facing quotation representation containing appropriate commercial information.

These are different presentations of commercial data, not necessarily different quotation entities.

### 15.2 Orders

An order represents a confirmed commercial transaction.

An order may originate from an existing quotation.

An order may also be created directly without a formal quotation.

In either case, the commercial values used for the transaction must be preserved.

### 15.3 Relationship to Pricing

Pricing calculations should be reusable across different commercial workflows.

However, the existence of a pricing calculation does not necessarily imply that a quotation or order has been created.

The exact ownership and persistence of pricing snapshots remain to be designed.

## 16. Numerical Precision and Rounding

### 16.1 Decimal Representation

Financial calculations should use BigDecimal rather than binary floating-point types such as float or double.

Material weights and unit costs may require fractional precision.

### 16.2 Intermediate Precision

Intermediate calculations should preserve sufficient precision to avoid unnecessary accumulated rounding errors.

For example, a material unit cost may contain more than two decimal places even when purchase prices are recorded with two decimal places.

### 16.3 Monetary Rounding

The final monetary rounding policy must be explicit.

Potential decisions include:

- Scale used for intermediate calculations.
- RoundingMode used for monetary results.
- Stage at which rounding occurs.
- Rounding of individual components versus totals.
- Commercial rounding of suggested selling prices.

The illustrative examples in this document use two decimal places for displayed monetary amounts.

They do not establish the final implementation policy.

## 17. Proposed Calculation Workflow

A possible future workflow is:

1. Identify the item to be priced.
2. Determine the required materials and quantities.
3. Resolve the applicable material cost references.
4. Estimate electricity consumption.
5. Calculate machine cost allocation.
6. Calculate active labor cost.
7. Include packaging costs.
8. Apply the defined loss allowance policy.
9. Calculate the production cost.
10. Apply the approved selling price formation policy.
11. Present the calculation for review.
12. Preserve the calculation snapshot when required by a commercial operation.

This workflow is conceptual.

The order and responsibilities of implementation components may change as the domain model evolves.

## 18. Open Decisions

The following decisions must be resolved before implementing the pricing engine:

| ID | Decision | Status |
|---|---|---|
| PR-001 | Material costing policy for selling price formation | Pending |
| PR-002 | Source and update strategy for replacement prices | Pending |
| PR-003 | Electricity cost assumptions | Pending |
| PR-004 | Machine depreciation and maintenance allocation | Pending |
| PR-005 | Labor cost policy | Pending |
| PR-006 | Packaging cost policy | Pending |
| PR-007 | Production loss allowance | Pending |
| PR-008 | Markup or target margin strategy | Pending |
| PR-009 | Minimum acceptable selling price | Pending |
| PR-010 | Monetary rounding and commercial rounding | Pending |
| PR-011 | Historical pricing snapshot structure | Pending |
| PR-012 | Manual price adjustment policy | Pending |
| PR-013 | Taxes, platform fees, and payment fees | Pending |

The material costing decision will be documented in:

`docs/adr/0004-material-costing-policy.md`

That ADR must remain pending until the alternatives have been evaluated and a policy has been selected.

## 19. Validation Strategy

Once the pricing rules are approved, the implementation should be verified using deterministic calculation tests.

Test scenarios should include:

- Products containing one filament.
- Products containing multiple filaments.
- Different material acquisition prices.
- Fractional material quantities.
- Zero or invalid input values.
- Changes in economic configuration.
- Rounding boundaries.
- Historical snapshot stability.
- Manual selling price adjustments.
- Direct orders and quotations.

Tests should validate business results independently of persistence and HTTP transport where practical.

## 20. Document Maintenance

This document must evolve as pricing rules are studied, validated, and implemented.

Approved policies should be reflected consistently in:

- Domain entities.
- Application services.
- Calculation tests.
- API contracts.
- User-facing explanations.
- Architectural Decision Records.

Unresolved assumptions must remain explicitly identified as pending.

The current domain model is documented in `domain-model.md`.

The high-level system architecture is documented in `overview.md`.
