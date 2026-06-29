import { TestBed } from "@angular/core/testing";
import { provideNoopAnimations } from "@angular/platform-browser/animations";
import { MessageService } from "primeng/api";
import { ContactComponent } from "./contact.component";

/**
 * Tests unitaires du formulaire de contact.
 * On verifie les regles de validation et l'envoi du toast de succes.
 */
describe("ContactComponent", () => {
  let component: ContactComponent;
  let messageService: MessageService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContactComponent],
      // provideNoopAnimations : evite d'avoir besoin du vrai moteur d'animations PrimeNG en test.
      providers: [MessageService, provideNoopAnimations()],
    }).compileComponents();

    const fixture = TestBed.createComponent(ContactComponent);
    component = fixture.componentInstance;
    messageService = TestBed.inject(MessageService);
  });

  it("le formulaire est invalide quand il est vide", () => {
    expect(component.form.invalid).toBe(true);
  });

  it("un email mal forme rend le champ email invalide", () => {
    component.form.setValue({ email: "pas-un-email", message: "Bonjour" });
    expect(component.email.invalid).toBe(true);
  });

  it("un message de plus de 300 caracteres est invalide", () => {
    component.form.setValue({ email: "a@b.com", message: "x".repeat(301) });
    expect(component.message.invalid).toBe(true);
  });

  it("avec email et message valides, le formulaire est valide", () => {
    component.form.setValue({ email: "a@b.com", message: "Bonjour" });
    expect(component.form.valid).toBe(true);
  });

  it("onSubmit sur un formulaire valide envoie un toast de succes", () => {
    const spy = jest.spyOn(messageService, "add");
    component.form.setValue({ email: "a@b.com", message: "Bonjour" });

    component.onSubmit();

    expect(spy).toHaveBeenCalledTimes(1);
    const toast = spy.mock.calls[0][0];
    expect(toast.detail).toContain("Demande de contact envoyée avec succès");
  });

  it("onSubmit sur un formulaire invalide n'envoie PAS de toast", () => {
    const spy = jest.spyOn(messageService, "add");
    component.onSubmit(); // formulaire vide
    expect(spy).not.toHaveBeenCalled();
  });
});
