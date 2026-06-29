package com.alten.shop.wishlist;

import com.alten.shop.product.Product;
import com.alten.shop.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Une entree de LISTE D'ENVIES : un produit "souhaite" par un utilisateur.
 * Comme le panier mais SANS quantite (on souhaite un produit, point).
 */
@Entity
@Table(name = "wishlist_items")
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** A qui appartient cette envie. */
    @ManyToOne(optional = false)
    private User user;

    /** Le produit souhaite. */
    @ManyToOne(optional = false)
    private Product product;

    /** Constructeur vide requis par JPA. */
    public WishlistItem() {
    }

    /** Constructeur pratique. */
    public WishlistItem(User user, Product product) {
        this.user = user;
        this.product = product;
    }

    // ----- Getters / Setters -----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
