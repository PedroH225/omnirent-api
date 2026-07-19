package br.com.omnirent.security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.infrastructure.ratelimit.RateLimitFilter;
import br.com.omnirent.security.auth.provider.OAuth2AuthenticationFailureHandler;
import br.com.omnirent.security.auth.provider.OAuth2AuthenticationSuccessHandler;
import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfigurations {

    private SecurityFilter securityFilter;
    
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    
    private OAuth2AuthenticationFailureHandler oAuth2AuthorizationFailureHandler;
    
    private RateLimitFilter rateLimitFilter;
    
    private AppProperties appProperties;

    @Bean 
    protected SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .oauth2Login(oauth -> oauth.
                		successHandler(oAuth2AuthenticationSuccessHandler)
                		.failureHandler(oAuth2AuthorizationFailureHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                		.requestMatchers(
                				"/item/aprove/**",
                				"/item/reject/**"
                				).hasRole("ADMIN")
                		.requestMatchers(
                                HttpMethod.POST,
                                "/auth/login",
                                "/auth/register"
                        ).permitAll()
                        .requestMatchers(
                                "/ws/**",
                                "/rental/enums",
                                "/item/enums",
                                "/item/feed",
                                "/webhooks/**",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/login"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                		.accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rateLimitFilter, SecurityFilter.class)
                .build();
    }
    
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(appProperties.frontUrl()));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean 
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
