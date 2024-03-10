package webavanzada.pucmm.practica6.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${token.secret_key}")
    private String SECRET_KEY;

    @Value("${token.expiration_time}")
    private Long EXPIRATION_TIME;

    public String getToken(UserDetails user) {
        return getToken(new HashMap<>(), user);
    }

    private String getToken(Map<String, Object> extractClaims, UserDetails user) {
        long expirationTimestamp = System.currentTimeMillis() + EXPIRATION_TIME;
        System.out.println("Expiration Timestamp: " + new Date(expirationTimestamp));

        return Jwts
                .builder()
                .claims(extractClaims)
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(expirationTimestamp))
                .signWith(getKey())
                .compact();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && isTokenExpired(token));
    }

    private Claims getAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T getClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token) {
        return getClaims(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }
}
