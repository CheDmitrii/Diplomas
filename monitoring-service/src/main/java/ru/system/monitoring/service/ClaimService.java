package ru.system.monitoring.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ru.system.library.exception.HttpResponseEntityException;

import java.util.Map;
import java.util.UUID;

@Service
public class ClaimService {
    public UUID getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null || !(authentication.getCredentials() instanceof Jwt jwt)) {
            throw new HttpResponseEntityException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
        String userId = jwt.getClaimAsString("user_id");
        if (userId == null) {
            throw new HttpResponseEntityException(HttpStatus.BAD_REQUEST, "Bad jwt, wrong user_id");
        }
        return UUID.fromString(userId);
    }

    public String getRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null || !(authentication.getCredentials() instanceof Jwt jwt)) {
            throw new HttpResponseEntityException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
        Object claim = jwt.getClaim("role");
        if (!(claim instanceof Map)) {
            throw new HttpResponseEntityException(HttpStatus.BAD_REQUEST, "Bad jwt, user doesn't have role");
        }
        Object value = ((Map<String, Object>) claim).get("value");
        if (value == null) {
            throw new HttpResponseEntityException(HttpStatus.BAD_REQUEST, "Bad jwt, user doesn't have role");
        }
        return value.toString();
    }

    public boolean hasFullPermission() {
        return this.getRole().equalsIgnoreCase("admin");
    }
}
