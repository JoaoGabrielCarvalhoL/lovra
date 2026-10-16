
package br.com.lovra.resource;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.lovra.dto.filamentspool.CreateFilamentSpoolRequest;
import br.com.lovra.dto.filamentspool.UpdateFilamentSpoolRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
@Tag(name = "Filament Spool Management", description = "Operations for creating, retrieving, listing, updating, and deleting filament spools.")
@Produces(MediaType.APPLICATION_JSON)
public interface FilamentSpoolResource {

    @POST
    @Path("/filaments/{filamentId}/spools")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a filament spool", description = "Registers a new spool associated with an existing filament.")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", name = "Created", description = "Filament spool created successfully."),
        @APIResponse(responseCode = "400", name = "Bad Request", description = "The request contains invalid filament spool data or an invalid filament identifier."),
        @APIResponse(responseCode = "401", name = "Unauthorized", description = "Authentication is required."),
        @APIResponse(responseCode = "403", name = "Forbidden", description = "The authenticated user is not allowed to create filament spools."),
        @APIResponse(responseCode = "404", name = "Not Found", description = "Filament not found.")})
    Response create(@PathParam("filamentId") UUID filamentId, @Valid CreateFilamentSpoolRequest createFilamentSpoolRequest);

    @GET
    @Path("/filament-spools/{id}")
    @Operation(summary = "Get filament spool by ID", description = "Retrieves the details of a filament spool using its unique identifier.")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", name = "OK", description = "Filament spool retrieved successfully."),
        @APIResponse(responseCode = "400", name = "Bad Request", description = "The provided filament spool identifier is invalid."),
        @APIResponse(responseCode = "401", name = "Unauthorized", description = "Authentication is required."),
        @APIResponse(responseCode = "403", name = "Forbidden", description = "The authenticated user is not allowed to access this filament spool."),
        @APIResponse(responseCode = "404", name = "Not Found", description = "Filament spool not found.")})
    Response findById(@PathParam("id") UUID id);

    @GET
    @Path("/filaments/{filamentId}/spools")
    @Operation(summary = "List filament spools by filament ID", description = "Retrieves a paginated list of spools associated with an existing filament.")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", name = "OK", description = "Filament spools retrieved successfully. Returns an empty list if the filament has no registered spools."),
        @APIResponse(responseCode = "400", name = "Bad Request", description = "The provided filament identifier or pagination parameters are invalid."),
        @APIResponse(responseCode = "401", name = "Unauthorized", description = "Authentication is required."),
        @APIResponse(responseCode = "403", name = "Forbidden", description = "The authenticated user is not allowed to list filament spools."),
        @APIResponse(responseCode = "404", name = "Not Found", description = "Filament not found.")})
    Response findAllByFilamentId(@PathParam("filamentId") UUID filamentId,
            @QueryParam("page") @DefaultValue("0") @Min(0) int page,
            @QueryParam("size") @DefaultValue("20") @Min(1) @Max(100) int size);

    @PUT
    @Path("/filament-spools/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update a filament spool", description = "Updates the purchase price, initial weight, and purchase date of an existing filament spool.")
    @APIResponses(value = {
        @APIResponse(responseCode = "204", name = "No Content", description = "Filament spool updated successfully."),
        @APIResponse(responseCode = "400", name = "Bad Request", description = "The request contains invalid filament spool data or an invalid identifier."),
        @APIResponse(responseCode = "401", name = "Unauthorized", description = "Authentication is required."),
        @APIResponse(responseCode = "403", name = "Forbidden", description = "The authenticated user is not allowed to update this filament spool."),
        @APIResponse(responseCode = "404", name = "Not Found", description = "Filament spool not found.")})
    Response update(@PathParam("id") UUID id, @Valid UpdateFilamentSpoolRequest updateFilamentSpoolRequest);

    @DELETE
    @Path("/filament-spools/{id}")
    @Operation(summary = "Delete a filament spool", description = "Deletes an existing filament spool using its unique identifier.")
    @APIResponses(value = {
        @APIResponse(responseCode = "204", name = "No Content", description = "Filament spool deleted successfully."),
        @APIResponse(responseCode = "400", name = "Bad Request", description = "The provided filament spool identifier is invalid."),
        @APIResponse(responseCode = "401", name = "Unauthorized", description = "Authentication is required."),
        @APIResponse(responseCode = "403", name = "Forbidden", description = "The authenticated user is not allowed to delete this filament spool."),
        @APIResponse(responseCode = "404", name = "Not Found", description = "Filament spool not found."),
        @APIResponse(responseCode = "409", name = "Conflict", description = "The filament spool cannot be deleted because it is referenced by other records.")})
    Response delete(@PathParam("id") UUID id);
}
