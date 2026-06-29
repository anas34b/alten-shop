package com.alten.shop.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Represente un PRODUIT.
 *
 * @Entity dit a Hibernate : "transforme cette classe en table SQL".
 * Chaque champ ci-dessous = une colonne de la table "products".
 * Les 14 champs sont IDENTIQUES au modele du front (product.model.ts),
 * pour que back et front parlent exactement le meme langage.
 */
@Entity
@Table(name = "products") // nom de la table en base
public class Product {

    /**
     * Identifiant unique.
     * @Id              : c'est la cle primaire de la table.
     * @GeneratedValue  : la base genere l'id automatiquement (1, 2, 3, ...).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Code produit (reference courte, ex: "f230fh0g3"). */
    private String code;

    /**
     * Nom du produit. Obligatoire :
     * @NotBlank refuse une valeur vide ou composee uniquement d'espaces (validation a la creation).
     * @Column(nullable = false) interdit aussi le vide cote base de donnees.
     */
    @NotBlank(message = "Le nom du produit est obligatoire")
    @Column(nullable = false)
    private String name;

    /** Description longue du produit. */
    private String description;

    /** Nom du fichier image (ex: "bamboo-watch.jpg"). */
    private String image;

    /** Categorie (ex: "Accessories", "Fitness", "Clothing", "Electronics"). */
    private String category;

    /** Prix en euros. Double = nombre a virgule. */
    private Double price;

    /** Quantite en stock. Vaut 0 par defaut si non fournie. */
    private Integer quantity = 0;

    /** Reference interne (ex: "REF-123-456"). */
    private String internalReference;

    /** Identifiant de l'etagere/rayon (donnee metier du sujet). */
    private Integer shellId;

    /**
     * Statut de stock, limite aux 3 valeurs de l'enum InventoryStatus.
     * @Enumerated(EnumType.STRING) : stocke le TEXTE ("INSTOCK") en base
     * plutot qu'un numero (0,1,2) -> bien plus lisible et robuste.
     */
    @Enumerated(EnumType.STRING)
    private InventoryStatus inventoryStatus;

    /** Note du produit (0 a 5). */
    private Integer rating;

    /** Date de creation, en millisecondes depuis 1970 (comme le front : un "number"). */
    private Long createdAt;

    /** Date de derniere modification, meme format. */
    private Long updatedAt;

    // ------------------------------------------------------------------
    //  GESTION AUTOMATIQUE DES DATES
    //  Ces methodes sont appelees par Hibernate au bon moment.
    // ------------------------------------------------------------------

    /**
     * Appelee juste AVANT le premier enregistrement en base.
     * On positionne createdAt et updatedAt a "maintenant",
     * sauf si une valeur a deja ete fournie (cas des donnees de demo).
     */
    @PrePersist
    public void onCreate() {
        long now = System.currentTimeMillis();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    /**
     * Appelee juste AVANT chaque mise a jour : on rafraichit updatedAt.
     */
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = System.currentTimeMillis();
    }

    // ------------------------------------------------------------------
    //  GETTERS / SETTERS
    //  Methodes standard pour LIRE (get) et ECRIRE (set) chaque champ.
    //  Indispensables pour que Spring/Jackson lisent et remplissent l'objet
    //  lors des conversions JSON <-> Java.
    // ------------------------------------------------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getInternalReference() { return internalReference; }
    public void setInternalReference(String internalReference) { this.internalReference = internalReference; }

    public Integer getShellId() { return shellId; }
    public void setShellId(Integer shellId) { this.shellId = shellId; }

    public InventoryStatus getInventoryStatus() { return inventoryStatus; }
    public void setInventoryStatus(InventoryStatus inventoryStatus) { this.inventoryStatus = inventoryStatus; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }
}
