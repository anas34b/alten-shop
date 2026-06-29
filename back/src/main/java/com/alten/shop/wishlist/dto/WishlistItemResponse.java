package com.alten.shop.wishlist.dto;

import com.alten.shop.product.Product;
import com.alten.shop.wishlist.WishlistItem;

/**
 * Ce qu'on renvoie pour une entree de liste d'envies : le produit souhaite.
 * On n'expose pas l'utilisateur.
 */
public record WishlistItemResponse(Product product) {

    public static WishlistItemResponse from(WishlistItem item) {
        return new WishlistItemResponse(item.getProduct());
    }
}
