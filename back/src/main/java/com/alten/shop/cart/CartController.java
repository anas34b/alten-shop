package com.alten.shop.cart;

import com.alten.shop.cart.dto.AddToCartRequest;
import com.alten.shop.cart.dto.CartItemResponse;
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
 * Routes du panier de l'utilisateur connecte.
 * Toutes ces routes exigent un token (regle "anyRequest authenticated" de SecurityConfig).
 *
 * Aucune route ne contient d'id d'utilisateur : l'utilisateur est toujours
 * deduit du token -> on ne peut agir que sur SON panier.
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /** GET /cart : le contenu du panier. */
    @GetMapping
    public List<CartItemResponse> getCart() {
        return cartService.getCart().stream()
                .map(CartItemResponse::from)
                .toList();
    }

    /** POST /cart : ajoute un produit (ou augmente sa quantite). Repond 201. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemResponse addToCart(@Valid @RequestBody AddToCartRequest request) {
        return CartItemResponse.from(
                cartService.addToCart(request.productId(), request.quantity()));
    }

    /** DELETE /cart/{productId} : retire un produit du panier. Repond 204. */
    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFromCart(@PathVariable Long productId) {
        cartService.removeFromCart(productId);
    }
}
