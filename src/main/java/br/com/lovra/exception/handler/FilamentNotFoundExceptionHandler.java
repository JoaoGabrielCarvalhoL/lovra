package br.com.lovra.exception.handler;

import java.time.LocalDateTime;
import java.util.Collections;

import br.com.lovra.exception.FilamentNotFoundException;
import br.com.lovra.exception.response.ExceptionResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class FilamentNotFoundExceptionHandler implements ExceptionMapper<FilamentNotFoundException> {

	@Override
	public Response toResponse(FilamentNotFoundException exception) {
		return Response.status(422)
                .entity(new ExceptionResponse(
                        "Unprocessable Content",
                        exception.getMessage(),
                        422, 
                        LocalDateTime.now(), 
                        Collections.emptyMap()))
                .build();
	}

}
