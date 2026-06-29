package com.alten.shop.cart.dto;

import com.alten.shop.cart.CartItem;
import com.alten.shop.product.Product;

/**
 * Ce qu'on renvoie au client pour une ligne de panier :
 * le produit complet + la quantite. On n'expose PAS l'utilisateur.
 */
public record CartItemResponse(Product product, Integer quantity) {

    /** Convertit une entite CartItem (base) en reponse pour le client. */
    public static CartItemResponse from(CartItem item) {
        return new CartItemResponse(item.getProduct(), item.getQuantity());
    }
}
