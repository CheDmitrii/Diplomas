package ru.system.monitoring.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.system.library.exception.HttpResponseEntityException;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ClaimService {
//    public UUID getUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || authentication.getCredentials() == null || !(authentication.getCredentials() instanceof Jwt jwt)) {
//            throw new HttpResponseEntityException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
//        }
//        String userId = jwt.getClaimAsString("user_id");
//        if (userId == null) {
//            throw new HttpResponseEntityException(HttpStatus.BAD_REQUEST, "Bad jwt, wrong user_id");
//        }
//        return UUID.fromString(userId);
//    }
//
//    public String getRole() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || authentication.getCredentials() == null || !(authentication.getCredentials() instanceof Jwt jwt)) {
//            throw new HttpResponseEntityException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
//        }
//        Object claim = jwt.getClaim("role");
//        if (!(claim instanceof Map)) {
//            throw new HttpResponseEntityException(HttpStatus.BAD_REQUEST, "Bad jwt, user doesn't have role");
//        }
//        Object value = ((Map<String, Object>) claim).get("value");
//        if (value == null) {
//            throw new HttpResponseEntityException(HttpStatus.BAD_REQUEST, "Bad jwt, user doesn't have role");
//        }
//        return value.toString();
//    }
//
//    public boolean hasFullPermission() {
//        return this.getRole().equalsIgnoreCase("admin");
//    }



    public Mono<UUID> getUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(authentication -> {
                    if (authentication == null || authentication.getCredentials() == null || !(authentication.getCredentials() instanceof Jwt jwt)) {
                        return Mono.error(new HttpResponseEntityException(HttpStatus.UNAUTHORIZED, "User is not authenticated"));
                    }
                    String userId = jwt.getClaimAsString("user_id");
                    if (userId == null) {
                        return Mono.error(new HttpResponseEntityException(HttpStatus.BAD_REQUEST, "Bad jwt, wrong user_id"));
                    }
                    try {
                        return Mono.just(UUID.fromString(userId));
                    } catch (IllegalArgumentException e) {
                        return Mono.error(new HttpResponseEntityException(HttpStatus.BAD_REQUEST, "Invalid UUID format in user_id"));
                    }
                });
    }

    public Mono<String> getRole() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(this::extractRoleFromAuthentication)
                .doOnNext(role -> log.info("Role: {}", role));
    }

    private Mono<String> extractRoleFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getCredentials() == null || !(authentication.getCredentials() instanceof Jwt jwt)) {
            return Mono.error(new HttpResponseEntityException(HttpStatus.UNAUTHORIZED, "User is not authenticated"));
        }
        Object claim = jwt.getClaim("role");
        if (!(claim instanceof Map)) {
            return Mono.error(new HttpResponseEntityException(HttpStatus.BAD_REQUEST, "Bad jwt, user doesn't have role"));
        }
        Object value = ((Map<String, Object>) claim).get("value");
        if (value == null) {
            return Mono.error(new HttpResponseEntityException(HttpStatus.BAD_REQUEST, "Bad jwt, user doesn't have role"));
        }
        return Mono.just(value.toString());
    }

    public Mono<Boolean> hasFullPermission() {
        return getRole().map(role -> role.equalsIgnoreCase("admin"));
    }

}
