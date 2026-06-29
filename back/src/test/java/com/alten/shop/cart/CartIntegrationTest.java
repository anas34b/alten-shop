package com.alten.shop.cart;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alten.shop.auth.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test d'INTEGRATION du panier.
 *
 * On utilise le compte admin (cree au demarrage) comme utilisateur courant ;
 * le panier marche pour n'importe quel utilisateur connecte.
 * Le produit d'id 1 existe (premier produit du seed).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CartIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    /** En-tete Authorization pour un e-mail donne. */
    private String bearer(String email) {
        return "Bearer " + jwtService.generateToken(email);
    }

    /** Cree un compte (pour avoir un VRAI utilisateur en base). Statut ignore. */
    private void register(String email) throws Exception {
        String body = "{\"username\":\"x\",\"firstname\":\"x\",\"email\":\"" + email
                + "\",\"password\":\"secret123\"}";
        mockMvc.perform(post("/account").contentType(MediaType.APPLICATION_JSON).content(body));
    }

    @Test
    void ajout_puis_lecture_du_panier() throws Exception {
        String admin = bearer("admin@admin.com");

        // Ajout du produit 1, quantite 2 -> 201
        mockMvc.perform(post("/cart").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1,\"quantity\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product.id").value(1))
                .andExpect(jsonPath("$.quantity").value(2));

        // Lecture du panier -> 1 ligne, quantite 2
        mockMvc.perform(get("/cart").header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].quantity").value(2));
    }

    @Test
    void ajouter_deux_fois_le_meme_produit_additionne_les_quantites() throws Exception {
        String admin = bearer("admin@admin.com");

        mockMvc.perform(post("/cart").header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":1,\"quantity\":2}"));
        mockMvc.perform(post("/cart").header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":1,\"quantity\":3}"));

        // Toujours UNE seule ligne, mais quantite 2+3 = 5
        mockMvc.perform(get("/cart").header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].quantity").value(5));
    }

    @Test
    void retrait_du_panier() throws Exception {
        String admin = bearer("admin@admin.com");

        mockMvc.perform(post("/cart").header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":1,\"quantity\":2}"));

        // Suppression -> 204
        mockMvc.perform(delete("/cart/1").header("Authorization", admin))
                .andExpect(status().isNoContent());

        // Panier vide
        mockMvc.perform(get("/cart").header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void panier_sans_token_renvoie_401() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void isolation_le_panier_d_un_autre_utilisateur_n_est_pas_visible() throws Exception {
        // L'admin ajoute un produit a SON panier.
        mockMvc.perform(post("/cart").header("Authorization", bearer("admin@admin.com"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":1,\"quantity\":2}"));

        // Un autre utilisateur (cree pour l'occasion) voit un panier VIDE.
        register("autre@test.com");
        mockMvc.perform(get("/cart").header("Authorization", bearer("autre@test.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
