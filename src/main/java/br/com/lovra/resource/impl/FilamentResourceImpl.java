package br.com.lovra.resource.impl;

import java.net.URI;
import java.util.UUID;

import br.com.lovra.dto.filament.CreateFilamentRequest;
import br.com.lovra.dto.filament.FilamentResponse;
import br.com.lovra.dto.filament.UpdateFilamentRequest;
import br.com.lovra.resource.FilamentResource;
import br.com.lovra.service.FilamentService;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Authenticated
@ApplicationScoped
public class FilamentResourceImpl implements FilamentResource {

	private final FilamentService filamentService;
	
	@Context
    UriInfo uriInfo;
	
	public FilamentResourceImpl(FilamentService filamentService) {
		this.filamentService = filamentService;
	}

	@Override
	public Response create(@Valid CreateFilamentRequest createFilamentRequest) {
		FilamentResponse filament = filamentService.create(createFilamentRequest);

        URI location = uriInfo.getAbsolutePathBuilder()
            .path(filament.id().toString())
            .build();

        return Response.created(location).entity(filament).build();
	}

	@Override
	public Response findById(UUID id) {
		return Response.status(Response.Status.OK)
				.entity(this.filamentService.findById(id))
				.build();
	}

	@Override
	public Response findAll(int page, int size) {
		return Response.status(Response.Status.OK)
				.entity(this.filamentService.findAll(page, size))
				.build();
	}

	@Override
	public Response update(UUID filamentId, @Valid UpdateFilamentRequest updateFilamentRequest) {
		this.filamentService.update(filamentId, updateFilamentRequest);
		return Response.noContent().build();
	}

}
