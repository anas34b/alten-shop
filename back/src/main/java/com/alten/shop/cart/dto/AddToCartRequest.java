package com.alten.shop.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Donnees envoyees par le client pour AJOUTER un produit au panier.
 *  - productId : l'id du produit a ajouter (obligatoire)
 *  - quantity  : la quantite (obligatoire, strictement positive)
 */
public record AddToCartRequest(

        @NotNull(message = "L'id du produit est obligatoire")
        Long productId,

        @NotNull(message = "La quantite est obligatoire")
        @Positive(message = "La quantite doit etre superieure a 0")
        Integer quantity
) {
}
