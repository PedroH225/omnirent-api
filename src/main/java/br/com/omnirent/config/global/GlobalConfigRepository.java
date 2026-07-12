package br.com.omnirent.config.global;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalConfigRepository extends JpaRepository<GlobalConfigurations, Integer> {
	
}
