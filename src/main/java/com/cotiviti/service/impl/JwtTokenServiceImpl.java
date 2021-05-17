package com.cotiviti.service.impl;

import com.cotiviti.constants.JwtTokenConstants;
import com.cotiviti.constants.MetatableConstant;
import com.cotiviti.entities.Customer;
import com.cotiviti.exception.UnauthorizedException;
import com.cotiviti.repository.CustomerRepository;
import com.cotiviti.service.JwtTokenService;
import com.cotiviti.security.JwtUser;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.impl.DefaultClock;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    private Clock clock = DefaultClock.INSTANCE;
    private final String JWT_SECRET_KEY = "UkXp2r5u8x/A?D(G";

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public String generateToken(JWTClaimsSet jwtClaimsSet, Date createdDate) {
        try {
            final Date expirationDate = calculateExpirationDate(createdDate);
            jwtClaimsSet = new JWTClaimsSet.Builder(jwtClaimsSet)
                    .expirationTime(expirationDate)
                    .build();

            SignedJWT signedJWT = generateSignedJWT(jwtClaimsSet);

            final String encryptionToken = encryptToken(signedJWT);
            return encryptionToken;
        } catch (Exception e) {
            log.error("Exception::: {}", e.getMessage());
            return null;
        }
    }

    private Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + MetatableConstant.IDLE_SESSION_EXPIRY_TIME * 1000);
    }

    private SignedJWT generateSignedJWT(JWTClaimsSet jwtClaimsSet) {
        try {
            KeyPair rsaKey = generateKeyPair();
            JWK jwk = new RSAKey.Builder((RSAPublicKey) rsaKey.getPublic())
                    .keyID(UUID.randomUUID().toString())
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .jwk(jwk)
                    .type(JOSEObjectType.JWT)
                    .build(), jwtClaimsSet);

            RSASSASigner signer = new RSASSASigner(rsaKey.getPrivate());
            signedJWT.sign(signer);
            return signedJWT;
        } catch (JOSEException e) {
            log.error("Exception::: {}", e.getMessage());
            return null;
        }
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            log.error("Exception::: {}", e.getMessage());
            return null;
        }
    }

    private String encryptToken(SignedJWT signedJWT) {
        try {
            JWEHeader jweHeader = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128GCM);
            Payload payload = new Payload(signedJWT);
            DirectEncrypter directEncrypter = new DirectEncrypter(JWT_SECRET_KEY.getBytes("UTF-8"));
            JWEObject jweObject = new JWEObject(jweHeader, payload);
            jweObject.encrypt(directEncrypter);
            return jweObject.serialize();
        } catch (Exception e) {
            log.error("Exception::: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String getUsername(String token) {
        return (String) getAllClaimsFromToken(token).getClaim(JwtTokenConstants.USERNAME.getName());
    }

    private JWTClaimsSet getAllClaimsFromToken(String token) {
        try {
            final SignedJWT signedJWT = decryptToken(token);
            return signedJWT.getJWTClaimsSet();
        } catch (ParseException ex) {
            log.error("Exception : " + ex.getMessage());
            return null;
        }
    }

    private SignedJWT decryptToken(String token) {
        try {
            JWEObject jweObject = JWEObject.parse(token);
            DirectDecrypter directDecrypter = new DirectDecrypter(JWT_SECRET_KEY.getBytes("UTF-8"));
            jweObject.decrypt(directDecrypter);
            if (verifySignature(jweObject.getPayload().toSignedJWT())) {
                return jweObject.getPayload().toSignedJWT();
            }
            log.error("Invalid Signature");
            return null;
        } catch (ParseException | UnsupportedEncodingException | JOSEException ex) {
            log.error("Exception : " + ex.getMessage());
            return null;
        }
    }

    private boolean verifySignature(SignedJWT signedJWT) throws JOSEException, ParseException {
        RSAKey publicKey = RSAKey.parse(signedJWT.getHeader().getJWK().toJSONObject());
        return signedJWT.verify(new RSASSAVerifier(publicKey));
    }

    @Override
    public Boolean validateToken(String token, UserDetails userDetails) {
        JwtUser user = (JwtUser) userDetails;
        final String username = getUsername(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token) && !isLoginExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        final Date expirationDate = getExpirationDate(token);
        if (expirationDate.before(clock.now())) {
            throw new UnauthorizedException("Session Expired", "Please log in again.");
        }
        return false;
    }

    private Date getExpirationDate(String token) {
        return getClaimFromToken(token, JWTClaimsSet::getExpirationTime);
    }

    private <T> T getClaimFromToken(String token, Function<JWTClaimsSet, T> claimsResolver) {
        final JWTClaimsSet claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private boolean isLoginExpired(String token) {
        Optional<Customer> customerOpt = customerRepository.findById(getLongValue(token, JwtTokenConstants.USER_ID.getName()));
        if (customerOpt.isPresent()) {
            return customerOpt.get().isLoginExpired();
        }
        return true;
    }

    public Long getLongValue(String token, String key) {
        return Long.valueOf(getAllClaimsFromToken(token).getClaim(key).toString());
    }

    @Override
    public String refreshToken(String token) {
        if (canTokenBeRefreshed(token)) {
            return generateRefreshedToken(getAllClaimsFromToken(token), clock.now());
        }
        return null;
    }

    private String generateRefreshedToken(JWTClaimsSet jwtClaimsSet, Date createdDate) {
        try {
            final Date expirationDate = calculateExpirationDate(createdDate, jwtClaimsSet);
            jwtClaimsSet = new JWTClaimsSet.Builder(jwtClaimsSet)
                    .expirationTime(expirationDate)
                    .build();

            SignedJWT signedJWT = generateSignedJWT(jwtClaimsSet);

            final String encryptionToken = encryptToken(signedJWT);
            return encryptionToken;
        } catch (Exception e) {
            log.error("Exception::: {}", e.getMessage());
            return null;
        }
    }

    private Date calculateExpirationDate(Date createdDate, JWTClaimsSet claims) {
        return new Date(createdDate.getTime() + MetatableConstant.IDLE_SESSION_EXPIRY_TIME * 1000);
    }

    private Boolean canTokenBeRefreshed(String token) {
        return !isTemp(token) && (!isTokenExpired(token));
    }

    @Override
    public boolean isTemp(String token) {
        return getBooleanValue(token, JwtTokenConstants.IS_TEMP);
    }

    private boolean getBooleanValue(String token, JwtTokenConstants jwtTokenConstants) {
        return (Boolean) Optional.ofNullable(getAllClaimsFromToken(token).getClaim(jwtTokenConstants.getName())).orElse(false);
    }

    @Override
    public UsernamePasswordAuthenticationToken getAuthentication(String token, UserDetails userDetails) {
        try {
            final SignedJWT signedJWT = decryptToken(token);

            final JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();

            final Collection<? extends GrantedAuthority> authorities = Arrays.stream(jwtClaimsSet.getClaim(JwtTokenConstants.ROLES.getName()).toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
        } catch (ParseException e) {
        }
        return new UsernamePasswordAuthenticationToken(userDetails, "", new ArrayList<>());
    }

}
