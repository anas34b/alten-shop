package com.alten.shop.wishlist;

import com.alten.shop.wishlist.dto.AddToWishlistRequest;
import com.alten.shop.wishlist.dto.WishlistItemResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Routes de la liste d'envies de l'utilisateur connecte.
 * Memes principes que le panier : token requis, utilisateur deduit du token.
 */
@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    /** GET /wishlist : le contenu de la liste d'envies. */
    @GetMapping
    public List<WishlistItemResponse> getWishlist() {
        return wishlistService.getWishlist().stream()
                .map(WishlistItemResponse::from)
                .toList();
    }

    /** POST /wishlist : ajoute un produit. Repond 201. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WishlistItemResponse addToWishlist(@Valid @RequestBody AddToWishlistRequest request) {
        return WishlistItemResponse.from(
                wishlistService.addToWishlist(request.productId()));
    }

    /** DELETE /wishlist/{productId} : retire un produit. Repond 204. */
    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromWishlist(@PathVariable Long productId) {
        wishlistService.removeFromWishlist(productId);
    }
}
