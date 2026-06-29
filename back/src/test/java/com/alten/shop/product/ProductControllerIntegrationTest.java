package com.alten.shop.product;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alten.shop.auth.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test d'INTEGRATION du COMPORTEMENT FONCTIONNEL des routes /api/products.
 *
 * Depuis l'etape 4, ces routes sont protegees : on doit donc fournir un token.
 * On genere un token "admin" directement via JwtService pour pouvoir tout faire
 * (lecture + creation). Le controle d'acces (qui a le droit) est teste a part
 * dans ProductSecurityIntegrationTest.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    private String adminAuth; // valeur de l'en-tete Authorization pour l'admin

    @BeforeEach
    void setUp() {
        // Un token pour l'admin -> le filtre lui donnera le role ADMIN.
        adminAuth = "Bearer " + jwtService.generateToken("admin@admin.com");
    }

    @Test
    void getAll_renvoie_les_30_produits_du_seed() throws Exception {
        mockMvc.perform(get("/api/products").header("Authorization", adminAuth))
                .andExpect(status().isOk())                    // HTTP 200
                .andExpect(jsonPath("$.length()").value(30));  // 30 produits charges au demarrage
    }

    @Test
    void create_produit_valide_renvoie_201() throws Exception {
        String body = """
                {"name":"Produit de test","price":10.0,"category":"Fitness","inventoryStatus":"INSTOCK"}
                """;

        mockMvc.perform(post("/api/products")
                        .header("Authorization", adminAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())                       // HTTP 201
                .andExpect(jsonPath("$.id").exists())                  // un id a ete genere
                .andExpect(jsonPath("$.name").value("Produit de test"));
    }

    @Test
    void create_sans_nom_renvoie_400() throws Exception {
        // Le nom est obligatoire (@NotBlank) -> la validation doit refuser, meme pour l'admin.
        String body = """
                {"price":10.0}
                """;

        mockMvc.perform(post("/api/products")
                        .header("Authorization", adminAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());                   // HTTP 400
    }

    @Test
    void getById_inexistant_renvoie_404() throws Exception {
        mockMvc.perform(get("/api/products/999999").header("Authorization", adminAuth))
                .andExpect(status().isNotFound());                     // HTTP 404
    }
}
