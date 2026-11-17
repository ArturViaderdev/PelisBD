import { useEffect, useState } from 'react';
import { movies3dService } from '../services/api';
import MovieCard from '../components/MovieCard';
import { useListParams } from '../utils/useListParams';
import { useAuthStore } from '../stores/authStore';
import { useDocumentTitle } from '../utils/useDocumentTitle';

// Listado público de películas aprobadas como 3D.
export default function Movies3D() {
  const { page, setPage } = useListParams();
  // Al iniciar o cerrar sesión se vuelve a pedir el listado para traer (o quitar) los flags
  const token = useAuthStore((state) => state.token);
  const [items, setItems] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  useDocumentTitle('Películas 3D');

  useEffect(() => {
    let cancelled = false;

    const fetchMovies = async () => {
      try {
        setIsLoading(true);
        const res = await movies3dService.getApproved(page, 12);
        const data = res.data;
        if (cancelled) return;
        // Se adapta al formato que espera MovieCard (como los de TMDB)
        setItems(
          (data.content || []).map((m) => ({
            id: m.tmdbId,
            title: m.title,
            poster_path: m.posterPath,
            release_date: m.releaseDate,
            watched: Boolean(m.watched),
            watchListed: Boolean(m.watchListed),
          }))
        );
        setTotalPages(data.totalPages || 1);
        setTotalElements(data.totalElements || 0);
      } catch (err) {
        if (cancelled) return;
        console.error('Error loading 3D movies:', err);
        setItems([]);
      } finally {
        if (!cancelled) setIsLoading(false);
      }
    };

    fetchMovies();
    return () => {
      cancelled = true;
    };
  }, [page, token]);

  const handlePageChange = (newPage) => {
    if (newPage >= 1 && newPage <= totalPages) setPage(newPage);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 space-y-8">
      <div className="space-y-2">
        <h1 className="text-3xl font-bold">Películas 3D</h1>
        <p className="text-gray-400">
          {totalElements} película{totalElements === 1 ? '' : 's'} disponible
          {totalElements === 1 ? '' : 's'} en 3D, propuestas por la comunidad y revisadas por un administrador.
        </p>
      </div>

      {isLoading ? (
        <div className="text-center py-12">
          <p className="text-gray-400">Cargando...</p>
        </div>
      ) : items.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-400 text-lg">Todavía no hay películas 3D</p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {items.map((item) => (
              <MovieCard
                key={item.id}
                item={item}
                type="movie"
                onWatchedToggle={(id, _type, value) =>
                  setItems((prev) => prev.map((it) => (it.id === id ? { ...it, watched: value } : it)))
                }
                onWatchlistToggle={(id, _type, value) =>
                  setItems((prev) => prev.map((it) => (it.id === id ? { ...it, watchListed: value } : it)))
                }
              />
            ))}
          </div>

          {totalPages > 1 && (
            <div className="flex justify-center items-center gap-2 mt-8">
              <button
                onClick={() => handlePageChange(page - 1)}
                disabled={page === 1}
                className="px-4 py-2 bg-gray-700 text-white rounded disabled:opacity-50"
              >
                Anterior
              </button>
              <span className="text-gray-400">
                Página {page} de {totalPages}
              </span>
              <button
                onClick={() => handlePageChange(page + 1)}
                disabled={page >= totalPages}
                className="px-4 py-2 bg-gray-700 text-white rounded disabled:opacity-50"
              >
                Siguiente
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
