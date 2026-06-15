// src/pages/SearchResults.jsx
import { useEffect, useMemo, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { moviesService } from '../services/api';
import MovieCard from '../components/MovieCard';
import FilterBar from '../components/FilterBar';
import Pagination from '../components/Pagination';

export default function SearchResults() {
  const [searchParams, setSearchParams] = useSearchParams();
  const query = (searchParams.get('q') || '').trim();
  const page = parseInt(searchParams.get('page')) || 1;

  const [results, setResults] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [totalPages, setTotalPages] = useState(1);

  const navigate = useNavigate();

  // ✅ DEBES DECLARAR filterType y sortBy AQUÍ, antes de usarlos
  const [filterType, setFilterType] = useState('all'); // ✅ Inicializado aquí
  const [sortBy, setSortBy] = useState('popularity'); // ✅ Inicializado aquí

  // ✅ Llamada al servicio de búsqueda
  useEffect(() => {
    if (!query) {
      setResults([]);
      setTotalPages(1);
      return;
    }

    const searchMovies = async () => {
      try {
        setIsLoading(true);
        setError(null);

        const response = await moviesService.searchMovies(query, page);
        const data = response.data;

        const movies = (data.results || []).map((item) => ({
          ...item,
          type: 'movie',
        }));

        setResults(movies);
        setTotalPages(data.total_pages || 1);
      } catch (err) {
        console.error('Error searching movies:', err);
        setError('No se pudieron cargar los resultados');
        setResults([]);
        setTotalPages(1);
      } finally {
        setIsLoading(false);
      }
    };

    searchMovies();
  }, [query, page]);

  // ✅ Ahora sí podemos usar filterType y sortBy
  const filteredResults = useMemo(() => {
    let data = [...results];

    if (filterType !== 'all') {
      data = data.filter((item) => item.type === filterType);
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
  }, [results, filterType, sortBy]);

  // ✅ Manejar cambio de página
  const handlePageChange = (newPage) => {
    setSearchParams({ q: query, page: newPage });
  };

  // ✅ Manejar cambios en filtros
  const handleFilterChange = (type) => {
    setFilterType(type);
    setSearchParams({ q: query, page: 1 });
  };

  const handleSortChange = (sort) => {
    setSortBy(sort);
    setSearchParams({ q: query, page: 1 });
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
          <p className="text-gray-400">Buscando películas...</p>
        </div>
      ) : !query ? (
        <div className="text-center py-12">
          <p className="text-gray-400 text-lg">
            Escribe un término de búsqueda.
          </p>
        </div>
      ) : error ? (
        <div className="text-center py-12">
          <p className="text-red-400 text-lg">{error}</p>
        </div>
      ) : filteredResults.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-400 text-lg">
            No se encontraron películas para "{query}"
          </p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {filteredResults.map((item) => (
              <MovieCard
                key={`${item.type}-${item.id}`}
                item={item}
                type={item.type}
              />
            ))}
          </div>

          {/* Paginación */}
          {totalPages > 1 && (
            <div className="flex justify-center mt-8">
              <Pagination
                currentPage={page}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            </div>
          )}
        </>
      )}
    </div>
  );
}
