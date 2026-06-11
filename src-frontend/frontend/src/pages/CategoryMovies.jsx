import { useEffect, useState } from 'react';
import { moviesService } from '../services/api';
import MovieCard from '../components/MovieCard';
import { FiChevronLeft, FiChevronRight } from 'react-icons/fi';
import { useParams } from 'react-router-dom';

export default function CategoryMovies() {
  const { id } = useParams(); // 📌 Obtiene el ID de la categoría de la URL
  const [movies, setMovies] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [genreName, setCategoryName] = useState(1);

  useEffect(() => {
    const fetchMoviesByCategory = async () => {
      try {
        setIsLoading(true);
        const response = await moviesService.getMoviesByCategory(id, currentPage);
        setMovies(response.data.results || []);
        setTotalPages(response.data.total_pages || 1);
        setCategoryName(response.data.name);
      } catch (error) {
        console.error('Error fetching movies by category:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchMoviesByCategory();
  }, [id, currentPage]);

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 space-y-8">
     <h1 className="text-3xl font-bold">
      Películas en categoría: {genreName}
    </h1>
      {isLoading ? (
        <div className="text-center py-12">
          <p className="text-gray-400">Cargando películas...</p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {movies.map((movie) => (
              <MovieCard key={movie.id} item={movie} type="movie" />
            ))}
          </div>

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

