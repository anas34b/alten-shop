package com.alten.shop.wishlist;

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
 * Logique metier de la liste d'envies (toujours pour l'utilisateur connecte).
 */
@Service
public class WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;

    public WishlistService(WishlistItemRepository wishlistItemRepository,
                           ProductRepository productRepository,
                           CurrentUserService currentUserService) {
        this.wishlistItemRepository = wishlistItemRepository;
        this.productRepository = productRepository;
        this.currentUserService = currentUserService;
    }

    /** Liste la wishlist de l'utilisateur connecte. */
    @Transactional(readOnly = true)
    public List<WishlistItem> getWishlist() {
        User user = currentUserService.getCurrentUser();
        return wishlistItemRepository.findByUser(user);
    }

    /**
     * Ajoute un produit a la wishlist.
     * Operation "idempotente" : si le produit y est deja, on ne cree pas de doublon,
     * on renvoie simplement l'entree existante.
     */
    @Transactional
    public WishlistItem addToWishlist(Long productId) {
        User user = currentUserService.getCurrentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produit introuvable : " + productId));

        return wishlistItemRepository.findByUserAndProduct(user, product)
                .orElseGet(() -> wishlistItemRepository.save(new WishlistItem(user, product)));
    }

    /** Retire un produit de la wishlist. */
    @Transactional
    public void removeFromWishlist(Long productId) {
        User user = currentUserService.getCurrentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produit introuvable : " + productId));

        WishlistItem item = wishlistItemRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ce produit n'est pas dans la liste d'envies"));

        wishlistItemRepository.delete(item);
    }
}
