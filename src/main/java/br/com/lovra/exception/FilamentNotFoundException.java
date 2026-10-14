package br.com.lovra.exception;

public class FilamentNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FilamentNotFoundException(String message) {
		super(message);
	}
}
