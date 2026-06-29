package com.alten.shop.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Porte d'entree vers la base pour les utilisateurs.
 * Comme pour les produits, JpaRepository fournit automatiquement le CRUD.
 *
 * On ajoute ici DEUX methodes "sur mesure". On ecrit juste leur signature :
 * Spring Data JPA devine la requete SQL a partir du NOM de la methode.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur par son e-mail.
     * Utilisee a la connexion (POST /token) pour retrouver le compte.
     * Optional = "peut-etre un User, peut-etre rien" (evite les erreurs si absent).
     */
    Optional<User> findByEmail(String email);

    /**
     * Indique si un e-mail est deja utilise.
     * Utilisee a l'inscription (POST /account) pour interdire les doublons.
     */
    boolean existsByEmail(String email);
}
