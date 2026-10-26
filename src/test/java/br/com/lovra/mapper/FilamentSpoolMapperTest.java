package br.com.lovra.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import br.com.lovra.dto.filamentspool.CreateFilamentSpoolRequest;
import br.com.lovra.dto.filamentspool.FilamentSpoolResponse;
import br.com.lovra.dto.filamentspool.UpdateFilamentSpoolRequest;
import br.com.lovra.entity.Filament;
import br.com.lovra.entity.FilamentSpool;
import br.com.lovra.entity.enumerations.FilamentType;

class FilamentSpoolMapperTest {

	private final FilamentSpoolMapper filamentSpoolMapper =
            Mappers.getMapper(FilamentSpoolMapper.class);

    @Test
    void shouldMapCreateFilamentSpoolRequestToFilamentSpool() {
    	CreateFilamentSpoolRequest createFilamentSpoolRequest = new CreateFilamentSpoolRequest(new BigDecimal("100.00"), new BigDecimal("100.00"), LocalDate.of(2026, 9,1));
    	FilamentSpool filamentSpool = filamentSpoolMapper.toFilamentSpool(createFilamentSpoolRequest);
    	Assertions.assertEquals(createFilamentSpoolRequest.purchasePrice(), filamentSpool.getPurchasePrice());
    	Assertions.assertEquals(createFilamentSpoolRequest.initialWeightInGrams(), filamentSpool.getInitialWeightInGrams());
    	Assertions.assertEquals(createFilamentSpoolRequest.purchasedAt(), filamentSpool.getPurchasedAt());
    	Assertions.assertNull(filamentSpool.getFilament());
    }

    @Test
    void shouldMapFilamentSpoolToFilamentSpoolResponse() {
    	Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
    	FilamentSpool filamentSpool = new FilamentSpool(filament, new BigDecimal("100.00"), new BigDecimal("100.00"), LocalDate.of(2026, 9,1));
    	FilamentSpoolResponse filamentSpoolResponse = filamentSpoolMapper.toFilamentSpoolResponse(filamentSpool);
    	Assertions.assertEquals(filamentSpool.getPurchasePrice(), filamentSpoolResponse.purchasePrice());
    	Assertions.assertEquals(filamentSpool.getInitialWeightInGrams(), filamentSpoolResponse.initialWeightInGrams());
    	Assertions.assertEquals(filamentSpool.getPurchasedAt(), filamentSpoolResponse.purchasedAt());
    	Assertions.assertEquals(filament.getId(), filamentSpoolResponse.filamentId());
    }

    @Test
    void shouldUpdateExistingFilamentSpool() {
    	UpdateFilamentSpoolRequest updateFilamentSpoolRequest = new UpdateFilamentSpoolRequest(new BigDecimal("110.00"), new BigDecimal("120.00"), LocalDate.of(2026, 10,1), new BigDecimal("120.00"));
    	Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
    	FilamentSpool filamentSpool = new FilamentSpool(filament, new BigDecimal("100.00"), new BigDecimal("100.00"), LocalDate.of(2026, 9,1));
    	this.filamentSpoolMapper.updateFilamentSpool(updateFilamentSpoolRequest, filamentSpool);
		Assertions.assertEquals( new BigDecimal("110.00"), filamentSpool.getPurchasePrice());
		Assertions.assertEquals(new BigDecimal("120.00"), filamentSpool.getInitialWeightInGrams());
		Assertions.assertEquals(LocalDate.of(2026, 10,1), filamentSpool.getPurchasedAt());
    }

}
