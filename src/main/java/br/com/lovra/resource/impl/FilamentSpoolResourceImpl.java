package br.com.lovra.resource.impl;

import java.net.URI;
import java.util.UUID;

import br.com.lovra.dto.filamentspool.CreateFilamentSpoolRequest;
import br.com.lovra.dto.filamentspool.FilamentSpoolResponse;
import br.com.lovra.dto.filamentspool.UpdateFilamentSpoolRequest;
import br.com.lovra.resource.FilamentSpoolResource;
import br.com.lovra.service.FilamentSpoolService;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Authenticated
@ApplicationScoped
public class FilamentSpoolResourceImpl implements FilamentSpoolResource {
	
	private final FilamentSpoolService filamentSpoolService;
	
	@Context
    private UriInfo uriInfo;

	public FilamentSpoolResourceImpl(FilamentSpoolService filamentSpoolService) {
		this.filamentSpoolService = filamentSpoolService;
	}

	@Override
	public Response create(UUID filamentId, CreateFilamentSpoolRequest createFilamentSpoolRequest) {
		FilamentSpoolResponse filamentSpool = this.filamentSpoolService.create(filamentId, createFilamentSpoolRequest);
		URI location = uriInfo.getBaseUriBuilder()
	            .path("api")
	            .path("filament-spools")
	            .path(filamentSpool.id().toString())
	            .build();

	        return Response.created(location).entity(filamentSpool).build();
	}

	@Override
	public Response findById(UUID id) {
		return Response.status(Response.Status.OK)
				.entity(this.filamentSpoolService.findById(id))
				.build();
	}

	@Override
	public Response findAllByFilamentId(UUID filamentId, int page, int size) {
		return Response.status(Response.Status.OK)
				.entity(this.filamentSpoolService.findAllByFilamentId(filamentId, page, size))
				.build();
	}

	@Override
	public Response update(UUID id, UpdateFilamentSpoolRequest updateFilamentSpoolRequest) {
		this.filamentSpoolService.update(id, updateFilamentSpoolRequest);
		return Response.noContent().build();
	}

	@Override
	public Response delete(UUID id) {
		this.filamentSpoolService.delete(id);
		return Response.noContent().build();
	}
	
}
