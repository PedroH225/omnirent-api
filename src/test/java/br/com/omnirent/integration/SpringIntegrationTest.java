package br.com.omnirent.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import br.com.omnirent.config.SecurityTestConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(SecurityTestConfig.class)
public abstract class SpringIntegrationTest extends IntegrationTest {

}
