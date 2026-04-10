package br.com.omnirent.user;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class AuthMetadata {		
	private Integer tokenVersion;
	
	private Integer globalVersion;
	
}
