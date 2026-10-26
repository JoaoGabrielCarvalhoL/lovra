package br.com.lovra.resource.impl;

import java.util.UUID;

import br.com.lovra.dto.filamentspool.UpdateFilamentSpoolRequest;
import br.com.lovra.resource.FilamentSpoolResource;
import br.com.lovra.service.FilamentSpoolService;
import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/api/spools")
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
@ApplicationScoped
public class FilamentSpoolResourceImpl implements FilamentSpoolResource {
	
	private final FilamentSpoolService filamentSpoolService;
	
	@Context
    private UriInfo uriInfo;

	public FilamentSpoolResourceImpl(FilamentSpoolService filamentSpoolService) {
		this.filamentSpoolService = filamentSpoolService;
	}

	@GET
    @Path("/{id}")
	@Override
	public Response findById(@PathParam("id") UUID id) {
		return Response.status(Response.Status.OK)
				.entity(this.filamentSpoolService.findById(id))
				.build();
	}

	@PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
	@Override
	public Response update(@PathParam("id") UUID id, UpdateFilamentSpoolRequest updateFilamentSpoolRequest) {
		this.filamentSpoolService.update(id, updateFilamentSpoolRequest);
		return Response.noContent().build();
	}

	@DELETE
    @Path("/{id}")
	@Override
	public Response delete(@PathParam("id") UUID id) {
		this.filamentSpoolService.delete(id);
		return Response.noContent().build();
	}
}