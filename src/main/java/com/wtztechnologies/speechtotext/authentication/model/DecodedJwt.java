package com.wtztechnologies.speechtotext.authentication.model;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;



@Slf4j
public class DecodedJwt {

    private final Map<String, Object> jwtClaimsSet;

    public DecodedJwt(Map<String, Object> jwtClaimsSet) {
        this.jwtClaimsSet = jwtClaimsSet;
    }

    public String getEmail() {
        return getStringClaim("email");
    }

    public String getUid() {
        return getStringClaim("sub");
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles() {
        Object roles = jwtClaimsSet.get("role");
        return roles instanceof List ? List.copyOf((List<String>) roles) : List.of();
    }

    protected String getStringClaim(String name) {
        Object claim = jwtClaimsSet.get(name);
        if (!(claim instanceof String)) {
            // log.warn("Failed to find claim: {}, received:{}", name, claim);
            return null;
        }

        return (String) claim;
    }
}
