import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { movies3dService } from '../services/api';
import { useAuthStore } from '../stores/authStore';
import { useMovies3dStore } from '../stores/movies3dStore';

// Bloque del detalle de película para el 3D:
//  - Usuario: proponer la película como 3D / cancelar la propuesta.
//  - Administrador: marcar directamente como 3D / quitar el 3D.
//  - Sin sesión: solo se ve la etiqueta si ya es 3D (eso lo pinta el título).
export default function Movie3DSection({ tmdbId }) {
  const { user } = useAuthStore();
  const add3d = useMovies3dStore((s) => s.add);
  const remove3d = useMovies3dStore((s) => s.remove);
  const is3dGlobal = useMovies3dStore((s) => s.ids.has(Number(tmdbId)));
  const location = useLocation();

  const [status, setStatus] = useState(null); // { is3d, myRequestStatus }
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState('');

  const isAdmin = user?.role === 'ADMIN';

  useEffect(() => {
    if (!user) return;
    let mounted = true;
    movies3dService
      .getStatus(tmdbId)
      .then((res) => mounted && setStatus(res.data))
      .catch((err) => {
        // Si no se puede consultar el estado, se muestra igualmente el botón;
        // si la acción falla, se verá el mensaje de error.
        console.error('Error loading 3D status:', err);
        if (mounted) setStatus({ is3d: false, myRequestStatus: null });
      });
    return () => {
      mounted = false;
    };
  }, [tmdbId, user]);

  // Sin sesión: botón que lleva al login y vuelve a esta película
  if (!user) {
    if (is3dGlobal) return null;
    const redirect = encodeURIComponent(location.pathname + location.search);
    return (
      <Link
        to={`/login?redirect=${redirect}`}
        className="inline-block rounded-lg border border-cyan-500 px-4 py-2 text-cyan-300 hover:bg-cyan-500 hover:text-black"
      >
        Proponer como 3D (inicia sesión)
      </Link>
    );
  }

  if (!status) {
    return <p className="text-sm text-gray-500">Comprobando 3D…</p>;
  }

  const run = async (action, okMessage) => {
    setBusy(true);
    setMessage('');
    try {
      await action();
      setMessage(okMessage);
    } catch (err) {
      console.error('3D action error:', err);
      setMessage('No se ha podido completar la acción');
    } finally {
      setBusy(false);
    }
  };

  // ---------- Administrador ----------
  if (isAdmin) {
    return (
      <div className="flex flex-wrap items-center gap-3">
        {status.is3d ? (
          <button
            type="button"
            disabled={busy}
            onClick={() =>
              run(async () => {
                await movies3dService.remove(tmdbId);
                remove3d(tmdbId);
                setStatus((s) => ({ ...s, is3d: false }));
              }, 'Se ha quitado la marca 3D')
            }
            className="rounded-lg bg-gray-700 px-4 py-2 text-white hover:bg-gray-600 disabled:opacity-50"
          >
            Quitar 3D
          </button>
        ) : (
          <button
            type="button"
            disabled={busy}
            onClick={() =>
              run(async () => {
                await movies3dService.approve(tmdbId);
                add3d(tmdbId);
                setStatus((s) => ({ ...s, is3d: true }));
              }, 'Película marcada como 3D para todos')
            }
            className="rounded-lg bg-cyan-600 px-4 py-2 text-white hover:bg-cyan-500 disabled:opacity-50"
          >
            Marcar como 3D
          </button>
        )}
        {message && <span className="text-sm text-gray-400">{message}</span>}
      </div>
    );
  }

  // ---------- Usuario ----------
  if (status.is3d) return null; // ya es 3D: no hay nada que proponer

  const requestStatus = status.myRequestStatus;

  return (
    <div className="flex flex-wrap items-center gap-3">
      {requestStatus === 'PENDING' ? (
        <>
          <span className="rounded-lg bg-yellow-900/60 px-3 py-2 text-sm text-yellow-200">
            Propuesta como 3D · pendiente de aprobación
          </span>
          <button
            type="button"
            disabled={busy}
            onClick={() =>
              run(async () => {
                const res = await movies3dService.cancelRequest(tmdbId);
                setStatus(res.data);
              }, 'Propuesta cancelada')
            }
            className="text-sm text-gray-400 underline hover:text-white disabled:opacity-50"
          >
            Cancelar
          </button>
        </>
      ) : (
        <>
          <button
            type="button"
            disabled={busy}
            onClick={() =>
              run(async () => {
                const res = await movies3dService.request(tmdbId);
                setStatus(res.data);
              }, 'Gracias. Un administrador revisará la propuesta.')
            }
            className="rounded-lg border border-cyan-500 px-4 py-2 text-cyan-300 hover:bg-cyan-500 hover:text-black disabled:opacity-50"
          >
            {requestStatus === 'REJECTED' ? 'Volver a proponer como 3D' : 'Proponer como 3D'}
          </button>
          {requestStatus === 'REJECTED' && !message && (
            <span className="text-sm text-gray-400">Tu propuesta anterior fue rechazada</span>
          )}
        </>
      )}
      {message && <span className="text-sm text-gray-400">{message}</span>}
    </div>
  );
}
