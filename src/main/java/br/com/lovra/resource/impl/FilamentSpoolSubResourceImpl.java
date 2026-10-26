package br.com.lovra.resource.impl;

import java.net.URI;
import java.util.UUID;

import br.com.lovra.dto.filamentspool.CreateFilamentSpoolRequest;
import br.com.lovra.dto.filamentspool.FilamentSpoolResponse;
import br.com.lovra.resource.FilamentSpoolSubResource;
import br.com.lovra.service.FilamentSpoolService;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Authenticated
@ApplicationScoped
public class FilamentSpoolSubResourceImpl implements FilamentSpoolSubResource {

	private final FilamentSpoolService filamentSpoolService;

	@Context
	private UriInfo uriInfo;

	public FilamentSpoolSubResourceImpl(FilamentSpoolService filamentSpoolService) {
		this.filamentSpoolService = filamentSpoolService;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Override
	public Response create(@PathParam("filamentId") UUID filamentId, CreateFilamentSpoolRequest createFilamentSpoolRequest) {
		FilamentSpoolResponse filamentSpool = this.filamentSpoolService.create(filamentId, createFilamentSpoolRequest);
		URI location = uriInfo.getBaseUriBuilder()
				.path("api")
				.path("spools")
				.path(filamentSpool.id().toString())
				.build();

		return Response.created(location).entity(filamentSpool).build();
	}

	@GET
	@Override
	public Response findAllByFilamentId(@PathParam("filamentId") UUID filamentId, 
			@QueryParam("page") @DefaultValue("0") int page,  
			@QueryParam("size") @DefaultValue("20") int size) {
		return Response.status(Response.Status.OK)
				.entity(this.filamentSpoolService.findAllByFilamentId(filamentId, page, size))
				.build();
	}
}
