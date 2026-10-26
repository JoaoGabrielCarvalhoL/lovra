package br.com.lovra.resource;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.lovra.dto.filament.CreateFilamentRequest;
import br.com.lovra.dto.filament.UpdateFilamentRequest;
import br.com.lovra.resource.impl.FilamentSpoolSubResourceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Tag(name = "Filament Management", description = "Operations for creating, retrieving, listing, and updating filaments.")
public interface FilamentResource {

    @Operation(summary = "Create a filament", description = "Registers a new filament with its name, brand, color, and material type.")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Filament created successfully."),
        @APIResponse(responseCode = "400", description = "The request contains invalid filament data."),
        @APIResponse(responseCode = "401", description = "Authentication is required."),
        @APIResponse(responseCode = "403", description = "The authenticated user is not allowed to create filaments.")})
    Response create(@Valid CreateFilamentRequest createFilamentRequest);

    @Operation(summary = "Get filament by ID", description = "Retrieves the details of a filament using its unique identifier.")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Filament retrieved successfully."),
        @APIResponse(responseCode = "400", description = "The provided filament identifier is invalid."),
        @APIResponse(responseCode = "401", description = "Authentication is required."),
        @APIResponse(responseCode = "403", description = "The authenticated user is not allowed to access this filament."),
        @APIResponse(responseCode = "404", description = "Filament not found.")})
    Response findById(UUID id);

    @Operation(summary = "List all filaments", description = "Retrieves all registered filaments.")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Filaments retrieved successfully. Returns an empty list if no filaments are registered."),
        @APIResponse(responseCode = "401", description = "Authentication is required."),
        @APIResponse(responseCode = "403", description = "The authenticated user is not allowed to list filaments.")})
    Response findAll(@Min(0) int page, @Min(1) @Max(100) int size);

    @Operation(summary = "Update a filament", description = "Updates the name, brand, color, and material type of an existing filament.")
    @APIResponses(value = {
        @APIResponse(responseCode = "204", description = "Filament updated successfully."),
        @APIResponse(responseCode = "400", description = "The request contains invalid filament data or an invalid identifier."),
        @APIResponse(responseCode = "401", description = "Authentication is required."),
        @APIResponse(responseCode = "403", description = "The authenticated user is not allowed to update this filament."),
        @APIResponse(responseCode = "404", description = "Filament not found.")})
    Response update(UUID id, @Valid UpdateFilamentRequest updateFilamentRequest);
    
    @Operation(summary = "Delete filament by ID", description = "Deletes a filament using its unique identifier.")
    @APIResponses(value = {
        @APIResponse(responseCode = "204", description = "Filament deleted successfully."),
        @APIResponse(responseCode = "400", description = "The provided filament identifier is invalid."),
        @APIResponse(responseCode = "401", description = "Authentication is required."),
        @APIResponse(responseCode = "403", description = "The authenticated user is not allowed to delete this filament."),
        @APIResponse(responseCode = "404", description = "Filament not found."),
        @APIResponse(responseCode = "409", description = "The filament cannot be deleted because it is associated with one or more spools.")})
    Response delete(UUID id);

    
    /**
     * Locates the sub-resource responsible for managing spools associated
     * with a specific filament.
     *
     * <p>This method acts as a Jakarta REST sub-resource locator. Unlike
     * regular resource methods, it does not declare an HTTP method annotation
     * such as {@code @GET} or {@code @POST}. Instead, it delegates request
     * processing to {@link FilamentSpoolSubResourceImpl}, which provides
     * the corresponding resource methods.</p>
     *
     * <p>The {@code filamentId} path parameter is available to the
     * sub-resource methods through {@code @PathParam}, allowing them to
     * access the parent filament identifier without storing request-specific
     * state in the sub-resource instance.</p>
     *
     * <p>Returning the concrete implementation also allows SmallRye OpenAPI
     * to discover and document the nested endpoints in the Swagger UI.
     * This behavior was verified with Quarkus 3.39.4.</p>
     *
     * <p>Supported endpoints:</p>
     * <ul>
     *   <li>GET /api/filaments/{filamentId}/spools</li>
     *   <li>POST /api/filaments/{filamentId}/spools</li>
     * </ul>
     *
     * @param filamentId the unique identifier of the parent filament
     * @return the sub-resource responsible for filament spool operations
     */
    @Path("/{filamentId}/spools")
    FilamentSpoolSubResourceImpl getSpoolsResource(@PathParam("filamentId") UUID filamentId);
}