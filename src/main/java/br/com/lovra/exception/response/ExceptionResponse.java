package br.com.lovra.exception.response;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public record ExceptionResponse(
		String title, 
		String message, 
		Integer status, 
		Instant occurredAt, 
		Map<String, String> errors) {

}
