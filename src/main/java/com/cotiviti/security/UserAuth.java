package com.cotiviti.security;

import com.cotiviti.constants.JwtTokenConstants;
import com.cotiviti.exception.UnauthorizedException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Optional;

@Slf4j
@Component
@RequestScope
public class UserAuth {

    @Autowired
    private HttpServletRequest httpServletRequest;

    private String token;
    private JWTClaimsSet jwtClaimsSet;

    private final String JWT_SECRET_KEY = "UkXp2r5u8x/A?D(G";
    
    @PostConstruct
    public void init() {
        String requestHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (requestHeader != null && requestHeader.startsWith(JwtTokenConstants.TOKEN_PREFIX.getName())) {
            token = requestHeader.substring(JwtTokenConstants.TOKEN_PREFIX.getName().length());
            if (token != null && !token.isEmpty() && !token.equalsIgnoreCase("null")) {
                try {
                    JWEObject jweObject = JWEObject.parse(token);
                    DirectDecrypter directDecrypter = new DirectDecrypter(JWT_SECRET_KEY.getBytes("UTF-8"));
                    jweObject.decrypt(directDecrypter);
                    jwtClaimsSet = jweObject.getPayload().toSignedJWT().getJWTClaimsSet();
                } catch (UnsupportedEncodingException  | JOSEException | ParseException ex) {
                    log.error("Exception : " + ex.getMessage());
                    throw new UnauthorizedException("Token is not valid");
                }
            } else {
                log.error("No token found in request headers.");
                throw new UnauthorizedException("No token found in request headers");
            }
        } else {
            log.error("No token found in request headers.");
            throw new UnauthorizedException("No token found in request headers");
        }
    }

    public boolean isTemp() {
        return getBooleanValue(JwtTokenConstants.IS_TEMP.getName());
    }

    public Long getLoggedInUserId() {
        return getLongValue(JwtTokenConstants.USER_ID.getName());
    }

    public String getUsername() {
        return getStringValue(JwtTokenConstants.USERNAME.getName());
    }

    public String getGroupName() {
        return getStringValue(JwtTokenConstants.GROUP.getName());
    }

    public String getStringValue(String key) {
        return (String) jwtClaimsSet.getClaim(key);
    }

    public Integer getIntegerValue(String key) {
        return (Integer) jwtClaimsSet.getClaim(key);
    }

    public Long getLongValue(String key) {
        return (Long) jwtClaimsSet.getClaim(key);
    }

    public Boolean getBooleanValue(String key) {
        return (Boolean) Optional.ofNullable(jwtClaimsSet.getClaim(key)).orElse(false);
    }

    public String getTokenId() {
        return jwtClaimsSet.getJWTID();
    }
}
