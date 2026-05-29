package br.com.omnirent.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;

import br.com.omnirent.config.SanitizationRequestBodyAdvice;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SanitizationRequestBodyAdvice.class)
public abstract class SpringMvcIntegration extends IntegrationTest {

}
