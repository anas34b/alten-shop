package com.alten.shop.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Donnees attendues pour SE CONNECTER (route POST /token).
 * Payload impose par le sujet : { email, password }.
 */
public record TokenRequest(

        @NotBlank(message = "L'e-mail est obligatoire")
        @Email(message = "L'e-mail n'est pas valide")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        String password
) {
}
