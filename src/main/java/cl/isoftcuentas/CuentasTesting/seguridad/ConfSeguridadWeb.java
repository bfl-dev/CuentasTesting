package cl.isoftcuentas.CuentasTesting.seguridad;

import cl.isoftcuentas.CuentasTesting.utils.ValidadorTokenJwt;
import cl.isoftcuentas.CuentasTesting.utils.JwtUtilidad;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Map;

@Configuration
@EnableWebSecurity
@AllArgsConstructor

public class ConfSeguridadWeb {

    private final JwtUtilidad jwtUtilidad;

    @Bean
    @Order(0)
    public SecurityFilterChain loginSecurityFilterChain(HttpSecurity httpSecurity, CorsConfigurationSource corsConfigurationSource) throws Exception {
        return httpSecurity
                .securityMatcher("api/autenticacion/**") // SOLO estas rutas
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .cors(c -> c.configurationSource(corsConfigurationSource))
                .build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain customSecurityFilterChain(HttpSecurity httpSecurity, CorsConfigurationSource corsConfigurationSource) throws Exception {
        return httpSecurity
                .securityMatcher("/api/cuentas/**") // SOLO rutas protegidas
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .cors(c -> c.configurationSource(corsConfigurationSource))
                .addFilterBefore(new ValidadorTokenJwt(jwtUtilidad), UsernamePasswordAuthenticationFilter.class)
                .build();
    }



    @Bean
    public AuthenticationManager authenticationManager
            (UserDetailsService userDetailsService, PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder PasswordEncoder() {
        return new DelegatingPasswordEncoder(
                "noop",
                Map.of("noop", NoOpPasswordEncoder.getInstance())
        );
    }


}
