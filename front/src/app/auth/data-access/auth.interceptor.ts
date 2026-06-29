import { HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { Router } from "@angular/router";
import { catchError, throwError } from "rxjs";
import { AuthService } from "./auth.service";

/**
 * Interceptor HTTP : s'execute sur CHAQUE requete sortante.
 *
 * 1) Si un token est present, on l'ajoute dans l'en-tete "Authorization: Bearer ...".
 *    -> plus besoin de le faire manuellement dans chaque appel.
 * 2) Si le serveur repond 401 (non authentifie / token expire), on deconnecte
 *    l'utilisateur et on le renvoie vers la page de connexion.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const token = authService.token();

  // On clone la requete pour y ajouter l'en-tete (les requetes sont immuables).
  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError((error) => {
      if (error.status === 401) {
        authService.logout();
        router.navigate(["/login"]);
      }
      // On relaie l'erreur pour que l'appelant puisse la traiter aussi.
      return throwError(() => error);
    }),
  );
};
