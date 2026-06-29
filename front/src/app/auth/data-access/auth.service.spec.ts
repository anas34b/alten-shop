import { provideHttpClient } from "@angular/common/http";
import { HttpTestingController, provideHttpClientTesting } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { AuthService } from "./auth.service";

/** Construit un faux token JWT contenant l'e-mail donne dans le champ "sub". */
function fakeToken(email: string): string {
  const payload = btoa(JSON.stringify({ sub: email }));
  return `header.${payload}.signature`;
}

describe("AuthService", () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [AuthService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it("non authentifie au depart", () => {
    expect(service.isAuthenticated()).toBe(false);
    expect(service.token()).toBeNull();
  });

  it("login envoie POST /token, stocke le token et authentifie", () => {
    const token = fakeToken("admin@admin.com");
    let completed = false;

    service.login("admin@admin.com", "admin123").subscribe(() => (completed = true));

    const req = httpMock.expectOne("/token");
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual({ email: "admin@admin.com", password: "admin123" });
    req.flush({ token });

    expect(completed).toBe(true);
    expect(service.isAuthenticated()).toBe(true);
    expect(service.email()).toBe("admin@admin.com");
    expect(service.isAdmin()).toBe(true); // admin@admin.com -> admin
    expect(localStorage.getItem("token")).toBe(token);
  });

  it("un utilisateur non-admin n'est PAS admin", () => {
    service.login("bob@test.com", "secret123").subscribe();
    httpMock.expectOne("/token").flush({ token: fakeToken("bob@test.com") });

    expect(service.isAuthenticated()).toBe(true);
    expect(service.isAdmin()).toBe(false);
  });

  it("logout efface le token", () => {
    service.login("a@b.com", "x").subscribe();
    httpMock.expectOne("/token").flush({ token: fakeToken("a@b.com") });

    service.logout();

    expect(service.isAuthenticated()).toBe(false);
    expect(localStorage.getItem("token")).toBeNull();
  });

  it("register envoie POST /account avec le bon payload", () => {
    const payload = { username: "u", firstname: "f", email: "a@b.com", password: "secret123" };
    let ok = false;

    service.register(payload).subscribe(() => (ok = true));

    const req = httpMock.expectOne("/account");
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual(payload);
    req.flush({ id: 1 });

    expect(ok).toBe(true);
  });
});
