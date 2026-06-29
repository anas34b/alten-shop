package com.alten.shop.config;

import com.alten.shop.product.Product;
import com.alten.shop.product.ProductRepository;
import com.alten.shop.user.User;
import com.alten.shop.user.UserRepository;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * "Semeur" de donnees de demarrage.
 *
 * Au lancement de l'application, il :
 *   1) charge les 30 produits de demonstration (si la base est vide),
 *   2) cree le compte administrateur (si aucun utilisateur n'existe).
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // pour hacher le mot de passe de l'admin
    private final ObjectMapper objectMapper;        // pour lire le fichier JSON

    private final String adminEmail;
    private final String adminPassword;

    /** Spring injecte automatiquement les dependances et les valeurs de configuration. */
    public DataSeeder(ProductRepository productRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      ObjectMapper objectMapper,
                      @Value("${app.admin.email}") String adminEmail,
                      @Value("${app.admin.password}") String adminPassword) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) throws Exception {
        seedProducts();
        seedAdminUser();
    }

    /** Charge les 30 produits depuis products.json si la table est vide. */
    private void seedProducts() throws Exception {
        if (productRepository.count() > 0) {
            return;
        }
        ClassPathResource resource = new ClassPathResource("products.json");
        try (InputStream is = resource.getInputStream()) {
            List<Product> products = objectMapper.readValue(is, new TypeReference<List<Product>>() {});
            for (Product p : products) {
                p.setId(null); // laisse la base attribuer un id
            }
            productRepository.saveAll(products);
        }
        System.out.println(">>> Seed : " + productRepository.count() + " produits charges.");
    }

    /** Cree le compte admin (mot de passe hache) si aucun utilisateur n'existe encore. */
    private void seedAdminUser() {
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }
        User admin = new User();
        admin.setUsername("admin");
        admin.setFirstname("Admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword)); // jamais en clair
        userRepository.save(admin);
        System.out.println(">>> Seed : compte admin cree (" + adminEmail + ").");
    }
}
