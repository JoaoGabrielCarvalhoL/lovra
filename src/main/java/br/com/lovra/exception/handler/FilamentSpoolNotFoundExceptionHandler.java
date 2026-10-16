package br.com.lovra.exception.handler;

import br.com.lovra.exception.FilamentSpoolNotFoundException;
import br.com.lovra.exception.response.ProblemDetailResponse;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class FilamentSpoolNotFoundExceptionHandler implements ExceptionMapper<FilamentSpoolNotFoundException> {

	private static final String PROBLEM_JSON = "application/problem+json";

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(FilamentSpoolNotFoundException exception) {
        int status = Response.Status.NOT_FOUND.getStatusCode();

        ProblemDetailResponse problem = new ProblemDetailResponse(
            "about:blank",
            "Not Found",
            status,
            exception.getMessage(),
            uriInfo.getRequestUri().getRawPath()
        );

        return Response.status(Response.Status.NOT_FOUND)
            .type(PROBLEM_JSON)
            .entity(problem)
            .build();
    }

}
