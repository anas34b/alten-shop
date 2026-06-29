package com.alten.shop.wishlist;

import com.alten.shop.product.Product;
import com.alten.shop.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acces base pour les entrees de liste d'envies.
 */
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    /** Toute la liste d'envies d'un utilisateur. */
    List<WishlistItem> findByUser(User user);

    /** L'entree pour un produit precis (si presente). */
    Optional<WishlistItem> findByUserAndProduct(User user, Product product);

    /** Indique si un produit est deja dans la liste d'envies de l'utilisateur. */
    boolean existsByUserAndProduct(User user, Product product);
}
