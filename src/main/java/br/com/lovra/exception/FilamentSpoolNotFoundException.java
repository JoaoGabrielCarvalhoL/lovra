package br.com.lovra.exception;

public class FilamentSpoolNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public FilamentSpoolNotFoundException(String message) {
		super(message);
	}

}
