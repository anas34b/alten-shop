package com.alten.shop.wishlist.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Donnees envoyees pour AJOUTER un produit a la liste d'envies.
 * Seul l'id du produit est necessaire (pas de quantite).
 */
public record AddToWishlistRequest(

        @NotNull(message = "L'id du produit est obligatoire")
        Long productId
) {
}
