import { Routes } from "@angular/router";
import { authGuard } from "./auth/guards/auth.guard";
import { LoginComponent } from "./auth/features/login/login.component";
import { CartComponent } from "./cart/features/cart/cart.component";
import { ContactComponent } from "./shared/features/contact/contact.component";
import { HomeComponent } from "./shared/features/home/home.component";

export const APP_ROUTES: Routes = [
  // Page de connexion : accessible SANS etre connecte.
  {
    path: "login",
    component: LoginComponent,
  },
  // Routes protegees : il faut etre connecte (sinon redirection vers /login).
  {
    path: "home",
    component: HomeComponent,
    canActivate: [authGuard],
  },
  {
    path: "products",
    canActivate: [authGuard],
    loadChildren: () =>
      import("./products/products.routes").then((m) => m.PRODUCTS_ROUTES)
  },
  {
    path: "cart",
    component: CartComponent,
    canActivate: [authGuard],
  },
  {
    path: "contact",
    component: ContactComponent,
    canActivate: [authGuard],
  },
  { path: "", redirectTo: "home", pathMatch: "full" },
];
