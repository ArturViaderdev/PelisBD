import { useEffect } from 'react';

const APP_NAME = 'PelisBD';

// Pone el título de la pestaña del navegador: "<título> | PelisBD".
// Si el título aún no está disponible (cargando), deja "PelisBD".
export function useDocumentTitle(title) {
  useEffect(() => {
    document.title = title ? `${title} | ${APP_NAME}` : APP_NAME;
  }, [title]);

  // Al salir de la página se restaura el título por defecto.
  useEffect(() => () => {
    document.title = APP_NAME;
  }, []);
}
