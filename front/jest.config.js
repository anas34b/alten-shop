/**
 * Configuration de Jest pour le front Angular.
 * On s'appuie sur "jest-preset-angular" qui sait compiler les composants Angular.
 */
module.exports = {
  // Preset qui configure la compilation TypeScript + Angular pour Jest.
  preset: "jest-preset-angular",

  // Fichier execute avant chaque suite de tests (initialise l'environnement de test Angular).
  setupFilesAfterEnv: ["<rootDir>/setup-jest.ts"],

  // On n'analyse pas ces dossiers.
  testPathIgnorePatterns: ["<rootDir>/node_modules/", "<rootDir>/dist/"],

  // Compilation TypeScript des tests via le tsconfig dedie.
  transform: {
    "^.+\\.(ts|mjs|js|html)$": [
      "jest-preset-angular",
      {
        tsconfig: "<rootDir>/src/tsconfig.spec.json",
        stringifyContentPathRegex: "\\.(html|svg)$",
      },
    ],
  },

  // Le projet importe les fichiers via "app/..." et "environments/..." (baseUrl=src) :
  // on indique a Jest ou les trouver.
  moduleNameMapper: {
    "^app/(.*)$": "<rootDir>/src/app/$1",
    "^environments/(.*)$": "<rootDir>/src/environments/$1",
  },
};
