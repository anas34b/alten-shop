package com.alten.shop.auth;

import com.alten.shop.auth.dto.AccountResponse;
import com.alten.shop.auth.dto.CreateAccountRequest;
import com.alten.shop.auth.dto.TokenRequest;
import com.alten.shop.auth.dto.TokenResponse;
import com.alten.shop.user.User;
import com.alten.shop.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controleur d'authentification : creation de compte et connexion.
 * Expose les deux routes imposees par le sujet : POST /account et POST /token.
 */
@RestController
public class AuthController {

    private final UserRepository userRepository;   // acces a la table des utilisateurs
    private final PasswordEncoder passwordEncoder;  // pour hacher / verifier les mots de passe
    private final JwtService jwtService;            // pour generer les tokens JWT

    /** Spring injecte automatiquement ces trois dependances. */
    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * POST /account : cree un nouveau compte utilisateur.
     * @Valid declenche les validations du DTO (champs obligatoires, e-mail, etc.).
     * Repond 201 (CREATED) en cas de succes.
     */
    @PostMapping("/account")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse register(@Valid @RequestBody CreateAccountRequest request) {
        // 1) Refuser un e-mail deja utilise (sinon deux comptes identiques).
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cet e-mail est deja utilise");
        }

        // 2) Construire le nouvel utilisateur.
        User user = new User();
        user.setUsername(request.username());
        user.setFirstname(request.firstname());
        user.setEmail(request.email());
        // On ne stocke JAMAIS le mot de passe en clair : on enregistre sa version hachee (BCrypt).
        user.setPassword(passwordEncoder.encode(request.password()));

        // 3) Sauvegarder en base.
        User saved = userRepository.save(user);

        // 4) Renvoyer les infos publiques (sans le mot de passe).
        return new AccountResponse(saved.getId(), saved.getUsername(),
                saved.getFirstname(), saved.getEmail());
    }

    /**
     * POST /token : verifie les identifiants et renvoie un token JWT si tout est correct.
     */
    @PostMapping("/token")
    public TokenResponse login(@Valid @RequestBody TokenRequest request) {
        // 1) Retrouver l'utilisateur par son e-mail.
        User user = userRepository.findByEmail(request.email())
                // Message volontairement generique (ne pas reveler si c'est l'e-mail ou le mot de passe qui est faux).
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "E-mail ou mot de passe incorrect"));

        // 2) Comparer le mot de passe fourni avec le hache stocke en base.
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou mot de passe incorrect");
        }

        // 3) Tout est bon : on genere et on renvoie un token JWT.
        String token = jwtService.generateToken(user.getEmail());
        return new TokenResponse(token);
    }
}
