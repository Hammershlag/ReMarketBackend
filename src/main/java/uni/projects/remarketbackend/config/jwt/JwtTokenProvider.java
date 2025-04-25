package uni.projects.remarketbackend.config.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uni.projects.remarketbackend.exceptions.exceptions.JwtTokenException;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.logging.Logger;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 25.04.2025
 */
@Component
public class JwtTokenProvider {

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${JWT_EXPIRATION}")
    private long jwtExpirationDate;

    @Value("${JWT_REFRESH_EXPIRATION}")
    private long jwtRefreshExpirationDate;

    public String generateToken(String username, long expiration) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + expiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }

    public String generateAccessToken(String username) {
        return generateToken(username, jwtExpirationDate);
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, jwtRefreshExpirationDate);
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // get username from JWT token
    public String getUsername(String token){

        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // validate JWT token
    public boolean validateToken(String token) throws JwtTokenException {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parse(token);
        } catch (Exception e){
            throw new JwtTokenException("Invalid token");
        }

        return true;

    }


}