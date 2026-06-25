import { useEffect, useState } from 'react';
import { moviesService } from '../services/api';
import MovieCard from '../components/MovieCard';
import { FiChevronLeft, FiChevronRight } from 'react-icons/fi';

export default function TrendingMovies() {
  const [movies, setMovies] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  useEffect(() => {
    const fetchTrendingMovies = async () => {
      try {
        setIsLoading(true);
        const response = await moviesService.getTrending(currentPage,"week");
        setMovies(response.data.results || []);
        setTotalPages(response.data.total_pages || 1);
      } catch (error) {
        console.error('Error fetching trending movies:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchTrendingMovies();
  }, [currentPage]);

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 space-y-8">
      <h1 className="text-3xl font-bold">Películas en Tendencia esta Semana</h1>

      {isLoading ? (
        <div className="text-center py-12">
          <p className="text-gray-400">Cargando películas en tendencia...</p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {movies.map((movie) => (
              <MovieCard key={movie.id} item={movie} type="movie" />
            ))}
          </div>

          {/* Paginación */}
          <div className="flex items-center justify-between">
            <button
              onClick={() => setCurrentPage(p => Math.max(1, p - 1))}
              disabled={currentPage === 1}
              className="flex items-center gap-2 px-4 py-2 bg-gray-800 rounded-lg hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <FiChevronLeft /> Anterior
            </button>

            <div className="flex items-center gap-2">
              <span className="text-gray-400">
                Página {currentPage} de {totalPages}
              </span>
            </div>

            <button
              onClick={() => setCurrentPage(p => Math.min(totalPages, p + 1))}
              disabled={currentPage === totalPages}
              className="flex items-center gap-2 px-4 py-2 bg-gray-800 rounded-lg hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Siguiente <FiChevronRight />
            </button>
          </div>
        </>
      )}
    </div>
  );
}
