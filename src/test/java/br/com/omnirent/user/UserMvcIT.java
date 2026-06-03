package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.omnirent.config.GlobalConfigHolder;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.SpringMvcIntegration;
import br.com.omnirent.security.dto.RegisterDTO;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserRequestDTO;
import br.com.omnirent.utils.SecurityTestUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Transactional
public class UserMvcIT extends SpringMvcIntegration {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private GlobalConfigHolder globalConfigHolder;

	private static final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule());
	
	private static final String USER_PREFIX = "/user";
	
	private static final String AUTH_PREFIX = "/auth";
	
	private User user1;
	
	@BeforeEach
	void setUp() {
		user1 = userRepository.save(UserTestFactory.owner());
	    SecurityTestUtils.setAuthenticatedUser(user1.getId());
	    globalConfigHolder.setGlobalTokenVersion(1);
	}
	
	@AfterEach
	void clearAuth() {
		SecurityTestUtils.clear();
	}
	
	@Test
	void shouldSanitizeNewUser() throws Exception {
		RegisterDTO dirty = new RegisterDTO(
		        "  John   Doe  ",
		        "  JOHN    DOE  ",
		        "  JOHNDOE@EMAIL.COM  ",
		        LocalDate.now().minusYears(20),
		        "  Password123  ",
		        "  Password123  "
		);
		
		String payload = objectMapper.writeValueAsString(dirty);
		
	    mockMvc.perform(post(AUTH_PREFIX + "/register")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(payload))
	        .andExpect(status().isOk());
	    
	    User user = userRepository.findByEmail("johndoe@email.com").orElseThrow();

	    assertThat(user.getName()).isEqualTo("John Doe");
	    assertThat(user.getDisplayUsername()).isEqualTo("johndoe");
	    assertThat(user.getEmail()).isEqualTo("johndoe@email.com");
	}
	
	@Test
	void shouldSanitizeUpdateUserBody() throws Exception {
		String dirtyName = "  John   Doe  ";
		String dirtyUsername = "  JOHN    DOE  ";
		String dirtyEmail = "  JOHNDOE@EMAIL.COM  ";
		
		UserRequestDTO dirty = UserTestFactory.requestDtoBuilder(
		        dirtyName,
		        dirtyUsername,
		        dirtyEmail,
		        user1.getBirthDate().minusYears(20)
		);
		
		String payload = objectMapper.writeValueAsString(dirty);
		
	    mockMvc.perform(put(USER_PREFIX + "/update")
	    		.with(SecurityTestUtils.auth(user1.getId()))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(payload))
	        .andExpect(status().isOk());

	    entityManager.flush();
	    entityManager.clear();
	    
	    User user = userRepository.findById(user1.getId()).orElseThrow();

	    assertThat(user.getName()).isEqualTo("John Doe");
	    assertThat(user.getDisplayUsername()).isEqualTo("johndoe");
	    assertThat(user.getEmail()).isEqualTo("johndoe@email.com");
	}
	
	@Test
	void shouldThrowAfterSanitization() throws Exception {
	    String dirtyName = "    a    ";
	    String dirtyUsername = "  JOHN    DOE  ";
	    String dirtyEmail = "  JOHNDOE@EMAIL.COM  ";

	    UserRequestDTO dirty = UserTestFactory.requestDtoBuilder(
	            dirtyName,
	            dirtyUsername,
	            dirtyEmail,
	            user1.getBirthDate().minusYears(20)
	    );

	    String payload = objectMapper.writeValueAsString(dirty);

	    mockMvc.perform(put(USER_PREFIX + "/update")
	            .with(SecurityTestUtils.auth(user1.getId()))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(payload))
	        .andExpect(status().isConflict());
	}
}
