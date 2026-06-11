import { useEffect, useState } from 'react';
import { moviesService, tvService } from '../services/api';
import MovieCard from '../components/MovieCard';
import CategoryList from '../components/CategoryList';
import { FiChevronRight } from 'react-icons/fi';

export default function Home() {
  const [popularMovies, setPopularMovies] = useState([]);
  const [trendingMovies, setTrendingMovies] = useState([]);
  const [popularTV, setPopularTV] = useState([]);
  const [trendingTV, setTrendingTV] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  
  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true);
        const [moviesRes, trendingRes, tvRes,trendingTV] = await Promise.all([
          moviesService.getPopular(1),
          moviesService.getTrending(1,'week'),
          tvService.getPopular(1),
          tvService.getTrending(1,'week'),
        ]);

        setPopularMovies(moviesRes.data.results?.slice(0, 6) || []);
        setTrendingMovies(trendingRes.data.results?.slice(0, 6) || []);
        setPopularTV(tvRes.data.results?.slice(0, 6) || []);
        setTrendingTV(trendingTV.data.results?.slice(0, 6) || []);
      } catch (error) {
        console.error('Error fetching home data:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, []);
  
  

  return (
    <div className="min-h-screen space-y-12 py-8">
      <div className="max-w-7xl mx-auto px-4">
        <div className="text-center mb-12">
          <h1 className="text-4xl md:text-5xl font-bold mb-4">
            Bienvenido a <span className="text-primary">PelisBD</span>
          </h1>
          <p className="text-gray-400 text-lg">
            Descubre, califica y comenta tus películas y series favoritas
          </p>
        </div>

         {/* Categorías */}
      <CategoryList 
      
      />
    
      </div>

      {/* Películas Populares */}
      {!isLoading && popularMovies.length > 0 && (
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold">Películas Populares</h2>
            <a href="/movies" className="flex items-center gap-1 text-primary hover:text-indigo-400">
              Ver más <FiChevronRight />
            </a>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {popularMovies.map((movie) => (
              <MovieCard key={movie.id} item={movie} type="movie" />
            ))}
          </div>
        </div>
      )}

      {/* Películas en Tendencia */}
      {!isLoading && trendingMovies.length > 0 && (
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold">En Tendencia esta Semana</h2>
            <a href="/movies/trending" className="flex items-center gap-1 text-primary hover:text-indigo-400">
              Ver más <FiChevronRight />
            </a>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {trendingMovies.map((movie) => (
              <MovieCard key={movie.id} item={movie} type="movie" />
            ))}
          </div>
        </div>
      )}

      {/* Series Populares */}
      {!isLoading && popularTV.length > 0 && (
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold">Series Populares</h2>
            <a href="/tv" className="flex items-center gap-1 text-primary hover:text-indigo-400">
              Ver más <FiChevronRight />
            </a>
          </div>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {popularTV.map((show) => (
              <MovieCard key={show.id} item={show} type="tv" />
            ))}
          </div>
        </div>
      )}

      {/* Series en Tendencia */}
    {!isLoading && trendingTV.length > 0 && (
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold">Series en Tendencia esta Semana</h2>
          <a href="/tv/trending" className="flex items-center gap-1 text-primary hover:text-indigo-400">
            Ver más <FiChevronRight />
          </a>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
          {trendingTV.map((show) => (
            <MovieCard key={show.id} item={show} type="tv" />
          ))}
        </div>
      </div>
)}
      
      {isLoading && (
        <div className="text-center py-12">
          <p className="text-gray-400">Cargando contenido...</p>
        </div>
      )}
    </div>
  );
}

