package com.alten.shop.cart;

import com.alten.shop.product.Product;
import com.alten.shop.product.ProductRepository;
import com.alten.shop.user.CurrentUserService;
import com.alten.shop.user.User;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Logique metier du panier.
 *
 * Toutes les operations portent sur le panier de l'UTILISATEUR CONNECTE
 * (recupere via CurrentUserService), jamais sur celui d'un autre.
 */
@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;

    public CartService(CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       CurrentUserService currentUserService) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.currentUserService = currentUserService;
    }

    /** Liste les lignes du panier de l'utilisateur connecte. */
    @Transactional(readOnly = true)
    public List<CartItem> getCart() {
        User user = currentUserService.getCurrentUser();
        return cartItemRepository.findByUser(user);
    }

    /**
     * Ajoute un produit au panier.
     * Si le produit y est deja, on AUGMENTE la quantite au lieu de creer une 2e ligne.
     */
    @Transactional
    public CartItem addToCart(Long productId, int quantity) {
        User user = currentUserService.getCurrentUser();

        // Le produit doit exister, sinon 404.
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produit introuvable : " + productId));

        // Deja dans le panier ? -> on additionne. Sinon -> nouvelle ligne.
        CartItem item = cartItemRepository.findByUserAndProduct(user, product)
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + quantity);
                    return existing;
                })
                .orElseGet(() -> new CartItem(user, product, quantity));

        return cartItemRepository.save(item);
    }

    /** Retire completement un produit du panier de l'utilisateur connecte. */
    @Transactional
    public void removeFromCart(Long productId) {
        User user = currentUserService.getCurrentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produit introuvable : " + productId));

        CartItem item = cartItemRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ce produit n'est pas dans le panier"));

        cartItemRepository.delete(item);
    }
}
