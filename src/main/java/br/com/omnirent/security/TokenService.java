package br.com.omnirent.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.com.omnirent.config.GlobalConfigHolder;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.AuthenticationErrorType;
import br.com.omnirent.exception.domain.apptype.CommonErrorType;
import br.com.omnirent.security.domain.AuthenticatedUser;
import br.com.omnirent.user.UserService;
import br.com.omnirent.user.domain.AuthMetadata;
import br.com.omnirent.user.domain.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    @Value("${tokenPass}")
    private String tokenPass;
    
	private final UserService userService;
    
    private final GlobalConfigHolder globalConfigHolder;
    
    public String generateToken(User userModel) {
        try {
        	
            Algorithm algorithm = Algorithm.HMAC256(tokenPass);

            AuthMetadata authMetadata = userModel.getAuthMetadata();

            return JWT.create()
                .withIssuer("auth")
                .withSubject(userModel.getId())
                .withClaim(
                    "roles",
                    userModel.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
                )
                .withClaim("ver", authMetadata.getTokenVersion())
                .withClaim("gver", authMetadata.getGlobalVersion())
                .withExpiresAt(getExpirationDate())
                .sign(algorithm);

        } catch (JWTCreationException exception) {
            throw new ApiException(CommonErrorType.INTERNAL_ERROR);
        }
    }

    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(tokenPass);

            return JWT.require(algorithm)
                .withIssuer("auth")
                .build()
                .verify(token);

        } catch (JWTVerificationException exception) {
            throw new BadCredentialsException(
                "", new ApiException(AuthenticationErrorType.INVALID_TOKEN)
            );
        }
    }
    
    public Authentication authenticate(DecodedJWT decoded) {
    	var id = decoded.getSubject();
        
        var rolesClaim = decoded.getClaim("roles").asList(String.class);

        List<SimpleGrantedAuthority> authorities =
            rolesClaim == null ? List.of() :
            rolesClaim.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        
        Integer tokenVer = decoded.getClaim("ver").asInt();
        AuthMetadata authMetadata = userService.getTokenVersion(id);
        if (!tokenVer.equals(authMetadata.getTokenVersion())) {
			throw new CredentialsExpiredException(
					"", new ApiException(AuthenticationErrorType.INVALID_TOKEN));
		} 
        
        Integer globalVer = globalConfigHolder.getGlobalTokenVersion();
        Integer currGlobalVer = decoded.getClaim("ver").asInt();
        if (!globalVer.equals(currGlobalVer)) {
			throw new CredentialsExpiredException(
					"", new ApiException(AuthenticationErrorType.INVALID_TOKEN));
		}
        
        AuthenticatedUser authenticatedUser = new AuthenticatedUser
        		(id, authorities, tokenVer, globalVer);

        var auth = new UsernamePasswordAuthenticationToken(
            authenticatedUser, null, authorities
        );
        
        return auth;
    }

    private Instant getExpirationDate() {
        return LocalDateTime.now()
            .plusHours(2)
            .toInstant(ZoneOffset.of("-03:00"));
    }
}