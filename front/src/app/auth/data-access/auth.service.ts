import { HttpClient } from "@angular/common/http";
import { Injectable, computed, inject, signal } from "@angular/core";
import { Observable, map, tap } from "rxjs";
import { environment } from "environments/environment";

/** Payload attendu par la route d'inscription du back. */
export interface RegisterPayload {
  username: string;
  firstname: string;
  email: string;
  password: string;
}

/**
 * Service d'authentification (cote front).
 *
 * - login()    : POST /token  -> recupere et stocke le token JWT
 * - register() : POST /account -> cree un compte
 * - logout()   : efface le token
 * - le token est garde en memoire (signal) ET dans le localStorage,
 *   pour rester connecte apres un rechargement de page.
 */
@Injectable({ providedIn: "root" })
export class AuthService {
  private readonly http = inject(HttpClient);
  private static readonly TOKEN_KEY = "token";

  // Etat du token : initialise depuis le localStorage (s'il existe).
  private readonly _token = signal<string | null>(
    localStorage.getItem(AuthService.TOKEN_KEY),
  );

  /** Vrai si un utilisateur est connecte. */
  public readonly isAuthenticated = computed(() => this._token() !== null);

  /** E-mail de l'administrateur (seul habilite a gerer les produits). */
  private static readonly ADMIN_EMAIL = "admin@admin.com";

  /** L'e-mail de l'utilisateur, extrait du token JWT (champ "sub"). */
  public readonly email = computed(() => {
    const token = this._token();
    if (!token) return null;
    try {
      // Un JWT = 3 parties separees par des points ; la 2e (payload) est en base64.
      const payload = JSON.parse(atob(token.split(".")[1]));
      return payload.sub as string;
    } catch {
      return null;
    }
  });

  /** Vrai si l'utilisateur connecte est l'administrateur. */
  public readonly isAdmin = computed(() => this.email() === AuthService.ADMIN_EMAIL);

  /** Renvoie le token courant (utilise par l'interceptor HTTP). */
  public token(): string | null {
    return this._token();
  }

  /** Connexion : recupere un token et le stocke. */
  public login(email: string, password: string): Observable<void> {
    return this.http
      .post<{ token: string }>(`${environment.apiUrl}/token`, { email, password })
      .pipe(
        tap((response) => this.setToken(response.token)),
        map(() => undefined),
      );
  }

  /** Inscription : cree un nouveau compte. */
  public register(payload: RegisterPayload): Observable<unknown> {
    return this.http.post(`${environment.apiUrl}/account`, payload);
  }

  /** Deconnexion : on oublie le token. */
  public logout(): void {
    this._token.set(null);
    localStorage.removeItem(AuthService.TOKEN_KEY);
  }

  /** Enregistre le token en memoire et dans le localStorage. */
  private setToken(token: string): void {
    this._token.set(token);
    localStorage.setItem(AuthService.TOKEN_KEY, token);
  }
}
