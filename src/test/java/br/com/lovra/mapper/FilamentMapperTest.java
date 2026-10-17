package br.com.lovra.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import br.com.lovra.dto.filament.CreateFilamentRequest;
import br.com.lovra.dto.filament.FilamentResponse;
import br.com.lovra.dto.filament.UpdateFilamentRequest;
import br.com.lovra.entity.Filament;
import br.com.lovra.entity.enumerations.FilamentType;

class FilamentMapperTest {

	private final FilamentMapper filamentMapper =
            Mappers.getMapper(FilamentMapper.class);

    @Test
    void shouldMapCreateFilamentRequestToFilament() {
		CreateFilamentRequest createFilamentRequest = new CreateFilamentRequest("PLA Black Silk",  "Voolt3D", "Black", FilamentType.PLA);
		Filament filament = filamentMapper.toFilament(createFilamentRequest);
		Assertions.assertEquals(createFilamentRequest.name(), filament.getName());
		Assertions.assertEquals(createFilamentRequest.brand(), filament.getBrand());
		Assertions.assertEquals(createFilamentRequest.color(), filament.getColor());
		Assertions.assertEquals(createFilamentRequest.filamentType(), filament.getFilamentType());
		Assertions.assertNull(filament.getId());
    }

    @Test
    void shouldMapFilamentToFilamentResponse() {
		Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
		FilamentResponse filamentResponse = filamentMapper.toFilamentResponse(filament);
		Assertions.assertEquals(filament.getName(), filamentResponse.name());
		Assertions.assertEquals(filament.getBrand(), filamentResponse.brand());
		Assertions.assertEquals(filament.getColor(), filamentResponse.color());
		Assertions.assertEquals(filament.getFilamentType(), filamentResponse.filamentType());
    }

    @Test
    void shouldUpdateExistingFilament() {
    	UpdateFilamentRequest updateFilamentRequest = new UpdateFilamentRequest("PLA Silver Silk",  "Voolt3D", "Silver", FilamentType.PETG);
    	Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
    	filamentMapper.updateFilament(updateFilamentRequest, filament);
		Assertions.assertEquals("PLA Silver Silk", filament.getName());
		Assertions.assertEquals("Voolt3D", filament.getBrand());
		Assertions.assertEquals("Silver", filament.getColor());
		Assertions.assertEquals(filament.getFilamentType(), FilamentType.PETG);
    }

}
