package br.com.lovra.resource;

import org.eclipse.microprofile.jwt.JsonWebToken;

import br.com.lovra.dto.user.CurrentUserResponse;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/me")
public class MeResource {

	private final SecurityIdentity securityIdentity;
	private final JsonWebToken accessToken;
	
	public MeResource(SecurityIdentity securityIdentity, JsonWebToken accessToken) {
		this.securityIdentity = securityIdentity;
		this.accessToken = accessToken;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public CurrentUserResponse getCurrentUser() {
		return toCurrentUserResponse();
	}
	
	private CurrentUserResponse toCurrentUserResponse() {
        return new CurrentUserResponse(
                this.accessToken.getSubject(), 
                this.accessToken.getClaim("given_name"), 
                this.accessToken.getClaim("family_name"), 
                this.accessToken.getClaim("preferred_username"), 
                this.accessToken.getClaim("email"), 
                this.securityIdentity.getRoles()
        );
	}
}
