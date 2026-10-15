package br.com.lovra.dto.filament;

import br.com.lovra.entity.enumerations.FilamentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateFilamentRequest(
		@NotBlank(message = "The field name cannot be empty or blank.") 
		String name,
	    @NotBlank(message = "The field brand cannot be empty or blank.") 
		String brand,
	    @NotBlank(message = "The field color cannot be empty or blank.") 
		String color,
	    @NotNull(message = "The field filamentType cannot be null.") 
		FilamentType filamentType) {

}
