package br.com.lovra.resource;

import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import br.com.lovra.dto.filamentspool.CreateFilamentSpoolRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.core.Response;

@Tag(name = "Filament Spool Management")
public interface FilamentSpoolSubResource {

    @Operation(summary = "Create a filament spool", description = "Registers a new spool associated with an existing filament.")
    @APIResponses(value = {
        @APIResponse(responseCode = "201", description = "Filament spool created successfully."),
        @APIResponse(responseCode = "400", description = "The request contains invalid filament spool data."),
        @APIResponse(responseCode = "401", description = "Authentication is required."),
        @APIResponse(responseCode = "403", description = "The authenticated user is not allowed to create filament spools."),
        @APIResponse(responseCode = "404", description = "Filament not found.")})
    Response create(UUID filamentId, @Valid CreateFilamentSpoolRequest createFilamentSpoolRequest);

    @Operation(summary = "List filament spools by filament ID", description = "Retrieves a paginated list of spools associated with an existing filament.")
    @APIResponses(value = {
        @APIResponse(responseCode = "200", description = "Filament spools retrieved successfully."),
        @APIResponse(responseCode = "400", description = "The provided parameters are invalid."),
        @APIResponse(responseCode = "401", description = "Authentication is required."),
        @APIResponse(responseCode = "403", description = "The authenticated user is not allowed to list filament spools.")})
    Response findAllByFilamentId(UUID filamentId, @Min(0) int page, @Min(1) @Max(100) int size);
	

}
