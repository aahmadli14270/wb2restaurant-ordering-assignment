package com.restaurant.ordering.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String secretKey = "secret-key"; // Use a more secure key in production

    public String createToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 1000 * 60 * 60 * 24); // Token valid for 24 hours

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role) // custom claim for user role
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secretKey) // Use secure key in prod
                .compact();
    }
}
