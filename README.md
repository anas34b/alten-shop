# ALTEN SHOP — Application e-commerce full-stack

Test technique ALTEN réalisé en **full-stack** : un front Angular et un back Spring Boot,
avec authentification JWT, panier, liste d'envies, tests automatisés, Docker et déploiement en ligne.

## 🌐 Démo en ligne

| | Lien |
|---|---|
| **Application (front)** | https://alten-shop.onrender.com |
| **API (back)** | https://alten-shop-back-33jv.onrender.com |
| **Documentation API (Swagger)** | https://alten-shop-back-33jv.onrender.com/swagger-ui/index.html |

**Compte de démonstration (admin)** : `admin@admin.com` / `admin123`
*(On peut aussi créer un compte ; un compte non-admin peut consulter mais pas gérer les produits.)*

> ⚠️ Hébergement gratuit (Render) : le back s'endort après ~15 min d'inactivité.
> Le 1er chargement peut prendre ~50 s (réveil), puis tout est rapide.

## 🧱 Stack technique

| Couche | Technologies |
|--------|--------------|
| **Front-end** | Angular 18, TypeScript, PrimeNG 17, Signals, composants standalone |
| **Back-end** | Java 21, Spring Boot 4, Spring Web, Spring Data JPA, Spring Security |
| **Base de données** | H2 (en mémoire) — bascule possible vers PostgreSQL via JPA |
| **Authentification** | JWT (jjwt), mots de passe hachés en BCrypt |
| **Tests** | JUnit 5 + Mockito + MockMvc (back) · Jest (front) |
| **Documentation API** | springdoc-openapi (Swagger UI) |
| **Conteneurisation** | Docker (image multi-étapes) |
| **Hébergement** | Render (back en Docker + front en site statique) |

## ✨ Fonctionnalités livrées

**Front — Partie 1 (Shop)** : liste produits riche (image, prix, note, badge de stock), ajout/retrait
au panier, badge de quantité dans la barre, page panier. **Partie 2** : page Contact avec formulaire validé
(email + message obligatoires, message ≤ 300, message de succès). **Bonus** : pagination + filtrage
(nom/catégorie), ajustement des quantités. Le panier est **persistant** (survit au rechargement).

**Back — Partie 1** : CRUD complet des produits en base. **Partie 2** : connexion obligatoire par JWT
(`POST /account`, `POST /token`), création/modification/suppression de produits réservées à
`admin@admin.com`, panier et liste d'envies par utilisateur. **Bonus** : Swagger + jeu de tests.

## ▶️ Lancer en local

Pré-requis : **Java 21**, **Node.js** (npm). Docker est optionnel.

**Back-end** (terminal 1) — port **8081** :
```bash
cd back
./mvnw spring-boot:run          # Windows PowerShell : .\mvnw.cmd spring-boot:run
# Alternative Docker :
# docker build -t alten-shop-back . && docker run -p 8081:8081 alten-shop-back
```

**Front-end** (terminal 2) — port **4200** :
```bash
cd front
npm install                     # une seule fois
npm start                       # http://localhost:4200
```

Ouvrir http://localhost:4200 → se connecter avec `admin@admin.com` / `admin123`.
Le front utilise un proxy (`proxy.conf.json`) qui relie `/api`, `/token`, `/account` au back (8081).

## 🧪 Tests

```bash
cd back && ./mvnw test          # back : tests JUnit (CRUD, sécurité, JWT, panier, wishlist)
cd front && npm test            # front : tests Jest (auth, panier, formulaire contact)
```

## 🏛️ Architecture

```
Angular (front, :4200)  ──proxy /api──►  Spring Boot (back, :8081)  ──JPA──►  H2
   standalone + signals                  Controller → Service → Repository
   interceptor JWT (Bearer)              Spring Security (filtre JWT, BCrypt)
```

- **Front** : découpage par fonctionnalité (`data-access` / `features` / `ui`), état réactif via Signals,
  interceptor HTTP qui ajoute le token, guard de route qui impose la connexion.
- **Back** : architecture en couches, sécurité stateless par JWT, contrôle admin sur les mutations produits,
  données de démonstration (30 produits + compte admin) chargées au démarrage.

## 📁 Structure du projet

```
alten-shop/
├── back/                 # API Spring Boot (Java)
│   ├── src/main/java/com/alten/shop/
│   │   ├── product/      # entité, repository, service*, controller produits
│   │   ├── user/         # entité utilisateur + service utilisateur courant
│   │   ├── auth/         # JWT, login/inscription, filtre de sécurité
│   │   ├── cart/         # panier (entité, repo, service, controller)
│   │   ├── wishlist/     # liste d'envies
│   │   └── config/       # sécurité, CORS, OpenAPI, données de démarrage
│   └── Dockerfile
├── front/                # Application Angular
│   └── src/app/          # products, cart, auth, shared (home, contact, menu)
└── docs/                 # Documentation (analyse, choix techniques, présentation, avancement)
```

## 📚 Documentation

Le dossier [`docs/`](docs/) contient :
- **01 — État des lieux** : analyse du code de départ fourni.
- **02 — Attendus & choix techniques** : exigences du sujet et justifications des choix.
- **03 — Présentation** : support pour la soutenance.
- **AVANCEMENT.md** : journal de bord détaillé + toutes les commandes pour lancer/tester.

---

## 📋 Sujet du test (énoncé original)

<details>
<summary>Déplier l'énoncé ALTEN</summary>

### Front-end

**Partie 1 : Shop**
- Afficher toutes les informations pertinentes d'un produit sur la liste
- Permettre d'ajouter un produit au panier depuis la liste
- Permettre de supprimer un produit du panier
- Afficher un badge indiquant la quantité de produits dans le panier
- Permettre de visualiser la liste des produits qui composent le panier

**Partie 2**
- Créer un point de menu "Contact" + une page avec formulaire (email + message)
- Email et message obligatoires, message < 300 caractères
- À l'envoi : afficher "Demande de contact envoyée avec succès"

**Bonus** : pagination/filtrage de la liste ; visualiser et ajuster la quantité.

### Back-end

**Partie 1** : back-end de gestion de produits (technologie au choix : Node, **Java/Spring Boot**,
C#/.NET, PHP/Symfony — API Platform interdite). Stockage en base SQL/NoSQL ou JSON.

**Partie 2**
- Connexion obligatoire via JWT ; routes `POST /account` et `POST /token`
- Seul `admin@admin.com` peut ajouter/modifier/supprimer des produits (solution simple, sans gestion de rôles)
- Gérer un panier d'achat par utilisateur
- Gérer une liste d'envies par utilisateur

**Bonus** : tests Postman ou Swagger pour valider l'API.

</details>
