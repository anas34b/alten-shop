import { Component, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { CartService } from "app/cart/data-access/cart.service";
import { ButtonModule } from "primeng/button";
import { InputNumberModule } from "primeng/inputnumber";
import { TableModule } from "primeng/table";

/**
 * Page "Mon panier" : affiche les produits du panier, permet d'ajuster
 * les quantites, de retirer un produit, et montre le total.
 */
@Component({
  selector: "app-cart",
  standalone: true,
  templateUrl: "./cart.component.html",
  styleUrls: ["./cart.component.scss"],
  imports: [CommonModule, FormsModule, ButtonModule, InputNumberModule, TableModule],
})
export class CartComponent {
  private readonly cartService = inject(CartService);

  // On expose directement les signaux du service (lecture seule).
  public readonly items = this.cartService.items;
  public readonly totalItems = this.cartService.totalItems;
  public readonly totalPrice = this.cartService.totalPrice;

  public readonly imageBaseUrl = "https://primefaces.org/cdn/primeng/images/demo/product/";

  /** Quand l'utilisateur change la quantite d'une ligne. */
  public onQuantityChange(productId: number, quantity: number): void {
    this.cartService.updateQuantity(productId, quantity);
  }

  /** Retire un produit du panier. */
  public onRemove(productId: number): void {
    this.cartService.removeFromCart(productId);
  }
}
