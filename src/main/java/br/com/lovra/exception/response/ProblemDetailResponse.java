package br.com.lovra.exception.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/***
 * Problem Details (RFC 9457)
 */

@JsonInclude(Include.NON_NULL)
public record ProblemDetailResponse(
    String type,
    String title,
    Integer status,
    String detail,
    String instance
) {
}
