package br.com.lovra.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.lovra.entity.FilamentSpool;
import br.com.lovra.exception.FilamentCostUnavailableException;
import br.com.lovra.exception.FilamentNotFoundException;
import br.com.lovra.repository.FilamentRepository;
import br.com.lovra.repository.FilamentSpoolRepository;
import br.com.lovra.service.FilamentCostService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FilamentCostServiceImpl implements FilamentCostService {
	
	private static final int COST_SCALE = 6;
    private static final RoundingMode COST_ROUNDING = RoundingMode.HALF_UP;
	
	private final FilamentSpoolRepository filamentSpoolRepository;
	private final FilamentRepository filamentRepository;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public FilamentCostServiceImpl(FilamentSpoolRepository filamentSpoolRepository, 
			FilamentRepository filamentRepository) {
		this.filamentSpoolRepository = filamentSpoolRepository;
		this.filamentRepository = filamentRepository;
	}

	@Override
	public BigDecimal calculateAverageCostPerGram(UUID id) {
		this.filamentRepository.findByIdOptional(id)
			.orElseThrow(() -> new FilamentNotFoundException("Filament not found with id: " + id)); 
		
		logger.info("Getting filament spolls by filament id: {}", id);
		List<FilamentSpool> filamentSpools = this.filamentSpoolRepository.findByFilamentId(id);
		
		if (filamentSpools.isEmpty()) {
			throw new FilamentCostUnavailableException("Generic Exception...EmptyList"); 
		}
		
		BigDecimal totalPrice = BigDecimal.ZERO;
		BigDecimal totalWeight = BigDecimal.ZERO;
		
		for (FilamentSpool filamentSpool : filamentSpools) {
			totalPrice = totalPrice.add(filamentSpool.getPurchasePrice());
			totalWeight = totalWeight.add(filamentSpool.getInitialWeightInGrams());
		}
		
		return totalPrice.divide(totalWeight, COST_SCALE, COST_ROUNDING);
	}
	

}
