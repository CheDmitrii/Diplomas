package ru.authentication.service.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import ru.authentication.service.MyUserDetailsService;

@Configuration
//@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/oauth2/**").permitAll() // Разрешить доступ к эндпоинтам OAuth2
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/oauth2/**", "/login/**"))
//                .oauth2Login(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults());
//                .userDetailsService(userDetailsService); // Отключить CSRF для токеновых эндпоинтов



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
