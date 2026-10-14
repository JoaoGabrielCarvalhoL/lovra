package br.com.lovra.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface FilamentCostService {
	
	BigDecimal calculateAverageCostPerGram(UUID id);

}
