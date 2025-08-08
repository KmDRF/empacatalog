package com.empacatalog.security.jwt; // Paquete corregido

import com.empacatalog.domain.model.User; // Importa la entidad User
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders; // Importa Decoders
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key; // Usa java.security.Key
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // Clave secreta para firmar el token, obtenida de application.properties
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Tiempo de expiración del token en milisegundos, de application.properties
    @Value("${jwt.expiration.ms}")
    private int jwtExpirationMs;

    // Genera un token JWT a partir de los detalles de un usuario.
    public String generateJwtToken(User userDetails) { // Ahora recibe un objeto User
        return Jwts.builder()
                .setSubject((userDetails.getEmail())) // El sujeto del token es el email del usuario
                .setIssuedAt(new Date()) // Fecha de emisión
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Fecha de expiración
                .signWith(key(), SignatureAlgorithm.HS256) // Firma el token con la clave secreta
                .compact();
    }

    // Obtiene la clave secreta decodificada.
    private Key key() { // Método para obtener la clave de firma
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)); // Decodifica la clave Base64
    }

    // Obtiene el email del usuario a partir del token JWT.
    public String getUserNameFromJwtToken(String token) { // Renombrado para consistencia con UserDetails
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // Valida un token JWT.
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT inválido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string está vacío: {}", e.getMessage());
        }
        return false;
    }

    // Verifica si el token ha expirado (útil para validaciones adicionales)
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            // Si hay alguna excepción al parsear, consideramos que está expirado o es inválido
            return true;
        }
    }

    // Obtener fecha de expiración del token (útil para información del cliente)
    public Date getExpirationDateFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
}
