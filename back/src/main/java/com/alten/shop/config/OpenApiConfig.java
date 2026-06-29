package com.alten.shop.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration de la documentation OpenAPI / Swagger.
 *
 * @OpenAPIDefinition : metadonnees de l'API (titre, version) affichees en haut de Swagger UI,
 *                      et declaration que l'API utilise globalement le schema de securite "bearerAuth".
 * @SecurityScheme    : definit un schema d'authentification de type "Bearer JWT".
 *                      -> fait apparaitre le bouton "Authorize" dans Swagger UI :
 *                         on y colle un token, et il est ensuite envoye automatiquement
 *                         dans l'en-tete Authorization des requetes de test.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ALTEN SHOP API",
                version = "1.0",
                description = "API e-commerce : produits, authentification JWT, panier et liste d'envies."),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class OpenApiConfig {
}
