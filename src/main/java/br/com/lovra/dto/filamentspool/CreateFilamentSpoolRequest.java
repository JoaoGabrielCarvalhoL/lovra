package br.com.lovra.dto.filamentspool;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

public record CreateFilamentSpoolRequest(
    @NotNull(message = "The field purchasePrice cannot be null.")
    @Positive(message = "The purchasePrice field only accepts positive values.")
    BigDecimal purchasePrice,

    @NotNull(message = "The field initialWeightInGrams cannot be null.")
    @Positive@Positive(message = "The initialWeightInGrams field only accepts positive values.")
    BigDecimal initialWeightInGrams,

    @NotNull(message = "The field purchasedAt cannot be null.")
    @PastOrPresent(message = "The purchasedAt field only accepts dates in the present and past tenses.")
    LocalDate purchasedAt

) {}