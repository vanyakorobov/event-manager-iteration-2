package korobov.dev.eventnotificator.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenManager {

    private final Key signingKey;
    private final long jwtLifetime;

    public JwtTokenManager(
            @Value("${jwt.sign-key}") String signKeyHex,
            @Value("${jwt.lifetime}") long jwtLifetime
    ) {
        // Преобразуем ваш hex-ключ в Key для HMAC
        this.signingKey = Keys.hmacShaKeyFor(signKeyHex.getBytes());
        this.jwtLifetime = jwtLifetime;
    }

    /**
     * Парсит токен и возвращает его Claims
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Проверяет, валиден ли токен (не истёк срок жизни)
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Генерация нового токена, если понадобится.
     * (Необязательно, но может пригодиться в будущем)
     */
    public String generateToken(Long userId, List<String> roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtLifetime);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("userId", userId)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(signingKey)
                .compact();
    }
}
