package com.alten.shop.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test UNITAIRE du JwtService.
 *
 * "Unitaire" = on teste une seule classe, isolee, SANS demarrer Spring ni la base.
 * On cree directement un JwtService avec une cle et une duree de test.
 */
class JwtServiceTest {

    // Cle de test (>= 32 caracteres) et duree de validite d'1 heure.
    private final JwtService jwtService = new JwtService(
            "cle-secrete-de-test-suffisamment-longue-0123456789", 3_600_000L);

    @Test
    void generateToken_puis_extractEmail_renvoie_le_meme_email() {
        // On genere un token pour un e-mail donne...
        String token = jwtService.generateToken("anas@test.com");

        // ...le token ne doit pas etre vide...
        assertFalse(token.isBlank(), "Le token genere ne doit pas etre vide");

        // ...et quand on le relit, on doit retrouver exactement le meme e-mail.
        assertEquals("anas@test.com", jwtService.extractEmail(token));
    }

    @Test
    void extractEmail_sur_un_token_invalide_leve_une_exception() {
        // Un token bidon doit etre rejete (signature invalide).
        assertThrows(Exception.class, () -> jwtService.extractEmail("token.invalide.bidon"));
    }
}
