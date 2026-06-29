package com.alten.shop.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mini-controleur de test (etape 1).
 *
 * Son seul but : verifier que le serveur demarre et repond bien a une requete HTTP.
 * On le supprimera plus tard, il ne sert qu'a valider que tout est branche.
 */
@RestController // Indique a Spring que cette classe gere des requetes HTTP et renvoie directement des donnees (pas une page HTML)
public class PingController {

    /**
     * Repond a une requete : GET http://localhost:8080/ping
     * @return la chaine "pong" (preuve que l'API fonctionne)
     */
    @GetMapping("/ping") // Associe la methode ci-dessous a l'URL "/ping" en HTTP GET
    public String ping() {
        return "pong";
    }
}
