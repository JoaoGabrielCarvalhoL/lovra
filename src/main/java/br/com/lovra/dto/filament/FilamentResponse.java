package br.com.lovra.dto.filament;

import java.util.UUID;

import br.com.lovra.entity.enumerations.FilamentType;

public record FilamentResponse(
		UUID id,
	    String name,
	    String brand,
	    String color,
	    FilamentType filamentType) {

}
