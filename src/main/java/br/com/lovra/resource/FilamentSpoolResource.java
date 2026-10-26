package br.com.lovra.resource;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.lovra.dto.filamentspool.UpdateFilamentSpoolRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

@Tag(name = "Filament Spool Management", description = "Operations for retrieving, listing, updating, and deleting filament spools.")
public interface FilamentSpoolResource {

    @Operation(summary = "Get filament spool by ID", description = "Retrieves the details of a filament spool using its unique identifier.")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", name = "OK", description = "Filament spool retrieved successfully."),
        @APIResponse(responseCode = "400", name = "Bad Request", description = "The provided filament spool identifier is invalid."),
        @APIResponse(responseCode = "401", name = "Unauthorized", description = "Authentication is required."),
        @APIResponse(responseCode = "403", name = "Forbidden", description = "The authenticated user is not allowed to access this filament spool."),
        @APIResponse(responseCode = "404", name = "Not Found", description = "Filament spool not found.")})
    Response findById(UUID id);

    @Operation(summary = "Update a filament spool", description = "Updates the purchase price, initial weight, and purchase date of an existing filament spool.")
    @APIResponses(value = {
        @APIResponse(responseCode = "204", name = "No Content", description = "Filament spool updated successfully."),
        @APIResponse(responseCode = "400", name = "Bad Request", description = "The request contains invalid filament spool data or an invalid identifier."),
        @APIResponse(responseCode = "401", name = "Unauthorized", description = "Authentication is required."),
        @APIResponse(responseCode = "403", name = "Forbidden", description = "The authenticated user is not allowed to update this filament spool."),
        @APIResponse(responseCode = "404", name = "Not Found", description = "Filament spool not found.")})
    Response update(UUID id, @Valid UpdateFilamentSpoolRequest updateFilamentSpoolRequest);

    @Operation(summary = "Delete a filament spool", description = "Deletes an existing filament spool using its unique identifier.")
    @APIResponses(value = {
        @APIResponse(responseCode = "204", name = "No Content", description = "Filament spool deleted successfully."),
        @APIResponse(responseCode = "400", name = "Bad Request", description = "The provided filament spool identifier is invalid."),
        @APIResponse(responseCode = "401", name = "Unauthorized", description = "Authentication is required."),
        @APIResponse(responseCode = "403", name = "Forbidden", description = "The authenticated user is not allowed to delete this filament spool."),
        @APIResponse(responseCode = "404", name = "Not Found", description = "Filament spool not found."),
        @APIResponse(responseCode = "409", name = "Conflict", description = "The filament spool cannot be deleted because it is referenced by other records.")})
    Response delete(UUID id);
}