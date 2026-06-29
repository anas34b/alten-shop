package com.alten.shop.config;

import com.alten.shop.auth.JwtAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration de la securite : qui a le droit d'acceder a quoi.
 *
 * Regles (exigences du sujet) :
 *  - /account et /token : ouverts a tous (sinon impossible de s'inscrire/se connecter)
 *  - GET /api/products   : reserve aux utilisateurs CONNECTES
 *  - POST/PATCH/DELETE /api/products : reserves a l'ADMIN (admin@admin.com)
 *  - tout le reste : connexion requise
 */
@Configuration
public class SecurityConfig {

    /**
     * Encodeur de mots de passe BCrypt (hache + sel aleatoire).
     * Sert a l'inscription (encoder) et a la connexion (verifier).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Chaine de filtres de securite.
     * On recoit le filtre JWT par injection (il est @Component) pour l'inserer dans la chaine.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {
        http
                // API REST sans cookie de session -> la protection CSRF est inutile.
                .csrf(csrf -> csrf.disable())

                // Autorise l'affichage de la console H2 (rendue dans une <iframe>).
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // Aucune session serveur : on s'appuie uniquement sur le token JWT a chaque requete.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Definition des regles d'acces, route par route.
                .authorizeHttpRequests(auth -> auth
                        // Laisse passer les redirections INTERNES de Spring (page d'erreur /error, forwards).
                        // Sans cela, un refus 403 declenche un dispatch vers /error qui, re-controle sans
                        // authentification, serait transforme a tort en 401.
                        .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()
                        // Routes publiques : inscription, connexion, test, console H2.
                        .requestMatchers("/account", "/token", "/ping").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        // Documentation Swagger / OpenAPI accessible sans authentification.
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        // Lecture des produits : il faut etre connecte.
                        .requestMatchers(HttpMethod.GET, "/api/products/**").authenticated()
                        // Modifications de produits : reservees a l'admin.
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                        // Toute autre route : connexion requise.
                        .anyRequest().authenticated())

                // Si la requete n'est pas authentifiee sur une route protegee -> reponse 401 (et non page de login).
                .exceptionHandling(ex -> ex.authenticationEntryPoint(
                        (request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))

                // Notre filtre JWT s'execute AVANT le filtre d'authentification standard de Spring.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
