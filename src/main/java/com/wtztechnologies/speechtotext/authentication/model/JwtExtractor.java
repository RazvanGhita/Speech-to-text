package com.wtztechnologies.speechtotext.authentication.model;

import java.text.ParseException;
import java.util.Map;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public final class JwtExtractor {

    public Map<String, Object> getClaimSets(String jwtToken) {
       
        try {
            JWT jwt = JWTParser.parse(jwtToken);
            JWTClaimsSet jwtClaimSet = jwt.getJWTClaimsSet();

            return jwtClaimSet.getClaims();
        } catch (IllegalArgumentException | ParseException e) {
            System.err.println(e);
            return (Map<String, Object>) e;
        }
    }
}
