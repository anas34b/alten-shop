import { Component, OnInit, computed, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { CartService } from "app/cart/data-access/cart.service";
import { Product } from "app/products/data-access/product.model";
import { ProductsService } from "app/products/data-access/products.service";
import { ProductFormComponent } from "app/products/ui/product-form/product-form.component";
import { MessageService } from "primeng/api";
import { ButtonModule } from "primeng/button";
import { CardModule } from "primeng/card";
import { DataViewModule } from 'primeng/dataview';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { RatingModule } from 'primeng/rating';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';

const emptyProduct: Product = {
  id: 0,
  code: "",
  name: "",
  description: "",
  image: "",
  category: "",
  price: 0,
  quantity: 0,
  internalReference: "",
  shellId: 0,
  inventoryStatus: "INSTOCK",
  rating: 0,
  createdAt: 0,
  updatedAt: 0,
};

@Component({
  selector: "app-product-list",
  templateUrl: "./product-list.component.html",
  styleUrls: ["./product-list.component.scss"],
  standalone: true,
  // CommonModule = pipes (currency...) ; FormsModule = ngModel (utilise par p-rating) ;
  // RatingModule = etoiles ; TagModule = badge de stock colore.
  imports: [
    CommonModule,
    FormsModule,
    DataViewModule,
    CardModule,
    ButtonModule,
    DialogModule,
    DropdownModule,
    InputTextModule,
    RatingModule,
    TagModule,
    ToastModule,
    ProductFormComponent,
  ],
})
export class ProductListComponent implements OnInit {
  private readonly productsService = inject(ProductsService);
  private readonly cartService = inject(CartService);
  private readonly messageService = inject(MessageService);

  public readonly products = this.productsService.products;

  // ----- Filtrage (bonus) -----
  /** Texte de recherche (par nom de produit). */
  public readonly searchTerm = signal("");
  /** Categorie selectionnee (null = toutes). */
  public readonly selectedCategory = signal<string | null>(null);

  /** Liste des categories distinctes, pour le menu deroulant. */
  public readonly categories = computed(() =>
    Array.from(new Set(this.products().map((p) => p.category))).sort(),
  );

  /** Produits filtres selon la recherche et la categorie (recalcul automatique). */
  public readonly filteredProducts = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    const category = this.selectedCategory();
    return this.products().filter(
      (p) =>
        (term === "" || p.name.toLowerCase().includes(term)) &&
        (category === null || p.category === category),
    );
  });

  public isDialogVisible = false;
  public isCreation = false;
  public readonly editedProduct = signal<Product>(emptyProduct);

  // URL de base des images produits (banque d'images de demonstration PrimeNG,
  // ces produits sont les exemples officiels de PrimeNG : bamboo-watch.jpg, etc.).
  public readonly imageBaseUrl = "https://primefaces.org/cdn/primeng/images/demo/product/";

  ngOnInit() {
    this.productsService.get().subscribe();
  }

  /** Libelle francais du statut de stock, affiche dans le badge. */
  public stockLabel(status: Product["inventoryStatus"]): string {
    switch (status) {
      case "INSTOCK": return "En stock";
      case "LOWSTOCK": return "Stock faible";
      case "OUTOFSTOCK": return "Rupture";
      default: return status;
    }
  }

  /** Couleur du badge (severity PrimeNG) selon le statut de stock. */
  public stockSeverity(status: Product["inventoryStatus"]): "success" | "warning" | "danger" | "info" {
    switch (status) {
      case "INSTOCK": return "success";   // vert
      case "LOWSTOCK": return "warning";  // orange
      case "OUTOFSTOCK": return "danger"; // rouge
      default: return "info";
    }
  }

  public onCreate() {
    this.isCreation = true;
    this.isDialogVisible = true;
    this.editedProduct.set(emptyProduct);
  }

  public onUpdate(product: Product) {
    this.isCreation = false;
    this.isDialogVisible = true;
    this.editedProduct.set(product);
  }

  public onDelete(product: Product) {
    this.productsService.delete(product.id).subscribe();
  }

  /** Ajoute le produit au panier et affiche une confirmation. */
  public onAddToCart(product: Product) {
    this.cartService.addToCart(product);
    this.messageService.add({
      severity: "success",
      summary: "Panier",
      detail: `"${product.name}" ajouté au panier`,
      life: 2000,
    });
  }

  public onSave(product: Product) {
    if (this.isCreation) {
      this.productsService.create(product).subscribe();
    } else {
      this.productsService.update(product).subscribe();
    }
    this.closeDialog();
  }

  public onCancel() {
    this.closeDialog();
  }

  private closeDialog() {
    this.isDialogVisible = false;
  }
}
