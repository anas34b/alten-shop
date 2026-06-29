// Initialise l'environnement de test Angular pour Jest (avec Zone.js).
// Equivalent moderne de l'ancien "test.ts" de Karma.
import { setupZoneTestEnv } from "jest-preset-angular/setup-env/zone";

setupZoneTestEnv();

// --- Filtre un bruit connu et sans impact ---
// jsdom (le faux navigateur de Jest) n'arrive pas a parser certaines regles CSS
// modernes de PrimeNG et logue "Could not parse CSS stylesheet". On masque ce
// message precis pour garder une sortie de test lisible (les tests ne sont pas affectes).
const originalConsoleError = console.error;
console.error = (...args: unknown[]) => {
  const first = args[0] as { message?: string } | undefined;
  if (first && typeof first.message === "string"
      && first.message.includes("Could not parse CSS stylesheet")) {
    return;
  }
  originalConsoleError(...args);
};
