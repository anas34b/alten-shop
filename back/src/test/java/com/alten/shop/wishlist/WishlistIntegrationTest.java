package com.alten.shop.wishlist;

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
 * Test d'INTEGRATION de la liste d'envies.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WishlistIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    private String bearer(String email) {
        return "Bearer " + jwtService.generateToken(email);
    }

    @Test
    void ajout_puis_lecture_de_la_wishlist() throws Exception {
        String admin = bearer("admin@admin.com");

        mockMvc.perform(post("/wishlist").header("Authorization", admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product.id").value(1));

        mockMvc.perform(get("/wishlist").header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].product.id").value(1));
    }

    @Test
    void ajouter_deux_fois_le_meme_produit_ne_cree_pas_de_doublon() throws Exception {
        String admin = bearer("admin@admin.com");

        mockMvc.perform(post("/wishlist").header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"productId\":1}"));
        mockMvc.perform(post("/wishlist").header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"productId\":1}"));

        // Toujours une seule entree.
        mockMvc.perform(get("/wishlist").header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void retrait_de_la_wishlist() throws Exception {
        String admin = bearer("admin@admin.com");

        mockMvc.perform(post("/wishlist").header("Authorization", admin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"productId\":1}"));

        mockMvc.perform(delete("/wishlist/1").header("Authorization", admin))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/wishlist").header("Authorization", admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void wishlist_sans_token_renvoie_401() throws Exception {
        mockMvc.perform(get("/wishlist"))
                .andExpect(status().isUnauthorized());
    }
}
