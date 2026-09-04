package com.cinema.Booking_service.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class KeycloakJwtAuthenticationConverter
        implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter scopesConverter =
            new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        Collection<GrantedAuthority> authorities =
                new ArrayList<>(scopesConverter.convert(jwt));

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess != null) {

            @SuppressWarnings("unchecked")
            Collection<String> roles =
                    (Collection<String>) realmAccess.get("roles");

            if (roles != null) {
                roles.forEach(role ->
                        authorities.add(
                                new SimpleGrantedAuthority("ROLE_" + role)
                        )
                );
            }
        }

        return new JwtAuthenticationToken(jwt, authorities);
    }
}