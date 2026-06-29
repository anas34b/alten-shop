package com.alten.shop.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtre execute UNE FOIS a chaque requete HTTP (OncePerRequestFilter).
 *
 * Role : si la requete porte un en-tete "Authorization: Bearer <token>",
 * on verifie le token et, s'il est valide, on declare l'utilisateur comme
 * "authentifie" pour la suite du traitement, avec son ROLE :
 *   - ROLE_ADMIN si l'e-mail du token est celui de l'admin
 *   - ROLE_USER  sinon
 *
 * Si le token est absent ou invalide, on ne fait rien : Spring Security
 * refusera alors l'acces aux routes protegees (reponse 401).
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    /** E-mail de l'admin, lu depuis la configuration (app.admin.email). */
    private final String adminEmail;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   @Value("${app.admin.email}") String adminEmail) {
        this.jwtService = jwtService;
        this.adminEmail = adminEmail;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // On ne traite que les en-tetes du type "Bearer <token>".
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // enleve le prefixe "Bearer "
            try {
                // Verifie la signature/expiration et recupere l'e-mail contenu dans le token.
                String email = jwtService.extractEmail(token);

                // Choisit le role selon l'e-mail.
                String role = email.equalsIgnoreCase(adminEmail) ? "ROLE_ADMIN" : "ROLE_USER";
                var authorities = List.of(new SimpleGrantedAuthority(role));

                // Declare l'utilisateur comme authentifie pour cette requete.
                var authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // Token invalide ou expire : on s'assure qu'aucune authentification n'est en place.
                SecurityContextHolder.clearContext();
            }
        }

        // On laisse la requete continuer son chemin (filtre suivant / controleur).
        filterChain.doFilter(request, response);
    }
}
