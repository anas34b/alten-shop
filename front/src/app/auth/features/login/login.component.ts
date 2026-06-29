import { Component, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { MessageService } from "primeng/api";
import { ButtonModule } from "primeng/button";
import { CardModule } from "primeng/card";
import { InputTextModule } from "primeng/inputtext";
import { ToastModule } from "primeng/toast";
import { AuthService } from "app/auth/data-access/auth.service";

/**
 * Page de connexion ET d'inscription (deux modes dans un seul ecran,
 * basculables par un bouton).
 */
@Component({
  selector: "app-login",
  standalone: true,
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.scss"],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ButtonModule,
    CardModule,
    InputTextModule,
    ToastModule,
  ],
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly messageService = inject(MessageService);

  /** Mode courant : true = inscription, false = connexion. */
  public readonly isRegisterMode = signal(false);

  /** Indique qu'une requete est en cours (desactive le bouton). */
  public readonly loading = signal(false);

  /** Formulaire de connexion. */
  public readonly loginForm = this.fb.group({
    email: ["", [Validators.required, Validators.email]],
    password: ["", [Validators.required]],
  });

  /** Formulaire d'inscription. */
  public readonly registerForm = this.fb.group({
    username: ["", [Validators.required]],
    firstname: ["", [Validators.required]],
    email: ["", [Validators.required, Validators.email]],
    password: ["", [Validators.required, Validators.minLength(6)]],
  });

  /** Bascule entre connexion et inscription. */
  public toggleMode(): void {
    this.isRegisterMode.update((v) => !v);
  }

  /** Soumission du formulaire de connexion. */
  public onLogin(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }
    const { email, password } = this.loginForm.getRawValue();
    this.loading.set(true);
    this.authService.login(email!, password!).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(["/products/list"]);
      },
      error: () => {
        this.loading.set(false);
        this.messageService.add({
          severity: "error",
          summary: "Connexion echouee",
          detail: "E-mail ou mot de passe incorrect.",
        });
      },
    });
  }

  /** Soumission du formulaire d'inscription (puis connexion automatique). */
  public onRegister(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }
    const payload = this.registerForm.getRawValue() as {
      username: string;
      firstname: string;
      email: string;
      password: string;
    };
    this.loading.set(true);
    this.authService.register(payload).subscribe({
      next: () => {
        // Compte cree : on connecte directement l'utilisateur.
        this.authService.login(payload.email, payload.password).subscribe({
          next: () => {
            this.loading.set(false);
            this.router.navigate(["/products/list"]);
          },
          error: () => this.loading.set(false),
        });
      },
      error: (err) => {
        this.loading.set(false);
        const detail =
          err.status === 409
            ? "Cet e-mail est deja utilise."
            : "Impossible de creer le compte.";
        this.messageService.add({ severity: "error", summary: "Inscription echouee", detail });
      },
    });
  }
}
