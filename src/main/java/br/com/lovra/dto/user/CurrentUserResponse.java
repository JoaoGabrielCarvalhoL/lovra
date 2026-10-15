package br.com.lovra.dto.user;

import java.util.Set;

public record CurrentUserResponse(String id, 
		String givenName, String familyName, String username, String email, 
		Set<String> roles) {

}
