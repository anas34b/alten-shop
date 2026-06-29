import {
  Component,
  inject,
} from "@angular/core";
import { Router, RouterModule } from "@angular/router";
import { AuthService } from "app/auth/data-access/auth.service";
import { CartService } from "app/cart/data-access/cart.service";
import { BadgeModule } from 'primeng/badge';
import { ButtonModule } from 'primeng/button';
import { SplitterModule } from 'primeng/splitter';
import { ToolbarModule } from 'primeng/toolbar';
import { PanelMenuComponent } from "./shared/ui/panel-menu/panel-menu.component";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.scss"],
  standalone: true,
  imports: [RouterModule, SplitterModule, ToolbarModule, BadgeModule, ButtonModule, PanelMenuComponent],
})
export class AppComponent {
  title = "ALTEN SHOP";

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  // Nombre d'articles dans le panier (signal calcule) -> alimente le badge.
  public readonly cartCount = inject(CartService).totalItems;

  // Etat de connexion + e-mail, pour afficher/cacher les elements de la barre.
  public readonly isAuthenticated = this.authService.isAuthenticated;
  public readonly userEmail = this.authService.email;

  /** Deconnexion : on efface le token et on retourne a la page de connexion. */
  public logout(): void {
    this.authService.logout();
    this.router.navigate(["/login"]);
  }
}
