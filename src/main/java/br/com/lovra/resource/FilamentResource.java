
package br.com.lovra.resource;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.lovra.dto.filament.CreateFilamentRequest;
import br.com.lovra.dto.filament.UpdateFilamentRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/filaments")
@Tag(name = "Filament Management", description = "Operations for creating, retrieving, listing, and updating filaments.")
@Produces(MediaType.APPLICATION_JSON)
public interface FilamentResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a filament", description = "Registers a new filament with its name, brand, color, and material type.")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", name = "Created", description = "Filament created successfully."),
        @APIResponse(responseCode = "400", name = "Bad Request", description = "The request contains invalid filament data."),
        @APIResponse(responseCode = "401", name = "Unauthorized", description = "Authentication is required."),
        @APIResponse(responseCode = "403", name = "Forbidden", description = "The authenticated user is not allowed to create filaments.")})
    Response create(@Valid CreateFilamentRequest createFilamentRequest);

    @GET
    @Path("/{id}")
    @Operation(summary = "Get filament by ID", description = "Retrieves the details of a filament using its unique identifier.")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", name = "OK", description = "Filament retrieved successfully."),
        @APIResponse(responseCode = "400", name = "Bad Request", description = "The provided filament identifier is invalid."),
        @APIResponse(responseCode = "401", name = "Unauthorized", description = "Authentication is required."),
        @APIResponse(responseCode = "403", name = "Forbidden", description = "The authenticated user is not allowed to access this filament."),
        @APIResponse(responseCode = "404", name = "Not Found", description = "Filament not found.")})
    Response findById(@PathParam("id") UUID id);

    @GET
    @Operation(summary = "List all filaments", description = "Retrieves all registered filaments.")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", name = "OK", description = "Filaments retrieved successfully. Returns an empty list if no filaments are registered."),
        @APIResponse(responseCode = "401", name = "Unauthorized", description = "Authentication is required."),
        @APIResponse(responseCode = "403", name = "Forbidden", description = "The authenticated user is not allowed to list filaments.")})
    Response findAll(@QueryParam("page") @DefaultValue("0") @Min(0) int page,
    	    @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size);

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a filament", description = "Updates the name, brand, color, and material type of an existing filament.")
    @APIResponses(value = {
        @APIResponse(responseCode = "204", name = "No Content", description = "Filament updated successfully."),
        @APIResponse(responseCode = "400", name = "Bad Request", description = "The request contains invalid filament data or an invalid identifier."),
        @APIResponse(responseCode = "401", name = "Unauthorized", description = "Authentication is required."),
        @APIResponse(responseCode = "403", name = "Forbidden", description = "The authenticated user is not allowed to update this filament."),
        @APIResponse(responseCode = "404", name = "Not Found", description = "Filament not found.")})
    Response update(@PathParam("id") UUID filamentId, @Valid UpdateFilamentRequest updateFilamentRequest);
}
