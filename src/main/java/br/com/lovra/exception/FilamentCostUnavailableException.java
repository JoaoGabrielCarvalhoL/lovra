package br.com.lovra.exception;

public class FilamentCostUnavailableException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public FilamentCostUnavailableException(String message) {
		super(message);
	}

}
