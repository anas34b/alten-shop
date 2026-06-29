package com.alten.shop.cart;

import com.alten.shop.product.Product;
import com.alten.shop.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Une LIGNE de panier : un produit, en une certaine quantite, pour un utilisateur donne.
 * Le panier d'un utilisateur = l'ensemble de ses CartItem.
 *
 * Relations UNIDIRECTIONNELLES (@ManyToOne) vers User et Product :
 *  - plusieurs lignes de panier peuvent pointer vers le meme utilisateur / le meme produit
 *  - pas de reference inverse -> evite les boucles lors de la conversion en JSON.
 */
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** A qui appartient cette ligne de panier. optional=false -> jamais nul. */
    @ManyToOne(optional = false)
    private User user;

    /** Le produit ajoute au panier. */
    @ManyToOne(optional = false)
    private Product product;

    /** Quantite de ce produit dans le panier. */
    private Integer quantity;

    /** Constructeur vide requis par JPA. */
    public CartItem() {
    }

    /** Constructeur pratique pour creer une nouvelle ligne. */
    public CartItem(User user, Product product, Integer quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }

    // ----- Getters / Setters -----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
