package ru.system.authentication.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.system.authentication.config.security.handler.LoginSuccessHandler;
import ru.system.authentication.config.security.handler.LogoutSuccessHandler;
import ru.system.authentication.config.security.jwt.JwtRoleConverter;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private String LOGIN_URL = "http://localhost:9000/oauth2/authorize?response_type=code&client_id=client&scope=openid&redirect_uri=http://localhost:9000/oauth2/callback";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Order(2)
    @Bean
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/roles").hasRole("USER")
                                .requestMatchers("/admin/**").permitAll()
//                                .requestMatchers("/admin/**").hasRole("USER")
                                .requestMatchers("/loginss", "/login").permitAll()
//                        .requestMatchers("/oauth2/**").permitAll() // Разрешить доступ к эндпоинтам OAuth2
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


    @Order(1)
    @Bean
    public SecurityFilterChain authServerFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**")
                .authorizeHttpRequests(authorize -> authorize
                                .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    /**
     * use this way when role put as single field ("role": "roleValue")
     * or with custom structure*/
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

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails userDetails = User.withUsername("test").password("111").authorities("read").build();
//        var inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
//        inMemoryUserDetailsManager.createUser(userDetails);
//        return inMemoryUserDetailsManager;
//    }



//    @Bean
//    AuthenticationEntryPoint oauth2AuthenticationEntryPoint() {
//        return (req, res, ex) -> {
//            if (ex instanceof OAuth2AuthenticationException oauthEx) {
//                res.setContentType("application/json");
//                res.setStatus(oauthEx.getError().getErrorCode().equals("invalid_client") ? 401 : 400);
//                res.getWriter().write(String.format("""
//                {
//                    "error": "%s",
//                    "error_description": "%s"
//                }
//                """, oauthEx.getError().getErrorCode(), oauthEx.getMessage()));
//            }
//        };
//    }





//    //    // 2. SecurityFilterChain для аутентификации пользователей (форма логина)
//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, MyUserDetailsService userDetailsService) throws Exception {
//        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
//                new OAuth2AuthorizationServerConfigurer();
//        http
//                .securityMatcher("/oauth2/**")
//                .authorizeHttpRequests(authorize -> authorize
////                        .requestMatchers("/login", "/error").permitAll()
////                        .requestMatchers("/oauth2/**").permitAll()
//                                .anyRequest().permitAll()
//                )
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/oauth2/**"))
////                .csrf(AbstractHttpConfigurer::disable)
//                //                .formLogin(Customizer.withDefaults())
//
//                .userDetailsService(userDetailsService)
//                .apply(authorizationServerConfigurer);
//
//
//
//        authorizationServerConfigurer
//                .registeredClientRepository(registeredClientRepository())
//                .authorizationService(authorizationService())
//                .authorizationConsentService(authorizationConsentService())
//                .tokenGenerator(tokenGenerator(jwkSource()))
//                .authorizationServerSettings(authorizationServerSettings());
//
//
//        return http.build();
//    }




//    @Bean
//    OAuth2AuthorizationService authorizationService() {
//        return new InMemoryOAuth2AuthorizationService();
//    }
//
//    @Bean
//    OAuth2AuthorizationConsentService authorizationConsentService() {
//        return new InMemoryOAuth2AuthorizationConsentService();
//    }
//
//    @Bean
//    OAuth2TokenGenerator<?> tokenGenerator(JWKSource<SecurityContext> jwkSource) {
//        JwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource);
//        return new JwtGenerator(jwtEncoder);
//    }
//
//
//    @Bean
//    @Order(2)
//    SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//                .formLogin(AbstractHttpConfigurer::disable);
//
//        return http.build();
//    }
}
