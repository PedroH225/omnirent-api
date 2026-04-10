package br.com.omnirent.config;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Scope("singleton")
@Data
@NoArgsConstructor
public class GlobalConfigHolder {

	private Integer globalTokenVersion;

}
