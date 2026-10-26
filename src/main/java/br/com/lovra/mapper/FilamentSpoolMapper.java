package br.com.lovra.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import br.com.lovra.dto.filamentspool.CreateFilamentSpoolRequest;
import br.com.lovra.dto.filamentspool.FilamentSpoolResponse;
import br.com.lovra.dto.filamentspool.UpdateFilamentSpoolRequest;
import br.com.lovra.entity.FilamentSpool;

@Mapper(componentModel = "cdi", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface FilamentSpoolMapper {
	
	@Mapping(target = "id", ignore = true)
    @Mapping(target = "filament", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "remainingWeightInGrams", ignore = true)
    FilamentSpool toFilamentSpool(CreateFilamentSpoolRequest request);

    @Mapping(target = "filamentId", source = "filament.id")
    FilamentSpoolResponse toFilamentSpoolResponse(FilamentSpool filamentSpool);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "filament", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateFilamentSpool(UpdateFilamentSpoolRequest request, @MappingTarget FilamentSpool filamentSpool);

}
