package br.com.lovra.exception.handler;

import br.com.lovra.exception.FilamentInUseException;
import br.com.lovra.exception.response.ProblemDetailResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class FilamentInUseExceptionHandler implements ExceptionMapper<FilamentInUseException> {

	private static final String PROBLEM_JSON = "application/problem+json";

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(FilamentInUseException exception) {
        int status = Response.Status.CONFLICT.getStatusCode();

        ProblemDetailResponse problem = new ProblemDetailResponse(
            "about:blank",
            "Conflict",
            status,
            exception.getMessage(),
            uriInfo.getRequestUri().getRawPath()
        );

        return Response.status(Response.Status.CONFLICT)
            .type(PROBLEM_JSON)
            .entity(problem)
            .build();
    }

}
