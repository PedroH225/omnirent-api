package br.com.omnirent.config;

import org.springframework.context.annotation.Scope;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "global_configurations")
public class GlobalConfigurations {
	
	@Id
	private Integer id;

	@Column(name = "global_token_version")
	private Integer globalTokenVersion;
	
	
}
