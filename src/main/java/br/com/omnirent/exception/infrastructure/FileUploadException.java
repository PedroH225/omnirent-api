package br.com.omnirent.exception.infrastructure;

import br.com.omnirent.exception.common.InfrastructureException;

public class FileUploadException extends InfrastructureException {
	private static final long serialVersionUID = 1L;

	public FileUploadException(Throwable e) {
		super("Failed to upload file: " + e);
	} 

}
