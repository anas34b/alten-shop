package com.alten.shop.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
 * Test d'INTEGRATION du CONTROLE D'ACCES sur /api/products (etape 4).
 *
 * Verifie les 3 cas cles :
 *   - sans token        -> 401 (non authentifie)
 *   - token utilisateur -> peut LIRE, mais 403 s'il tente de CREER
 *   - token admin       -> peut CREER (201)
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    private String bearer(String email) {
        return "Bearer " + jwtService.generateToken(email);
    }

    private static final String NEW_PRODUCT = """
            {"name":"Nouveau","price":12.0,"category":"Fitness","inventoryStatus":"INSTOCK"}
            """;

    // ---------- Sans token ----------

    @Test
    void lecture_sans_token_renvoie_401() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized()); // 401
    }

    @Test
    void creation_sans_token_renvoie_401() throws Exception {
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NEW_PRODUCT))
                .andExpect(status().isUnauthorized()); // 401
    }

    // ---------- Token d'un utilisateur "normal" ----------

    @Test
    void lecture_avec_token_utilisateur_renvoie_200() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("Authorization", bearer("user@test.com")))
                .andExpect(status().isOk()); // 200 : un utilisateur connecte peut lire
    }

    @Test
    void creation_avec_token_utilisateur_renvoie_403() throws Exception {
        mockMvc.perform(post("/api/products")
                        .header("Authorization", bearer("user@test.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NEW_PRODUCT))
                .andExpect(status().isForbidden()); // 403 : interdit a un non-admin
    }

    // ---------- Token de l'admin ----------

    @Test
    void creation_avec_token_admin_renvoie_201() throws Exception {
        mockMvc.perform(post("/api/products")
                        .header("Authorization", bearer("admin@admin.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(NEW_PRODUCT))
                .andExpect(status().isCreated()); // 201 : seul l'admin peut creer
    }
}
