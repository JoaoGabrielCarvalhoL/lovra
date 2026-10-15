package br.com.lovra.exception.handler;

import java.time.Instant;
import java.util.Collections;

import br.com.lovra.exception.FilamentCostUnavailableException;
import br.com.lovra.exception.response.ExceptionResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class FilamentCostUnavailableExceptionHandler implements ExceptionMapper<FilamentCostUnavailableException> {

	@Override
	public Response toResponse(FilamentCostUnavailableException exception) {
		return Response.status(422)
                .entity(new ExceptionResponse(
                        "Unprocessable Content",
                        exception.getMessage(),
                        422, 
                        Instant.now(), 
                        Collections.emptyMap()))
                .build();
	}

}
