import { Injectable, computed, signal } from "@angular/core";
import { Product } from "app/products/data-access/product.model";

/** Une ligne de panier : un produit + sa quantite. */
export interface CartItem {
  product: Product;
  quantity: number;
}

/**
 * Service du panier (en memoire, cote front).
 *
 * providedIn: "root" -> une seule instance partagee dans toute l'application,
 * donc le badge de la barre et la page panier voient le meme panier.
 *
 * On utilise les Signals : l'etat est reactif, et "totalItems"/"totalPrice"
 * se recalculent automatiquement a chaque modification du panier.
 */
@Injectable({ providedIn: "root" })
export class CartService {
  /** Cle de stockage du panier dans le localStorage du navigateur. */
  private static readonly STORAGE_KEY = "cart";

  // Etat prive (modifiable seulement par ce service) + version publique en lecture seule.
  // Au demarrage, on RESTAURE le panier depuis le localStorage (s'il existe) ->
  // ainsi le panier survit a un rafraichissement de page.
  private readonly _items = signal<CartItem[]>(this.loadFromStorage());
  public readonly items = this._items.asReadonly();

  /** Nombre total d'articles (somme des quantites) -> sert au badge. */
  public readonly totalItems = computed(() =>
    this._items().reduce((sum, item) => sum + item.quantity, 0),
  );

  /** Prix total du panier. */
  public readonly totalPrice = computed(() =>
    this._items().reduce((sum, item) => sum + item.product.price * item.quantity, 0),
  );

  /**
   * Ajoute un produit au panier.
   * S'il y est deja, on augmente sa quantite ; sinon on ajoute une nouvelle ligne.
   */
  public addToCart(product: Product, quantity = 1): void {
    this._items.update((items) => {
      const existing = items.find((i) => i.product.id === product.id);
      if (existing) {
        return items.map((i) =>
          i.product.id === product.id ? { ...i, quantity: i.quantity + quantity } : i,
        );
      }
      return [...items, { product, quantity }];
    });
    this.persist();
  }

  /** Retire completement un produit du panier. */
  public removeFromCart(productId: number): void {
    this._items.update((items) => items.filter((i) => i.product.id !== productId));
    this.persist();
  }

  /**
   * Change la quantite d'un produit.
   * Une quantite <= 0 retire le produit du panier.
   */
  public updateQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeFromCart(productId); // persiste deja
      return;
    }
    this._items.update((items) =>
      items.map((i) => (i.product.id === productId ? { ...i, quantity } : i)),
    );
    this.persist();
  }

  /** Vide le panier. */
  public clear(): void {
    this._items.set([]);
    this.persist();
  }

  // ----- Persistance dans le localStorage -----

  /** Sauvegarde l'etat courant du panier dans le localStorage. */
  private persist(): void {
    try {
      localStorage.setItem(CartService.STORAGE_KEY, JSON.stringify(this._items()));
    } catch {
      // Si le localStorage est indisponible (mode prive...), on ignore silencieusement.
    }
  }

  /** Lit le panier sauvegarde au demarrage (tableau vide si rien / erreur). */
  private loadFromStorage(): CartItem[] {
    try {
      const raw = localStorage.getItem(CartService.STORAGE_KEY);
      return raw ? (JSON.parse(raw) as CartItem[]) : [];
    } catch {
      return [];
    }
  }
}
