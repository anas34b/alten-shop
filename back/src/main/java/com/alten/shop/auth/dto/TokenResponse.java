package com.alten.shop.auth.dto;

/**
 * Reponse renvoyee apres une connexion reussie (POST /token) :
 * elle contient le jeton JWT que le client devra renvoyer ensuite
 * dans l'en-tete "Authorization: Bearer <token>".
 */
public record TokenResponse(String token) {
}
