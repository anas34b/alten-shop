package com.alten.shop.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represente un UTILISATEUR de l'application.
 *
 * Les champs correspondent au payload de creation de compte impose par le sujet
 * (route POST /account) : username, firstname, email, password.
 *
 * IMPORTANT : le champ "password" ne contient JAMAIS le mot de passe en clair,
 * mais sa version HACHEE avec BCrypt (on l'enregistrera comme ca a l'etape suivante).
 */
@Entity
@Table(name = "users")
public class User {

    /** Identifiant unique, genere automatiquement par la base. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom d'utilisateur (pseudo). */
    private String username;

    /** Prenom. */
    private String firstname;

    /**
     * Adresse e-mail. Sert d'identifiant de connexion -> elle doit etre UNIQUE.
     * @Column(unique = true) : la base refusera deux comptes avec le meme e-mail.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /** Mot de passe HACHE (BCrypt), jamais en clair. */
    @Column(nullable = false)
    private String password;

    // ------------------------------------------------------------------
    //  GETTERS / SETTERS (lecture / ecriture de chaque champ)
    // ------------------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
