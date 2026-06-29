package com.alten.shop.product;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Le "repository" = la porte d'entree vers la base pour les produits.
 *
 * On ECRIT juste cette interface vide... et Spring Data JPA fournit
 * AUTOMATIQUEMENT, sans aucun code de notre part, toutes les methodes CRUD :
 *
 *   - findAll()          -> recupere tous les produits
 *   - findById(id)       -> recupere un produit par son id
 *   - save(produit)      -> cree OU met a jour un produit
 *   - deleteById(id)     -> supprime un produit
 *   - count()            -> compte les produits
 *   - existsById(id)     -> teste l'existence d'un produit
 *
 * JpaRepository<Product, Long> signifie :
 *   - on gere l'entite "Product"
 *   - dont l'identifiant (cle primaire) est de type "Long"
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Vide volontairement : tout le CRUD de base est deja fourni par JpaRepository.
    // (On pourra ajouter ici des recherches sur mesure plus tard, ex: findByCategory.)
}
