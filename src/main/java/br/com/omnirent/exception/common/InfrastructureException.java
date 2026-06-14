package br.com.omnirent.exception.common;

public class InfrastructureException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public InfrastructureException(String message) {
        super(message);
    }

    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
}
