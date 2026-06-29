# Avancement du projet — ALTEN SHOP

> Journal de bord du développement. Mis à jour à chaque étape.

## Stack utilisée
- **Front** : Angular 18 + PrimeNG 17 (fourni, non modifié pour l'instant)
- **Back** : Java 21 + Spring Boot 4.1 + Spring Data JPA + base H2 (dev)
- **Base de données** : H2 en mémoire (dev) → PostgreSQL/Supabase (prod, plus tard)
- ⚠️ Le back tourne sur le **port 8081** (le 8080 est occupé par un service WSL/Docker sur cette machine)

---

## Comment lancer et tester le back-end soi-même

### Démarrer le serveur
```bash
cd back
./mvnw spring-boot:run
```
Le serveur écoute sur http://localhost:8081 (laisser le terminal ouvert ; `Ctrl+C` pour arrêter).

### Démarrer via Docker (alternative — pas besoin de Java/Maven installés)
```bash
cd back
docker build -t alten-shop-back:latest .            # construit l'image (~quelques min la 1re fois)
docker run -d --name alten-shop -p 8082:8081 alten-shop-back:latest   # lance le conteneur
# L'API est alors sur http://localhost:8082  (8082 = port hôte -> 8081 dans le conteneur)
docker logs -f alten-shop                            # voir les logs
docker rm -f alten-shop                              # arrêter et supprimer le conteneur
```

### 1) Tester dans le NAVIGATEUR (lecture simple)
- http://localhost:8081/ping → affiche `pong`
- http://localhost:8081/api/products → la liste JSON des 30 produits
- http://localhost:8081/api/products/1 → le produit n°1

### 2) Voir les données dans la CONSOLE H2 (interface visuelle de la base)
- Ouvrir http://localhost:8081/h2-console
- Renseigner :
  - **JDBC URL** : `jdbc:h2:mem:shopdb`
  - **User Name** : `sa`
  - **Password** : (vide)
- Cliquer **Connect**, puis exécuter : `SELECT * FROM products;`

### 3) Tester création / modification / suppression (avec curl ou Postman)
```bash
# Créer un produit (réponse attendue : HTTP 201)
curl -X POST http://localhost:8081/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","price":10.0,"category":"Fitness","inventoryStatus":"INSTOCK"}'

# Modifier partiellement le produit d'id 5 (réponse : HTTP 200)
curl -X PATCH http://localhost:8081/api/products/5 \
  -H "Content-Type: application/json" \
  -d '{"price":99.9,"inventoryStatus":"LOWSTOCK"}'

# Supprimer le produit d'id 5 (réponse : HTTP 204)
curl -X DELETE http://localhost:8081/api/products/5
```
> Astuce : la base H2 étant "en mémoire", elle repart vide (puis re-remplie avec les 30 produits)
> à chaque redémarrage du serveur. Pratique pour tester proprement.

### 4) Tester l'authentification (étape 3)
```bash
# Créer un compte (réponse : HTTP 201, sans le mot de passe)
curl -X POST http://localhost:8081/account \
  -H "Content-Type: application/json" \
  -d '{"username":"anas","firstname":"Anas","email":"anas@test.com","password":"secret123"}'

# Se connecter -> renvoie un token JWT (HTTP 200)
curl -X POST http://localhost:8081/token \
  -H "Content-Type: application/json" \
  -d '{"email":"anas@test.com","password":"secret123"}'
```
> Dans la console H2, `SELECT * FROM users;` montre le mot de passe HACHÉ (`$2a$...`), jamais en clair.

### 5) Tester la sécurité (étape 4) — le token est désormais OBLIGATOIRE
Un compte admin est créé au démarrage : **admin@admin.com / admin123**.
```bash
# 1) Se connecter en admin et récupérer le token dans une variable
TOKEN=$(curl -s -X POST http://localhost:8081/token \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@admin.com","password":"admin123"}' | sed 's/.*"token":"//;s/".*//')

# 2) Lire les produits SANS token -> 401 (refusé)
curl -i http://localhost:8081/api/products

# 3) Lire AVEC le token -> 200
curl http://localhost:8081/api/products -H "Authorization: Bearer $TOKEN"

# 4) Créer un produit en admin -> 201
curl -X POST http://localhost:8081/api/products \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"name":"Nouveau","price":50.0,"category":"Electronics","inventoryStatus":"INSTOCK"}'
```
> Règles : GET produits = tout utilisateur connecté (401 sinon) ; POST/PATCH/DELETE = admin uniquement
> (403 pour un utilisateur normal connecté).
> 💡 Piège rencontré et corrigé : un refus 403 déclenchait une redirection interne `/error` re-contrôlée
> sans auth → transformée à tort en 401. Réglé en excluant les dispatch `ERROR`/`FORWARD` de la sécurité.

### 6) Tester panier & wishlist (étape 5) — token requis
```bash
TOKEN=$(curl -s -X POST http://localhost:8081/token -H "Content-Type: application/json" \
  -d '{"email":"admin@admin.com","password":"admin123"}' | sed 's/.*"token":"//;s/".*//')

# Ajouter le produit 1 (quantité 2) au panier -> 201 ; ré-ajouter cumule la quantité
curl -X POST http://localhost:8081/cart -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" -d '{"productId":1,"quantity":2}'
# Voir le panier
curl http://localhost:8081/cart -H "Authorization: Bearer $TOKEN"
# Retirer le produit 1 -> 204
curl -X DELETE http://localhost:8081/cart/1 -H "Authorization: Bearer $TOKEN"

# Wishlist (même principe, sans quantité)
curl -X POST http://localhost:8081/wishlist -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" -d '{"productId":5}'
curl http://localhost:8081/wishlist -H "Authorization: Bearer $TOKEN"
```
> L'utilisateur est déduit du token : chacun ne voit que SON panier / SA wishlist.

### 7) Documentation Swagger (étape 7)
Démarrer le serveur, puis ouvrir dans le navigateur :
- **http://localhost:8081/swagger-ui/index.html** → l'interface Swagger (toutes les routes, testables)
- http://localhost:8081/v3/api-docs → la spec OpenAPI au format JSON
> Pour tester une route protégée : se connecter via `/token`, copier le token, cliquer sur
> **Authorize** (en haut à droite), coller le token → toutes les requêtes seront authentifiées.

---

## Tests automatisés

On écrit les tests **au fur et à mesure** (à chaque étape), pas à la fin.
Lancer tous les tests :
```bash
cd back
./mvnw test
```
- Tests unitaires (suffixe `*Test`, ex: `JwtServiceTest`) : testent une classe isolée, sans Spring.
- Tests d'intégration (`*IntegrationTest`) : démarrent l'app + H2 et appellent les routes via MockMvc.
- ⚠️ Convention Maven : `mvn test` exécute les classes `*Test`/`*Tests` ; les classes `*IT` ne tournent
  qu'avec `mvn verify` (plugin Failsafe). On nomme donc nos tests d'intégration `*IntegrationTest`.

État actuel : **10 tests, tous au vert** (produits + auth + JWT + contexte).

## Étapes du développement back-end

- [x] **Étape 1 — Squelette Spring Boot** : projet créé, serveur qui démarre, endpoint `/ping`, base H2 + console.
- [x] **Étape 2 — CRUD Produits** : entité `Product`, repository, contrôleur REST `/api/products`
      (GET liste, GET par id, POST, PATCH, DELETE), validation, et chargement des 30 produits au démarrage.
- [x] **Étape 3 — Utilisateurs + connexion JWT** : entité `User`, routes `POST /account` et `POST /token`,
      mots de passe hachés (BCrypt), génération du token JWT (lib jjwt), config Spring Security
      (encodeur BCrypt + routes ouvertes temporairement).
- [x] **Étape 4 — Sécurité** : filtre JWT, routes protégées (connexion obligatoire pour lire les produits),
      création/modif/suppression réservées à `admin@admin.com`. Compte admin créé automatiquement au
      démarrage (`admin@admin.com` / `admin123`). 15 tests verts.
- [x] **Étape 5 — Panier + liste d'envies** par utilisateur : entités `CartItem` / `WishlistItem`,
      repositories, services, contrôleurs (`/cart`, `/wishlist`), DTOs, et `CurrentUserService`
      (utilisateur déduit du token → isolation entre comptes). 24 tests verts.
- [x] **Étape 6 — Dockerisation** du back : `Dockerfile` multi-étapes (build JDK → runtime JRE),
      `.dockerignore`, image `alten-shop-back:latest` (~381 Mo) construite et testée en conteneur.
- [x] **Étape 7 — Swagger** : springdoc-openapi 2.8.6 (compatible Boot 4 !), `OpenApiConfig` avec
      schéma de sécurité Bearer JWT (bouton « Authorize »). UI sur `/swagger-ui/index.html`,
      doc JSON sur `/v3/api-docs`. Tests automatisés déjà faits au fur et à mesure (24 tests).

## Front-end
- [x] **F1 — Affichage produit complet** : grille de cartes (image via CDN PrimeNG, prix en €,
      note en étoiles, badge de stock coloré INSTOCK/LOWSTOCK/OUTOFSTOCK), CRUD conservé. Vérifié visuellement.
- [x] **F2 — Page + menu Contact** : menu « Contact », formulaire réactif validé (email obligatoire+format,
      message obligatoire + 300 max, compteur, bouton désactivé si invalide, toast de succès). Vérifié
      visuellement + **6 tests unitaires Jest** verts.
- [x] **F3 — Panier** : `CartService` (signals : items, totalItems, totalPrice), bouton « Ajouter au panier »
      + toast sur chaque produit, **badge** sur l'icône panier de la barre, page panier `/cart` (tableau,
      quantité ajustable +/−, retrait, total), entrée de menu « Panier ». **Persisté dans le localStorage**
      (survit au rechargement de page). Vérifié visuellement (parcours piloté) + **9 tests Jest** du service.
- [x] **F4 — Bonus** : pagination du `p-dataView` (8 par page) + barre de recherche par nom + filtre par
      catégorie (signaux + liste calculée) + message « aucun résultat ». Vérifié live.
- [x] **F5 — Branchement front ↔ back** : proxy de dev (`proxy.conf.json` : `/api`,`/token`,`/account`→8081),
      `AuthService` (login/register/logout, token en localStorage), interceptor JWT (ajoute le Bearer + gère
      le 401), guard de route (redirige vers `/login`), page **connexion/inscription**, email + déconnexion
      dans la barre. Vérifié live (parcours piloté : non connecté → /login → connexion admin → 30 produits
      du vrai back). 4 tests Jest du `AuthService`.
- [ ] **Déploiement** : front sur Vercel, back sur Render, base sur Supabase.

### Lancer le front
```bash
cd front
npm start          # http://localhost:4200  (la page produits est sur /products/list)
```

### Tester le front (Jest)
```bash
cd front
npm test           # = jest  (tourne sans navigateur)
```

### Lancer TOUTE l'appli (front + back ensemble)
```bash
# 1) Le back (au choix) :
cd back && ./mvnw spring-boot:run        # OU :  docker run -d -p 8081:8081 alten-shop-back:latest
# 2) Le front (avec proxy vers le back) :
cd front && npm start                    # http://localhost:4200
```
> Le front (`npm start`) utilise `proxy.conf.json` pour rediriger `/api`, `/token`, `/account` vers le
> back (8081). Au 1er accès, on est redirigé vers **/login**. Se connecter avec **admin@admin.com /
> admin123** (ou créer un compte). Le token JWT est ensuite envoyé automatiquement sur chaque requête.
> ⚠️ Le template fournissait une config **Karma** (`karma.conf.js`, `test.ts`) mais SANS les dépendances
> (config « morte »). Karma étant **déprécié**, on l'a remplacé par **Jest** (`jest-preset-angular`) :
> moderne, rapide, sans navigateur. Fichiers : `jest.config.js`, `setup-jest.ts`.

---

## Notes techniques utiles (pour la soutenance)
- **Spring Boot 4 + Jackson 3** : le namespace JSON a changé (`tools.jackson` au lieu de
  `com.fasterxml.jackson`). À savoir si on cherche de l'aide en ligne (la plupart des exemples sont en
  Spring Boot 3 / Jackson 2).
- **Mode mock du front** : tant que le front n'est pas branché au back, il utilise `assets/products.json`
  en repli. Les mêmes 30 produits servent de données de départ au back (fichier copié dans
  `back/src/main/resources/products.json`).
