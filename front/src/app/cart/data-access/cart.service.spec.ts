import { Product } from "app/products/data-access/product.model";
import { CartService } from "./cart.service";

/** Fabrique un produit de test minimal (seuls id et price comptent ici). */
function makeProduct(id: number, price: number): Product {
  return {
    id,
    code: `code-${id}`,
    name: `Produit ${id}`,
    description: "",
    image: "",
    category: "Test",
    price,
    quantity: 0,
    internalReference: "",
    shellId: 0,
    inventoryStatus: "INSTOCK",
    rating: 0,
    createdAt: 0,
    updatedAt: 0,
  };
}

describe("CartService", () => {
  let service: CartService;

  beforeEach(() => {
    // On vide le localStorage pour isoler chaque test (le panier y est persiste).
    localStorage.clear();
    // Le service n'a aucune dependance -> on peut l'instancier directement.
    service = new CartService();
  });

  it("le panier est vide au depart", () => {
    expect(service.items().length).toBe(0);
    expect(service.totalItems()).toBe(0);
    expect(service.totalPrice()).toBe(0);
  });

  it("ajoute un produit au panier", () => {
    service.addToCart(makeProduct(1, 10));
    expect(service.items().length).toBe(1);
    expect(service.totalItems()).toBe(1);
  });

  it("cumule la quantite si on ajoute deux fois le meme produit", () => {
    const p = makeProduct(1, 10);
    service.addToCart(p, 2);
    service.addToCart(p, 3);
    expect(service.items().length).toBe(1); // toujours une seule ligne
    expect(service.totalItems()).toBe(5); // 2 + 3
  });

  it("met a jour la quantite d'un produit", () => {
    service.addToCart(makeProduct(1, 10), 1);
    service.updateQuantity(1, 4);
    expect(service.totalItems()).toBe(4);
  });

  it("une quantite <= 0 retire le produit", () => {
    service.addToCart(makeProduct(1, 10));
    service.updateQuantity(1, 0);
    expect(service.items().length).toBe(0);
  });

  it("retire un produit du panier", () => {
    service.addToCart(makeProduct(1, 10));
    service.addToCart(makeProduct(2, 20));
    service.removeFromCart(1);
    expect(service.items().length).toBe(1);
    expect(service.items()[0].product.id).toBe(2);
  });

  it("calcule le prix total", () => {
    service.addToCart(makeProduct(1, 10), 2); // 20
    service.addToCart(makeProduct(2, 5), 3); // 15
    expect(service.totalPrice()).toBe(35);
  });

  it("vide le panier", () => {
    service.addToCart(makeProduct(1, 10));
    service.clear();
    expect(service.items().length).toBe(0);
  });

  it("le panier est restaure depuis le localStorage (survit a un rechargement)", () => {
    service.addToCart(makeProduct(1, 10), 2);

    // Un NOUVEAU service (= comme apres un rechargement de page) doit retrouver le panier.
    const reloaded = new CartService();
    expect(reloaded.items().length).toBe(1);
    expect(reloaded.totalItems()).toBe(2);
    expect(reloaded.items()[0].product.id).toBe(1);
  });
});
