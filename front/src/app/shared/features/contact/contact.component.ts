import { Component, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormBuilder, ReactiveFormsModule, Validators } from "@angular/forms";
import { MessageService } from "primeng/api";
import { ButtonModule } from "primeng/button";
import { CardModule } from "primeng/card";
import { InputTextModule } from "primeng/inputtext";
import { InputTextareaModule } from "primeng/inputtextarea";
import { ToastModule } from "primeng/toast";

/**
 * Page "Contact" : un formulaire email + message avec validations.
 *
 * On utilise les "Reactive Forms" (FormBuilder) plutot que ngModel :
 * mieux adapte a plusieurs validations, et facile a tester.
 */
@Component({
  selector: "app-contact",
  standalone: true,
  templateUrl: "./contact.component.html",
  styleUrls: ["./contact.component.scss"],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ButtonModule,
    CardModule,
    InputTextModule,
    InputTextareaModule,
    ToastModule,
  ],
})
export class ContactComponent {
  private readonly fb = inject(FormBuilder);
  private readonly messageService = inject(MessageService);

  /**
   * Le formulaire et ses regles de validation :
   *  - email   : obligatoire + format e-mail valide
   *  - message : obligatoire + 300 caracteres maximum
   */
  public readonly form = this.fb.group({
    email: ["", [Validators.required, Validators.email]],
    message: ["", [Validators.required, Validators.maxLength(300)]],
  });

  /** Raccourcis pour acceder aux champs depuis le template. */
  public get email() {
    return this.form.controls.email;
  }
  public get message() {
    return this.form.controls.message;
  }

  /** Soumission du formulaire. */
  public onSubmit(): void {
    // Securite : si invalide, on marque les champs comme "touches" pour afficher les erreurs, puis on stoppe.
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    // (Plus tard : on enverra reellement au back-end.)
    // Pour l'instant, on confirme a l'utilisateur, comme demande par le sujet.
    this.messageService.add({
      severity: "success",
      summary: "Succès",
      detail: "Demande de contact envoyée avec succès",
    });

    // On vide le formulaire apres envoi.
    this.form.reset();
  }
}
