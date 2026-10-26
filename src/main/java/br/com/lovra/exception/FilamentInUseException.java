package br.com.lovra.exception;

public class FilamentInUseException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FilamentInUseException(String message) {
		super(message);
	}

}
