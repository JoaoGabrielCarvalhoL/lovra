package br.com.lovra.dto.filamentspool;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FilamentSpoolResponse(
		UUID id,
	    UUID filamentId,
	    BigDecimal purchasePrice,
	    BigDecimal initialWeightInGrams,
	    LocalDate purchasedAt) {

}
