package com.alten.shop.auth.dto;

/**
 * Reponse renvoyee apres la creation d'un compte (POST /account).
 * On expose volontairement uniquement les infos publiques :
 * surtout PAS le mot de passe (meme hache).
 */
public record AccountResponse(Long id, String username, String firstname, String email) {
}
