package ru.system.monitoring.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri; // todo: start use

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
//                .authorizeExchange(exchenge -> exchenge
//                        .anyExchange().permitAll()
//                ); // todo: swap on reacive stack (SecurityFilterChain -> SecurityWebFilterChain, HttpSecurity -> ServerHttpSecurity), drop from common library exception and add it in auth service (servlet exception) and in monitoring service (webFlux exception)
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

                .authorizeHttpRequests(auth -> auth
                                .anyRequest().permitAll()
//                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.disable());
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
