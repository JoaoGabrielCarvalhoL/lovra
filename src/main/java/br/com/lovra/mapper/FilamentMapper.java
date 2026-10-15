package br.com.lovra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import br.com.lovra.dto.filament.CreateFilamentRequest;
import br.com.lovra.dto.filament.FilamentResponse;
import br.com.lovra.dto.filament.UpdateFilamentRequest;
import br.com.lovra.entity.Filament;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface FilamentMapper {

	@Mapping(target = "id", ignore = true)
	Filament toFilament(CreateFilamentRequest createFilamentRequest);
	
	FilamentResponse toFilamentResponse(Filament filament);
	
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	void updateFilament(UpdateFilamentRequest updateFilamentRequest, @MappingTarget Filament filament);
}
