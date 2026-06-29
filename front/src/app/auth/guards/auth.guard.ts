import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { AuthService } from "../data-access/auth.service";

/**
 * Garde de route : autorise l'acces seulement si l'utilisateur est connecte.
 * Sinon, on le redirige vers la page de connexion.
 *
 * On l'applique sur les routes a proteger (produits, panier, contact...).
 */
export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  // createUrlTree -> redirige proprement vers /login.
  return router.createUrlTree(["/login"]);
};
