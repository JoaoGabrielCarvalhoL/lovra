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
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Authenticated
@ApplicationScoped
@Path("/api/filaments")
@Produces(MediaType.APPLICATION_JSON)
public class FilamentResourceImpl implements FilamentResource {

	private final FilamentService filamentService;
	private final FilamentSpoolSubResourceImpl filamentSpoolSubResource;
	
	@Context
    private UriInfo uriInfo;
	
	public FilamentResourceImpl(FilamentService filamentService, FilamentSpoolSubResourceImpl filamentSpoolSubResource) {
		this.filamentService = filamentService;
		this.filamentSpoolSubResource = filamentSpoolSubResource;
	}

	@POST
    @Consumes(MediaType.APPLICATION_JSON)
	@Override
	public Response create(CreateFilamentRequest createFilamentRequest) {
		FilamentResponse filament = filamentService.create(createFilamentRequest);
        URI location = uriInfo.getAbsolutePathBuilder().path(filament.id().toString()).build();
        return Response.created(location).entity(filament).build();
	}

	@GET
    @Path("/{id}")
	@Override
	public Response findById(@PathParam("id") UUID id) {
		return Response.status(Response.Status.OK).entity(this.filamentService.findById(id)).build();
	}

	@GET
	@Override
	public Response findAll(@QueryParam("page") @DefaultValue("0") int page,
    	    @QueryParam("size") @DefaultValue("20") int size) {
		return Response.status(Response.Status.OK).entity(this.filamentService.findAll(page, size)).build();
	}

	@PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
	@Override
	public Response update(@PathParam("id") UUID id, UpdateFilamentRequest updateFilamentRequest) {
		this.filamentService.update(id, updateFilamentRequest);
		return Response.noContent().build();
	}

	@DELETE
    @Path("/{id}")
	@Override
	public Response delete(@PathParam("id") UUID id) {
		this.filamentService.delete(id);
		return Response.noContent().build();
	}
	
	@Override
	@Path("/{filamentId}/spools")
	public FilamentSpoolSubResourceImpl getSpoolsResource(@PathParam("filamentId") UUID filamentId) {
		return this.filamentSpoolSubResource;
	}
	
	
	
}