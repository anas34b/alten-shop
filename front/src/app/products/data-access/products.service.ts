import { Injectable, inject, signal } from "@angular/core";
import { Product } from "./product.model";
import { HttpClient } from "@angular/common/http";
import { catchError, Observable, tap } from "rxjs";
import { environment } from "environments/environment";

@Injectable({
    providedIn: "root"
}) export class ProductsService {

    private readonly http = inject(HttpClient);
    // En dev apiUrl="" -> "/api/products" (proxy) ; en prod -> URL complete du back.
    private readonly path = `${environment.apiUrl}/api/products`;
    
    private readonly _products = signal<Product[]>([]);

    public readonly products = this._products.asReadonly();

    public get(): Observable<Product[]> {
        return this.http.get<Product[]>(this.path).pipe(
            catchError((error) => {
                return this.http.get<Product[]>("assets/products.json");
            }),
            tap((products) => this._products.set(products)),
        );
    }

    // Sur create/update/delete : PAS de catchError -> si le serveur refuse (ex: 403 pour un
    // non-admin), l'erreur remonte au composant (qui affiche un message) et la liste locale
    // n'est PAS modifiee. La mise a jour optimiste (tap) ne se produit que sur succes reel.

    public create(product: Product): Observable<boolean> {
        return this.http.post<boolean>(this.path, product).pipe(
            tap(() => this._products.update(products => [product, ...products])),
        );
    }

    public update(product: Product): Observable<boolean> {
        return this.http.patch<boolean>(`${this.path}/${product.id}`, product).pipe(
            tap(() => this._products.update(products => {
                return products.map(p => p.id === product.id ? product : p)
            })),
        );
    }

    public delete(productId: number): Observable<boolean> {
        return this.http.delete<boolean>(`${this.path}/${productId}`).pipe(
            tap(() => this._products.update(products => products.filter(product => product.id !== productId))),
        );
    }
}