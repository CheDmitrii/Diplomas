package ru.system.authentication.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.system.authentication.config.security.handler.LoginSuccessHandler;
import ru.system.authentication.config.security.handler.LogoutSuccessHandler;
import ru.system.authentication.config.security.jwt.JwtRoleConverter;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private String LOGIN_URL = "http://localhost:9000/oauth2/authorize?response_type=code&client_id=client&scope=openid&redirect_uri=http://localhost:9000/oauth2/callback";

    @Order(2)
    @Bean
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/roles").hasRole("USER")
                                .requestMatchers("/admin/**").permitAll()
                                .requestMatchers("/loginss", "/login").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/oauth2/**", "/login", "/loginss", "/logout/**"))
//                .oauth2Login(Customizer.withDefaults())
                // todo: тут задать кастомный loginUri
                .formLogin(login -> login
                        .loginPage("/loginss")
                        .loginProcessingUrl("/login")
                        .successHandler(loginSuccessHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/test?url")
                        .addLogoutHandler(logoutSuccessHandler)
                        .logoutSuccessHandler(
                                (request, response, authentication) -> {
                                    response.sendRedirect(LOGIN_URL);
//                                    response.sendRedirect("/test?url");
                                }
                        )
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .oauth2ResourceServer(auth -> auth
                        .jwt(jwt -> jwt
                                .jwkSetUri("http://localhost:9000/oauth2/jwks")
                                .jwtAuthenticationConverter(new JwtRoleConverter())
                        )
                );


//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/oauth2/**").permitAll() // Разрешить доступ к эндпоинтам OAuth2
//                        .anyRequest().permitAll()
//                )
////                .exceptionHandling(ex -> ex.
////                        authenticationEntryPoint(oauth2AuthenticationEntryPoint())
////                )
////                .exceptionHandling(ex -> ex
////                        .authenticationEntryPoint((req, res, authEx) -> {
////                            res.setContentType("application/json");
////                            res.setStatus(401);
////                            res.getWriter().write(String.format(
////                                    "{\"error\":\"%s\", \"description\":\"%s\"}",
////                                    "invalid_client",
////                                    authEx.getMessage()
////                            ));
////                        })
////                )
////                .csrf(csrf -> csrf.ignoringRequestMatchers("/oauth2/**", "/login/**")) // Отключить CSRF для токеновых эндпоинтов
//                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
        corsConfiguration.setAllowedMethods(List.of("*"));
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }


//    @Order(1)
//    @Bean
//    public SecurityFilterChain authServerFilterChain(HttpSecurity http) throws Exception {
//        http
//                .securityMatcher("/admin/**")
//                .authorizeHttpRequests(authorize -> authorize
//                                .anyRequest().permitAll()
//                )
//                .csrf(csrf -> csrf.disable());
//
//        return http.build();
//    }

    /**
     * use this way when role put as single field ("role": "roleValue")
     * or with custom structure
     * */
//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("role");
//        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
//
//        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
//        // use to get role from more complex structure
//        //jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {});
//        return jwtAuthenticationConverter;
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
