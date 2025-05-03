package ru.authentication.service.config;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OidcClientRegistrationEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OidcProviderConfigurationEndpointConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.OidcProviderMetadataClaimAccessor;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import ru.authentication.service.MyUserDetailsService;
import ru.authentication.service.entity.User;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class AuthorizationConfig {


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http, MyUserDetailsService userDetailsService) throws Exception {
//        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
//        var conf = new OAuth2AuthorizationServerConfiguration();
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();
        http
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/oauth2/**").permitAll() // Разрешить доступ к эндпоинтам OAuth2
                        .anyRequest().authenticated())
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/oauth2/**", "/login/**"))
                .formLogin(Customizer.withDefaults())
//                .oauth2ResourceServer(oauth ->
//                        oauth.jwt(jwt -> jwt.jwtAuthenticationConverter()))
                .userDetailsService(userDetailsService);
//                .exceptionHandling(exceptions ->
//                        exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
//                );
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/oauth2/**").permitAll() // Разрешить доступ к эндпоинтам OAuth2
//                        .anyRequest().permitAll()
//                )
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/oauth2/**", "/login/**"))
//                .formLogin(Customizer.withDefaults()); // Отключить CSRF для токеновых эндпоинтов

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class).oidc(Customizer.withDefaults());

        return http.build();
    }



    // http://localhost:9000/oauth2/authorize?response_type=code&client_id=client&scope=openid&redirect_uri=http://localhost:9000/oauth2/callback

    @Bean
    public InMemoryRegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("client")
                .clientSecret("secret") // без шифрования
//                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
//                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)

                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .redirectUri("http://localhost:9000/oauth2/callback")
//                .redirectUri("http://localhost:8080/login/oauth2/code/client-oidc")
                .scope(OidcScopes.OPENID)
                .scope("offline_access")
//                .scope("read")
//                .scope("write")
                .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }


    @Bean
    public AuthenticationManager authenticationManager(MyUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);

        ProviderManager providerManager = new ProviderManager(authProvider);
        /**
        * off remove password and another Credentials after authentication and put it in redis
        * to decrease call function loadUserByUsername from MyUserDetailsService (bad practice)
        * **/
        providerManager.setEraseCredentialsAfterAuthentication(false);

        return providerManager;
    }


    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:9000")
                .build();
    }







//    @Bean
//    public OidcProviderMetadataClaimCustomizer providerMetadataCustomizer() {
//        OidcProviderMetadataClaimAccessor
//        return (metadataContext) -> {
//            OidcProviderMetadata metadata = metadataContext.getProviderMetadata();
//
//            // Customize the id_token_signing_alg_values_supported claim
//            metadata.setClaim(OidcProviderMetadataClaim.ID_TOKEN_SIGNING_ALG_VALUES_SUPPORTED.getName(), Arrays.asList("RS512"));
//        };
//    }






//    @Bean
//    public JwtEncoder jwtEncoder() throws Exception {
//        return new NimbusJwtEncoder(jwkSource());
//    }

//    public JwtDecoder jwtDecoder() throws Exception {
//
//    }


    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer(MyUserDetailsService userDetailsService) {
        return context -> {
            String name = context.getPrincipal().getName();
            System.out.println("Username " + name);
            User user = (User) userDetailsService.loadUserByUsername(name);
            context.getClaims().claim("user_id", user.getId()).build();
//            System.out.println("Current algorithm " + context.get("alg"));
//            context.getJwsHeader().algorithm(SignatureAlgorithm.RS512).build();
//            System.out.println("NEW Current algorithm " + context.get("alg"));
//            context.getHeaders().algorithm(SignatureAlgorithm.RS512);
        };
    }











    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        KeyPair keyPair = this.generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
//                .keyID(UUID.randomUUID().toString())
                .keyID("test_id")
//                .algorithm(new Algorithm("RS512"))
                .build();
        return (jwkSelector, securityContext) -> jwkSelector.select(new JWKSet(rsaKey));
    }

    private KeyPair generateRsaKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
}
