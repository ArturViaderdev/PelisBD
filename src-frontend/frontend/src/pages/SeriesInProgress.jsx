import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FiPlayCircle } from 'react-icons/fi';
import { userService } from '../services/api';
import { useAuthStore } from '../stores/authStore';
import { useListParams } from '../utils/useListParams';
import { useDocumentTitle } from '../utils/useDocumentTitle';

const PAGE_SIZE = 12;

// Series con al menos un capítulo visto y el capítulo más avanzado de cada una.
export default function SeriesInProgress() {
  const { isAuthenticated } = useAuthStore();
  const navigate = useNavigate();
  const [series, setSeries] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(false);
  // Página en la URL (?page=2) para no perderla al volver del detalle
  const { page, setPage } = useListParams();

  useDocumentTitle('Series en curso');

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    const fetchSeries = async () => {
      try {
        setIsLoading(true);
        setError(false);
        const response = await userService.getSeriesInProgress();
        setSeries(Array.isArray(response.data) ? response.data : []);
      } catch (err) {
        console.error('Error fetching series in progress:', err);
        setError(true);
        setSeries([]);
      } finally {
        setIsLoading(false);
      }
    };

    fetchSeries();
  }, [isAuthenticated, navigate]);

  const totalPages = Math.max(1, Math.ceil(series.length / PAGE_SIZE));
  const currentPage = Math.min(page, totalPages);
  const pageItems = series.slice(
    (currentPage - 1) * PAGE_SIZE,
    currentPage * PAGE_SIZE
  );

  const handlePageChange = (newPage) => {
    if (newPage >= 1 && newPage <= totalPages) {
      setPage(newPage);
    }
  };

  const formatDate = (value) =>
    value
      ? new Date(value).toLocaleDateString('es-ES', {
          year: 'numeric',
          month: 'short',
          day: 'numeric',
        })
      : null;

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 space-y-8">
      <div>
        <h1 className="text-3xl font-bold">Series en curso</h1>
        <p className="text-gray-400 mt-1">
          Series de las que has visto algún capítulo y hasta dónde has llegado.
        </p>
      </div>

      {isLoading ? (
        <div className="text-center py-12">
          <p className="text-gray-400">Cargando...</p>
        </div>
      ) : error ? (
        <div className="text-center py-12">
          <p className="text-red-400">No se han podido cargar tus series.</p>
        </div>
      ) : series.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-400 text-lg">
            Aún no has marcado ningún capítulo como visto
          </p>
        </div>
      ) : (
        <>
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-4">
          {pageItems.map((serie) => {
            const name = serie.name || `Serie #${serie.tvId}`;
            const lastDate = formatDate(serie.lastWatchedAt);

            return (
              <Link
                key={serie.tvId}
                to={`/tv/${serie.tvId}`}
                className="group block overflow-hidden rounded-lg bg-gray-800 shadow-md transition-all duration-300 hover:scale-105 hover:shadow-xl"
                aria-label={`Ver detalles de ${name}`}
              >
                <div className="relative aspect-[2/3] w-full overflow-hidden">
                  {serie.posterPath ? (
                    <img
                      src={`https://image.tmdb.org/t/p/w300${serie.posterPath}`}
                      alt={name}
                      className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-110"
                    />
                  ) : (
                    <div className="flex h-full w-full items-center justify-center bg-gradient-to-br from-gray-700 to-gray-900">
                      <span className="text-sm text-gray-400">Sin imagen</span>
                    </div>
                  )}

                  <span className="absolute bottom-2 left-2 flex items-center gap-1 rounded bg-black/75 px-2 py-1 text-xs font-semibold text-white">
                    <FiPlayCircle size={14} className="text-primary" />
                    T{serie.lastSeason} · E{serie.lastEpisode}
                  </span>
                </div>

                <div className="space-y-1 p-3">
                  <h3 className="line-clamp-1 text-sm font-semibold text-white transition-colors group-hover:text-primary">
                    {name}
                  </h3>
                  <p className="text-xs text-gray-300">
                    Último: temporada {serie.lastSeason}, capítulo {serie.lastEpisode}
                  </p>
                  <p className="text-xs text-gray-400">
                    {serie.watchedEpisodes}{' '}
                    {serie.watchedEpisodes === 1 ? 'capítulo visto' : 'capítulos vistos'}
                    {lastDate && <> · {lastDate}</>}
                  </p>
                </div>
              </Link>
            );
          })}
        </div>

        {/* Paginación */}
        {totalPages > 1 && (
          <div className="flex justify-center items-center gap-2 mt-8">
            <button
              onClick={() => handlePageChange(currentPage - 1)}
              disabled={currentPage === 1}
              className="px-4 py-2 bg-gray-700 text-white rounded disabled:opacity-50"
            >
              Anterior
            </button>

            <span className="text-gray-400">
              Página {currentPage} de {totalPages}
            </span>

            <button
              onClick={() => handlePageChange(currentPage + 1)}
              disabled={currentPage >= totalPages}
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
