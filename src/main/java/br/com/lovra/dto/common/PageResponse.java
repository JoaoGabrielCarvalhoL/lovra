package br.com.lovra.dto.common;

import java.util.List;

public record PageResponse<T>(
		List<T> content,
	    int page,
	    int size,
	    long totalElements,
	    int totalPages) {

}
