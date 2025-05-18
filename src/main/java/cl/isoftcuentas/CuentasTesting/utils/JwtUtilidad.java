package cl.isoftcuentas.CuentasTesting.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtUtilidad {
    @Value("${security.jwt.secret}")
    private String privateKey;

    @Value("${security.jwt.issuer}")
    private String userGenerator;

    @Value("${security.jwt.expiration-minutes}") // Expiración por defecto: 10 minutos
    private int expirationMinutes;

    public String createToken(Authentication authentication) {
        String username = authentication.getPrincipal().toString();

        if (authentication.getPrincipal() instanceof User){
            username = ((User) authentication.getPrincipal()).getUsername();
        }

        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Date issuedDate = new Date();
        Date expirationDate = new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000L);

        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        return JWT.create()
                .withIssuer(this.userGenerator)
                .withSubject(username)
                .withClaim("authorities", authorities) // Guardar roles como array
                .withIssuedAt(issuedDate)
                .withExpiresAt(expirationDate)
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);
    }

    public Optional<DecodedJWT> validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        try {
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build()
                    .verify(token);
            return Optional.of(decodedJWT);
        } catch (TokenExpiredException e) {
            log.warn("JWT token has expired: {}", token);
        } catch (Exception e) {
            log.error("JWT token validation failed: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public String getUsernameFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getSubject();
    }
    public List<String> getRolesFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim("authorities").asList(String.class);
    }
    public String getIssuerFromToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getIssuer();
    }

    public String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> "AuthToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

}
