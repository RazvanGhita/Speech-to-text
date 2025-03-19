package com.wtztechnologies.speechtotext.authentication.model;



import java.security.Principal;
import java.util.Map;



public class JwtPrincipal extends DecodedJwt implements Principal {

    private final String jwtToken;

    public JwtPrincipal(String jwtToken, Map<String, Object> jwtClaimsSet) {
        super(jwtClaimsSet);
        this.jwtToken = jwtToken;
    }

    @Override
    public String getName() {
        return getEmail();
    }

    public String getJwtToken() {
        return jwtToken;
    }

    
}
