package com.alten.shop.product;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controleur REST des produits.
 *
 * @RestController        : cette classe expose des routes HTTP et renvoie du JSON.
 * @RequestMapping(...)   : toutes les routes ci-dessous commencent par "/api/products"
 *                          (c'est exactement l'URL que le service Angular appelle deja).
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    /** Acces a la base de donnees pour les produits. */
    private final ProductRepository repository;

    /**
     * Constructeur : Spring "injecte" automatiquement le repository ici
     * (on n'a pas a le creer nous-memes). C'est l'injection de dependances.
     */
    public ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * GET /api/products
     * Renvoie la liste de tous les produits.
     */
    @GetMapping
    public List<Product> getAll() {
        return repository.findAll();
    }

    /**
     * GET /api/products/{id}
     * Renvoie un seul produit, ou une erreur 404 s'il n'existe pas.
     * {id} dans l'URL est recupere par @PathVariable.
     */
    @GetMapping("/{id}")
    public Product getOne(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produit introuvable : " + id));
    }

    /**
     * POST /api/products
     * Cree un nouveau produit a partir du JSON envoye dans le corps de la requete.
     * @Valid     : declenche les validations de l'entite (ex: nom obligatoire).
     * @RequestBody: convertit le JSON recu en objet Product.
     * @ResponseStatus(CREATED) : repond avec le code HTTP 201 (et non 200) en cas de succes.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid @RequestBody Product product) {
        product.setId(null); // on force un nouvel id genere par la base (on ignore un id eventuellement envoye)
        return repository.save(product);
    }

    /**
     * PATCH /api/products/{id}
     * Met a jour PARTIELLEMENT un produit : seuls les champs fournis (non nuls)
     * sont modifies, les autres restent inchanges.
     */
    @PatchMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product changes) {
        // 1) On recupere le produit existant (ou 404 si absent).
        Product existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Produit introuvable : " + id));

        // 2) On applique uniquement les champs presents dans la requete.
        if (changes.getCode() != null) existing.setCode(changes.getCode());
        if (changes.getName() != null) existing.setName(changes.getName());
        if (changes.getDescription() != null) existing.setDescription(changes.getDescription());
        if (changes.getImage() != null) existing.setImage(changes.getImage());
        if (changes.getCategory() != null) existing.setCategory(changes.getCategory());
        if (changes.getPrice() != null) existing.setPrice(changes.getPrice());
        if (changes.getQuantity() != null) existing.setQuantity(changes.getQuantity());
        if (changes.getInternalReference() != null) existing.setInternalReference(changes.getInternalReference());
        if (changes.getShellId() != null) existing.setShellId(changes.getShellId());
        if (changes.getInventoryStatus() != null) existing.setInventoryStatus(changes.getInventoryStatus());
        if (changes.getRating() != null) existing.setRating(changes.getRating());

        // 3) On sauvegarde : Hibernate met a jour la ligne et rafraichit updatedAt (@PreUpdate).
        return repository.save(existing);
    }

    /**
     * DELETE /api/products/{id}
     * Supprime un produit. Repond avec le code 204 (No Content) en cas de succes.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Produit introuvable : " + id);
        }
        repository.deleteById(id);
    }
}
