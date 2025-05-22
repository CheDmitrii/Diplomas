package ru.system.monitoring.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri; // todo: start use

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
////                .authorizeExchange(exchenge -> exchenge
////                        .anyExchange().permitAll()
////                ); // todo: swap on reacive stack (SecurityFilterChain -> SecurityWebFilterChain, HttpSecurity -> ServerHttpSecurity), drop from common library exception and add it in auth service (servlet exception) and in monitoring service (webFlux exception)
////                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
//
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().authenticated()
//                )
//                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwkSetUri(jwkSetUri)));
//        return http.build();
//    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .authorizeExchange(exchenge -> exchenge
//                        .anyExchange().permitAll()
//                ); // todo: swap on reacive stack (SecurityFilterChain -> SecurityWebFilterChain, HttpSecurity -> ServerHttpSecurity), drop from common library exception and add it in auth service (servlet exception) and in monitoring service (webFlux exception)
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

                .authorizeExchange(exchange -> exchange
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwkSetUri(jwkSetUri)));
        return http.build();
    }

    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
        corsConfiguration.setAllowedMethods(List.of("*"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
