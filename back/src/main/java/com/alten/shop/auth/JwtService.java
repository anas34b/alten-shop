package com.alten.shop.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service responsable des tokens JWT : les CREER (a la connexion)
 * et les VERIFIER (a chaque requete protegee).
 *
 * Un JWT est une chaine signee qui contient des infos (ici l'e-mail de l'utilisateur).
 * Comme il est signe avec notre cle secrete, personne ne peut le falsifier.
 *
 * @Service : indique a Spring de creer et gerer une instance de cette classe.
 */
@Service
public class JwtService {

    /** Cle secrete derivee de la valeur "app.jwt.secret" du fichier de config. */
    private final SecretKey secretKey;

    /** Duree de validite d'un token en millisecondes ("app.jwt.expiration-ms"). */
    private final long expirationMs;

    /**
     * Le constructeur recoit les valeurs de configuration grace a @Value.
     * On transforme le texte du secret en vraie cle cryptographique HMAC-SHA.
     */
    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /**
     * Cree un token pour un utilisateur donne.
     * @param email l'e-mail de l'utilisateur, stocke dans le "subject" du token.
     * @return la chaine JWT signee.
     */
    public String generateToken(String email) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)          // l'info principale : a qui appartient le token
                .issuedAt(now)           // date de creation
                .expiration(expiration)  // date au-dela de laquelle le token n'est plus valide
                .signWith(secretKey)     // signature avec notre cle secrete
                .compact();              // produit la chaine finale
    }

    /**
     * Verifie un token et en extrait l'e-mail.
     * Si le token est invalide ou expire, jjwt leve une exception.
     * @param token la chaine JWT a verifier.
     * @return l'e-mail contenu dans le token.
     */
    public String extractEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)        // verifie la signature avec notre cle
                .build()
                .parseSignedClaims(token)     // echoue si signature/expiration invalides
                .getPayload();
        return claims.getSubject();
    }
}
