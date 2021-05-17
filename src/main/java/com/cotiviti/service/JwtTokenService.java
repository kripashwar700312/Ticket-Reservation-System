package com.cotiviti.service;

import com.nimbusds.jwt.JWTClaimsSet;
import java.util.Date;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtTokenService {

    String generateToken(JWTClaimsSet jwtClaimsSet, Date createdDate);

    String getUsername(String token);

    Boolean validateToken(String token, UserDetails userDetails);

    String refreshToken(String token);

    UsernamePasswordAuthenticationToken getAuthentication(final String token, final UserDetails userDetails);

    boolean isTemp(String token);
}
