package br.com.omnirent.config.global;

import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class GlobalConfigSchedule {
	
	private final GlobalConfigHolder globalConfigHolder;
	
	private final GlobalConfigRepository configRepository;
	
	@Scheduled(fixedRate=60000)
	public void getGlobalVersion() {
		Optional<GlobalConfigurations> optGlobalConfig =
				configRepository.findById(1);
				
		if (optGlobalConfig.isPresent()) {
			GlobalConfigurations currGlobalConfig = optGlobalConfig.get();
			
			Integer currGlobalVer = currGlobalConfig.getGlobalTokenVersion();
			Integer globalVer = globalConfigHolder.getGlobalTokenVersion();
			
			if (!currGlobalVer.equals(globalVer)) {
				globalConfigHolder.setGlobalTokenVersion(currGlobalConfig.getGlobalTokenVersion());
			}

		}
	}
}
