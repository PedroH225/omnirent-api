package br.com.omnirent.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.com.omnirent.exception.common.ForbiddenException;
import br.com.omnirent.exception.common.UnauthorizedException;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.AuthMetadata;
import br.com.omnirent.user.domain.User;

@Service
public class TokenService {

	@Value("${tokenPass}")
    private String tokenPass;
	
    public String generateToken(User userModel){
        try {
            Algorithm algorithm = Algorithm.HMAC256(tokenPass);

            AuthMetadata authMetadata = userModel.getAuthMetadata();
            String token = JWT.create()
                .withIssuer("auth")
                .withSubject(userModel.getId())
                .withClaim("roles",
                        userModel.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
                )
                .withClaim("ver", authMetadata.getTokenVersion())
                .withClaim("gver", authMetadata.getGlobalVersion())
                .withExpiresAt(getExpirationDate())
                .sign(algorithm);
            return token;


        } catch (JWTCreationException exception) {
            throw new RuntimeException("ERROR WHILE GENERATING TOKEN", exception);
        }
    }

        public DecodedJWT validateToken(String token){
            try {
                Algorithm algorithm = Algorithm.HMAC256(tokenPass);

                return JWT.require(algorithm)
                    .withIssuer("auth")
                    .build()
                    .verify(token);
            } 
            
            catch (JWTVerificationException exception) {
            	throw new UnauthorizedException("Requires authentication");
            }
        }

        private Instant getExpirationDate(){
            return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
        }
    }
