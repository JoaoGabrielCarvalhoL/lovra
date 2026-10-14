package br.com.lovra.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.lovra.entity.Filament;
import br.com.lovra.entity.FilamentSpool;
import br.com.lovra.entity.enumerations.FilamentType;
import br.com.lovra.exception.FilamentCostUnavailableException;
import br.com.lovra.exception.FilamentNotFoundException;
import br.com.lovra.repository.FilamentRepository;
import br.com.lovra.repository.FilamentSpoolRepository;

@ExtendWith(MockitoExtension.class)
class FilamentCostServiceImplTest {
	
	@InjectMocks
	private FilamentCostServiceImpl filamentCostServiceImpl;
	
	@Mock
	private FilamentRepository filamentRepository;
	
	@Mock
	private FilamentSpoolRepository filamentSpoolRepository;
	
	@Test
	void shouldCalculateAverageCostForSingleSpool() {
		
		Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
		List<FilamentSpool> filamentSpools = List.of(new FilamentSpool(filament, new BigDecimal("100.00"), new BigDecimal("1000.00"), LocalDate.now()));
		
		UUID id = UUID.randomUUID();
		BigDecimal expected = new BigDecimal("0.100000");
		
		Mockito.when(filamentRepository.findByIdOptional(id)).thenReturn(Optional.of(filament));
		
		Mockito.when(filamentSpoolRepository.findByFilamentId(id)).thenReturn(filamentSpools);
		
		BigDecimal average = filamentCostServiceImpl.calculateAverageCostPerGram(id);
		
		Assertions.assertEquals(expected, average);
		Mockito.verify(filamentRepository).findByIdOptional(id);
		Mockito.verify(filamentSpoolRepository).findByFilamentId(id);
		
	}
	
	@Test
	void shouldCalculateAverageCostForMultipleSpools() {
		Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
		List<FilamentSpool> filamentSpools = List.of(new FilamentSpool(filament, new BigDecimal("100.00"), new BigDecimal("1000.00"), LocalDate.of(2026, 9, 1)), 
				new FilamentSpool(filament, new BigDecimal("120.00"), new BigDecimal("1000.00"), LocalDate.of(2026, 10, 1)));
		
		UUID id = UUID.randomUUID();
		BigDecimal expected = new BigDecimal("0.110000");
		
		Mockito.when(filamentRepository.findByIdOptional(id)).thenReturn(Optional.of(filament));
		
		Mockito.when(filamentSpoolRepository.findByFilamentId(id)).thenReturn(filamentSpools);
		
		BigDecimal average = filamentCostServiceImpl.calculateAverageCostPerGram(id);
		
		Assertions.assertEquals(expected, average);
		Mockito.verify(filamentRepository).findByIdOptional(id);
		Mockito.verify(filamentSpoolRepository).findByFilamentId(id);
	}
	
	@Test
	void shouldCalculateWeightedAverageCost() {
		Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
		List<FilamentSpool> filamentSpools = List.of(new FilamentSpool(filament, new BigDecimal("100.00"), new BigDecimal("1000.00"), LocalDate.of(2026, 9, 1)), 
				new FilamentSpool(filament, new BigDecimal("80.00"), new BigDecimal("500.00"), LocalDate.of(2026, 10, 1)));
		
		UUID id = UUID.randomUUID();
		BigDecimal expected = new BigDecimal("0.120000");
		
		Mockito.when(filamentRepository.findByIdOptional(id)).thenReturn(Optional.of(filament));
		
		Mockito.when(filamentSpoolRepository.findByFilamentId(id)).thenReturn(filamentSpools);
		
		BigDecimal average = filamentCostServiceImpl.calculateAverageCostPerGram(id);
		
		Assertions.assertEquals(expected, average);
		Mockito.verify(filamentRepository).findByIdOptional(id);
		Mockito.verify(filamentSpoolRepository).findByFilamentId(id);
	}
	
	@Test
	void shouldThrowWhenFilamentDoesNotExist() {
		UUID id = UUID.randomUUID();
		Mockito.when(filamentRepository.findByIdOptional(id)).thenReturn(Optional.empty());
		Assertions.assertThrows(FilamentNotFoundException.class, () -> {
			filamentCostServiceImpl.calculateAverageCostPerGram(id);
		});
		Mockito.verify(filamentRepository).findByIdOptional(id);
	}
	
	@Test
	void shouldThrowWhenFilamentHasNoSpools() {
		UUID id = UUID.randomUUID();
		Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
		Mockito.when(filamentRepository.findByIdOptional(id)).thenReturn(Optional.of(filament));
		Mockito.when(filamentSpoolRepository.findByFilamentId(id)).thenReturn(Collections.emptyList());
		Assertions.assertThrows(FilamentCostUnavailableException.class, () -> {
			filamentCostServiceImpl.calculateAverageCostPerGram(id);
		});
		Mockito.verify(filamentRepository).findByIdOptional(id);
		Mockito.verify(filamentSpoolRepository).findByFilamentId(id);
	}
	
	@Test
	void shouldRoundAverageCostToSixDecimalPlaces() {
		Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
		List<FilamentSpool> filamentSpools = List.of(new FilamentSpool(filament, new BigDecimal("100.00"), new BigDecimal("300.00"), LocalDate.of(2026,10,1)));
		
		UUID id = UUID.randomUUID();
		BigDecimal expected = new BigDecimal("0.333333");
		
		Mockito.when(filamentRepository.findByIdOptional(id)).thenReturn(Optional.of(filament));
		
		Mockito.when(filamentSpoolRepository.findByFilamentId(id)).thenReturn(filamentSpools);
		
		BigDecimal average = filamentCostServiceImpl.calculateAverageCostPerGram(id);
		
		Assertions.assertEquals(expected, average);
		Mockito.verify(filamentRepository).findByIdOptional(id);
		Mockito.verify(filamentSpoolRepository).findByFilamentId(id);
	}
	
	void shouldRoundUpAverageCostWhenSeventhDecimalIsGreaterThanFive() {
		Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
		List<FilamentSpool> filamentSpools = List.of(new FilamentSpool(filament, new BigDecimal("100.00"), new BigDecimal("600.00"), LocalDate.of(2026,10,1)));
		
		UUID id = UUID.randomUUID();
		BigDecimal expected = new BigDecimal("0.166667");
		
		Mockito.when(filamentRepository.findByIdOptional(id)).thenReturn(Optional.of(filament));
		
		Mockito.when(filamentSpoolRepository.findByFilamentId(id)).thenReturn(filamentSpools);
		
		BigDecimal average = filamentCostServiceImpl.calculateAverageCostPerGram(id);
		
		Assertions.assertEquals(expected, average);
		Mockito.verify(filamentRepository).findByIdOptional(id);
		Mockito.verify(filamentSpoolRepository).findByFilamentId(id);
	}

}
