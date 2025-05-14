package ru.system.authentication.config.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import ru.system.authentication.entity.Role;
import ru.system.library.exception.HttpResponseEntityException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class JwtRoleConverter implements Converter<Jwt, AbstractAuthenticationToken> {



    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    private Collection<? extends GrantedAuthority> extractResourceRoles(final Jwt jwt) {
//        Role role;
//        try {
//            role = objectMapper.readValue(jwt.getClaimAsString("role"), Role.class);
//        } catch (JsonProcessingException e) {
//            log.error("Can't parse role");
//            return Collections.emptySet();
//        }
//        log.error("role value {}", role);
//        if (role == null) {
//            return Collections.emptySet();
//        }
//        Role resourceAccess =  objectMapper.readTree((String) jwt.getClaim("role"), Role.class); // <- specify here whatever additional jwt claim you wish to convert to authority
//        if (resourceAccess != null) {
//            // Convert every entry in value list of "role" claim to an Authority
//            return resourceAccess.stream().map(x -> new SimpleGrantedAuthority("ROLE_" + x))
//                    .collect(Collectors.toSet());
//        }
        // Fallback: return empty list in case the jwt has no "role" claim.
        Map<String, Object> claim = jwt.getClaim("role");
        if (Objects.isNull(claim)) {
            throw new HttpResponseEntityException(HttpStatus.BAD_REQUEST, "Bad data of jwt");
        }
        Object value = claim.get("value");
        if (Objects.isNull(value) || !(value instanceof String)) {
            throw new HttpResponseEntityException(HttpStatus.BAD_REQUEST, "Bad data of jwt");
        }
        return Set.of(new SimpleGrantedAuthority("ROLE_" + (String) value));
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        Collection<GrantedAuthority> authorities =
                Stream.concat(defaultGrantedAuthoritiesConverter.convert(source).stream(),
                        extractResourceRoles(source).stream()).collect(Collectors.toSet());
        return new JwtAuthenticationToken(source, authorities);
    }
}
