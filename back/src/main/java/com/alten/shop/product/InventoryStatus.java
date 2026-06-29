package com.alten.shop.product;

/**
 * Les trois etats de stock possibles d'un produit.
 *
 * Une "enum" (enumeration) = une liste FERMEE de valeurs autorisees.
 * Avantage : impossible d'enregistrer un statut invalide (ex: "EN_STOCK"),
 * seules ces trois valeurs sont acceptees a la compilation.
 *
 * Ces noms correspondent exactement au modele du front (product.model.ts) :
 *   inventoryStatus: "INSTOCK" | "LOWSTOCK" | "OUTOFSTOCK"
 */
public enum InventoryStatus {
    INSTOCK,    // en stock
    LOWSTOCK,   // stock faible
    OUTOFSTOCK  // rupture de stock
}
