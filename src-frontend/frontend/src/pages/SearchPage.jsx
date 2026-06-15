// src/pages/SearchPage.jsx
import { useEffect, useState, useMemo } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { searchService } from '../services/api';
import MovieCard from '../components/MovieCard';
import FilterBar from '../components/FilterBar';

export default function SearchPage() {
  const [searchParams] = useSearchParams();
  const query = (searchParams.get('q') || '').trim();
  const page = parseInt(searchParams.get('page')) || 1;

  const [results, setResults] = useState({ movies: [], series: [] });
  const [isLoading, setIsLoading] = useState(false);
  const [filterType, setFilterType] = useState('all');
  const [sortBy, setSortBy] = useState('popularity');

  const navigate = useNavigate();

  // ✅ Llamada al servicio de búsqueda
  useEffect(() => {
    const searchAll = async () => {
      if (!query) {
        setResults({ movies: [], series: [] });
        return;
      }

      try {
        setIsLoading(true);
        const response = await searchService.search(query, page);

        // ✅ TMDB devuelve `movies` y `series` separados
        const movies = response.data.movies || [];
        const series = response.data.series || [];

        setResults({ movies, series });
      } catch (err) {
        console.error('Error searching:', err);
        setResults({ movies: [], series: [] });
      } finally {
        setIsLoading(false);
      }
    };

    searchAll();
  }, [query, page]);

  // ✅ Filtrar películas
  const filteredMovies = useMemo(() => {
    let data = [...results.movies];

    if (filterType !== 'all') {
      data = data.filter((item) => item.media_type === filterType);
    }

    if (sortBy === 'title') {
      data.sort((a, b) => (a.title || '').localeCompare(b.title || ''));
    } else if (sortBy === 'date') {
      data.sort(
        (a, b) =>
          new Date(b.release_date || 0).getTime() -
          new Date(a.release_date || 0).getTime()
      );
    } else if (sortBy === 'popularity') {
      data.sort((a, b) => (b.popularity || 0) - (a.popularity || 0));
    }

    return data;
  }, [results.movies, filterType, sortBy]);

  // ✅ Filtrar series
  const filteredSeries = useMemo(() => {
    let data = [...results.series];

    if (filterType !== 'all') {
      data = data.filter((item) => item.media_type === filterType);
    }

    if (sortBy === 'title') {
      data.sort((a, b) => (a.name || '').localeCompare(b.name || ''));
    } else if (sortBy === 'date') {
      data.sort(
        (a, b) =>
          new Date(b.first_air_date || 0).getTime() -
          new Date(a.first_air_date || 0).getTime()
      );
    } else if (sortBy === 'popularity') {
      data.sort((a, b) => (b.popularity || 0) - (a.popularity || 0));
    }

    return data;
  }, [results.series, filterType, sortBy]);

  // ✅ Manejar cambio de filtro
  const handleFilterChange = (type) => {
    setFilterType(type);
    navigate(`?q=${encodeURIComponent(query)}&page=1`);
  };

  const handleSortChange = (sort) => {
    setSortBy(sort);
    navigate(`?q=${encodeURIComponent(query)}&page=1`);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 space-y-8">
      <h1 className="text-3xl font-bold">
        Resultados para: <span className="text-primary">"{query}"</span>
      </h1>

      <FilterBar
        filterType={filterType}
        sortBy={sortBy}
        onFilterChange={handleFilterChange}
        onSortChange={handleSortChange}
      />

      {isLoading ? (
        <div className="text-center py-12">
          <p className="text-gray-400">Buscando películas y series...</p>
        </div>
      ) : !query ? (
        <div className="text-center py-12">
          <p className="text-gray-400 text-lg">
            Escribe un término de búsqueda.
          </p>
        </div>
      ) : (
        <>
          {/* Películas */}
          {filteredMovies.length > 0 && (
            <div>
              <h2 className="text-xl font-bold text-primary mb-4">🎬 Películas</h2>
              <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
                {filteredMovies.map((item) => (
                  <MovieCard
                    key={item.id}
                    item={item}
                    type="movie"
                  />
                ))}
              </div>
            </div>
          )}

          {/* Series */}
          {filteredSeries.length > 0 && (
            <div>
              <h2 className="text-xl font-bold text-primary mb-4">📺 Series</h2>
              <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
                {filteredSeries.map((item) => (
                  <MovieCard
                    key={item.id}
                    item={item}
                    type="tv"
                  />
                ))}
              </div>
            </div>
          )}

          {/* Sin resultados */}
          {filteredMovies.length === 0 && filteredSeries.length === 0 && (
            <div className="text-center py-12">
              <p className="text-gray-400 text-lg">
                No se encontraron resultados para "{query}"
              </p>
            </div>
          )}
        </>
      )}
    </div>
  );
}
