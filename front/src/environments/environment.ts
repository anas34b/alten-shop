export const environment = {
  production: false,
  // En dev, apiUrl est vide : les appels "/api/...", "/token", "/account" sont
  // redirige vers le back par le proxy (proxy.conf.json).
  apiUrl: "",
};
