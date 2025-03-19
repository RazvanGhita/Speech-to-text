package com.wtztechnologies.speechtotext.authentication.model;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenResolver {

    
    private final JwtExtractor jwtExtractor = new JwtExtractor();

    public Authentication resolve(String jwtToken) {
        Map<String, Object> claimsSet = jwtExtractor.getClaimSets(jwtToken);

        JwtPrincipal principal = new JwtPrincipal(jwtToken, claimsSet);

        List<String> roles = Optional.ofNullable(principal.getRoles()).orElse(null);
        List<SimpleGrantedAuthority> grantedAuthorities = roles.stream()
                .map(s -> new SimpleGrantedAuthority(s.toUpperCase())).collect(toList());

        return new UsernamePasswordAuthenticationToken(principal, null, grantedAuthorities);
    }
}
