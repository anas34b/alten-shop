package com.alten.shop.user;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Retrouve l'utilisateur actuellement connecte.
 *
 * Le filtre JWT a place l'e-mail de l'utilisateur dans le "SecurityContext".
 * Ici, on relit cet e-mail puis on charge l'utilisateur correspondant en base.
 *
 * C'est le point central de l'ISOLATION : panier et wishlist sont toujours
 * rattaches a CET utilisateur, jamais a un id passe dans l'URL. Impossible
 * donc d'acceder aux donnees d'un autre.
 */
@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** @return l'utilisateur connecte, ou une erreur 401 si introuvable. */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non authentifie");
        }
        String email = authentication.getName(); // = l'e-mail place dans le token
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Utilisateur introuvable"));
    }
}
