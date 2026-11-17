import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { movies3dService } from '../services/api';
import { useMovies3dStore } from '../stores/movies3dStore';
import { useDocumentTitle } from '../utils/useDocumentTitle';

// Cola de propuestas 3D pendientes, agrupadas por película.
// Las más pedidas aparecen primero.
export default function AdminMovies3DPage() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [busyId, setBusyId] = useState(null);
  const add3d = useMovies3dStore((s) => s.add);

  useDocumentTitle('Admin · Propuestas 3D');

  const load = async () => {
    try {
      setLoading(true);
      const res = await movies3dService.getPendingRequests();
      setRequests(Array.isArray(res.data) ? res.data : []);
    } catch (err) {
      console.error('Error loading 3D requests:', err);
      setRequests([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const handle = async (tmdbId, approve) => {
    setBusyId(tmdbId);
    try {
      if (approve) {
        await movies3dService.approve(tmdbId);
        add3d(tmdbId);
      } else {
        await movies3dService.reject(tmdbId);
      }
      setRequests((prev) => prev.filter((r) => r.tmdbId !== tmdbId));
    } catch (err) {
      console.error('Error reviewing 3D request:', err);
    } finally {
      setBusyId(null);
    }
  };

  return (
    <div className="max-w-5xl mx-auto px-4 py-8 space-y-6">
      <div>
        <h1 className="text-3xl font-bold">Propuestas de películas 3D</h1>
        <p className="text-gray-400 mt-1">
          Al aprobar, la película se muestra como 3D para todos los usuarios.
        </p>
      </div>

      {loading ? (
        <p className="text-gray-400">Cargando...</p>
      ) : requests.length === 0 ? (
        <p className="text-gray-400">No hay propuestas pendientes.</p>
      ) : (
        <div className="space-y-3">
          {requests.map((r) => {
            const year = r.releaseDate ? new Date(r.releaseDate).getFullYear() : null;
            return (
              <div
                key={r.tmdbId}
                className="flex items-center gap-4 rounded-lg bg-gray-800 p-3"
              >
                <Link to={`/movie/${r.tmdbId}`} className="shrink-0">
                  {r.posterPath ? (
                    <img
                      src={`https://image.tmdb.org/t/p/w92${r.posterPath}`}
                      alt={r.title}
                      className="w-14 aspect-[2/3] object-cover rounded"
                    />
                  ) : (
                    <div className="w-14 aspect-[2/3] rounded bg-gray-700" />
                  )}
                </Link>

                <div className="flex-1 min-w-0">
                  <Link
                    to={`/movie/${r.tmdbId}`}
                    className="font-semibold text-white hover:text-primary"
                  >
                    {r.title}
                    {year && <span className="ml-2 text-gray-400 font-normal">({year})</span>}
                  </Link>
                  <p className="text-sm text-gray-400">
                    Propuesta por {r.requestCount} usuario{r.requestCount === 1 ? '' : 's'}
                    {r.firstRequestedAt &&
                      ` · desde ${new Date(r.firstRequestedAt).toLocaleDateString('es-ES')}`}
                  </p>
                </div>

                <div className="flex gap-2">
                  <button
                    disabled={busyId === r.tmdbId}
                    onClick={() => handle(r.tmdbId, true)}
                    className="rounded bg-cyan-600 px-3 py-2 text-sm text-white hover:bg-cyan-500 disabled:opacity-50"
                  >
                    Aprobar 3D
                  </button>
                  <button
                    disabled={busyId === r.tmdbId}
                    onClick={() => handle(r.tmdbId, false)}
                    className="rounded bg-gray-700 px-3 py-2 text-sm text-white hover:bg-gray-600 disabled:opacity-50"
                  >
                    Rechazar
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
