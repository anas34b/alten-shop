package com.alten.shop.cart;

import com.alten.shop.product.Product;
import com.alten.shop.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acces base pour les lignes de panier.
 * Spring Data genere les requetes a partir du nom des methodes.
 */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /** Toutes les lignes de panier d'un utilisateur (= son panier complet). */
    List<CartItem> findByUser(User user);

    /** La ligne correspondant a un produit precis dans le panier d'un utilisateur (si elle existe). */
    Optional<CartItem> findByUserAndProduct(User user, Product product);
}
