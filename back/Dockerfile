# =====================================================================
#  DOCKERFILE — construit une image Docker autonome du back-end.
#  Construction "multi-etapes" : on compile dans une grosse image (JDK),
#  puis on ne garde que le jar dans une petite image (JRE) -> image finale legere.
# =====================================================================

# ---------- ETAPE 1 : BUILD (compilation du jar avec Maven) ----------
# Image contenant Java 21 (JDK) pour compiler.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# 1) On copie d'abord SEULEMENT les fichiers Maven (wrapper + pom).
#    Objectif : telecharger les dependances dans une couche Docker mise en cache,
#    qui ne sera re-executee que si le pom.xml change (et non a chaque modif de code).
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

# 2) Puis on copie le code source et on construit le jar.
#    -DskipTests : on ne relance pas les tests ici (ils tournent separement via "mvn test").
COPY src/ src/
RUN ./mvnw -B -q clean package -DskipTests

# ---------- ETAPE 2 : RUNTIME (execution du jar) ----------
# Image legere contenant uniquement le JRE 21 (pas de quoi compiler).
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# On recupere UNIQUEMENT le jar produit a l'etape de build.
COPY --from=build /app/target/shop-0.0.1-SNAPSHOT.jar app.jar

# Port sur lequel l'application ecoute (informatif ; le mapping reel se fait au "docker run").
EXPOSE 8081

# Commande lancee au demarrage du conteneur.
ENTRYPOINT ["java", "-jar", "app.jar"]
