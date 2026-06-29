package com.alten.shop.auth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test d'INTEGRATION de l'authentification (routes /account et /token).
 *
 * Classe nommee "*Test" pour etre executee par "mvn test".
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void inscription_puis_connexion_renvoie_un_token() throws Exception {
        // 1) Creation d'un compte -> 201, et la reponse ne doit PAS contenir le mot de passe.
        String inscription = """
                {"username":"u","firstname":"f","email":"new@test.com","password":"secret123"}
                """;
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inscription))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@test.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

        // 2) Connexion avec les memes identifiants -> 200 + un token non vide.
        String connexion = """
                {"email":"new@test.com","password":"secret123"}
                """;
        mockMvc.perform(post("/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(connexion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void connexion_mauvais_mot_de_passe_renvoie_401() throws Exception {
        String inscription = """
                {"username":"u","firstname":"f","email":"wp@test.com","password":"secret123"}
                """;
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inscription))
                .andExpect(status().isCreated());

        String mauvaise = """
                {"email":"wp@test.com","password":"MAUVAIS"}
                """;
        mockMvc.perform(post("/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mauvaise))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void inscription_email_deja_utilise_renvoie_409() throws Exception {
        String inscription = """
                {"username":"u","firstname":"f","email":"dup@test.com","password":"secret123"}
                """;
        // 1re fois : OK
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inscription))
                .andExpect(status().isCreated());
        // 2e fois avec le meme e-mail : conflit
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inscription))
                .andExpect(status().isConflict());
    }
}
