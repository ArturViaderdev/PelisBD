// Utilidades para la sesión (token JWT guardado en localStorage).

// Devuelve la fecha de caducidad del token en milisegundos, o null si no se puede leer.
export const getTokenExpiration = (token) => {
  try {
    const payload = token.split('.')[1];
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    const json = JSON.parse(atob(base64));
    return typeof json.exp === 'number' ? json.exp * 1000 : null;
  } catch {
    return null;
  }
};

export const isTokenExpired = (token) => {
  if (!token) return false;
  const exp = getTokenExpiration(token);
  return exp !== null && Date.now() >= exp;
};

// Hay un token guardado y ha caducado → la sesión ha expirado.
export const isStoredSessionExpired = () =>
  isTokenExpired(localStorage.getItem('token'));

let redirecting = false;

// Cierra la sesión y lleva al login, recordando la página en la que estaba el usuario.
// Solo actúa si había una sesión iniciada: a los usuarios no logueados no les afecta.
export const expireSession = () => {
  if (!localStorage.getItem('token')) return;

  localStorage.removeItem('token');
  localStorage.removeItem('user');

  if (redirecting) return;
  redirecting = true;

  const { pathname, search } = window.location;
  if (pathname === '/login') {
    window.location.replace('/login?expired=1');
    return;
  }

  const from = encodeURIComponent(pathname + search);
  window.location.replace(`/login?expired=1&redirect=${from}`);
};
