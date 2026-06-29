package com.alten.shop.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Donnees attendues pour CREER un compte (route POST /account).
 * Correspond exactement au payload impose par le sujet :
 *   { username, firstname, email, password }
 *
 * Un "record" = une classe compacte pour transporter des donnees :
 * Java genere automatiquement le constructeur, les accesseurs et equals/hashCode.
 *
 * Les annotations valident les donnees AVANT tout traitement :
 *  - @NotBlank : champ obligatoire (ni vide, ni espaces seulement)
 *  - @Email    : doit ressembler a une adresse e-mail
 *  - @Size     : longueur minimale (ici 6 caracteres pour le mot de passe)
 */
public record CreateAccountRequest(

        @NotBlank(message = "Le nom d'utilisateur est obligatoire")
        String username,

        @NotBlank(message = "Le prenom est obligatoire")
        String firstname,

        @NotBlank(message = "L'e-mail est obligatoire")
        @Email(message = "L'e-mail n'est pas valide")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit faire au moins 6 caracteres")
        String password
) {
}
