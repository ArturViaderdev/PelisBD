import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { moviesService } from '../services/api';
import MovieCard from '../components/MovieCard';
import FilterBar from '../components/FilterBar';

export default function SearchResults() {
  const [searchParams] = useSearchParams();
  const query = (searchParams.get('q') || '').trim();

  const [results, setResults] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [filterType, setFilterType] = useState('all');
  const [sortBy, setSortBy] = useState('popularity');

  useEffect(() => {
    const searchMovies = async () => {
      if (!query) {
        setResults([]);
        return;
      }

      try {
        setIsLoading(true);

        const response = await moviesService.searchMovies(query);
        const movies = (response.data?.results || []).map((item) => ({
          ...item,
          type: 'movie',
        }));

        setResults(movies);
      } catch (error) {
        console.error('Error searching movies:', error);
        setResults([]);
      } finally {
        setIsLoading(false);
      }
    };

    searchMovies();
  }, [query]);

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
    }

    return data;
  }, [results, filterType, sortBy]);

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 space-y-8">
      <h1 className="text-3xl font-bold">
        Resultados para: <span className="text-primary">"{query}"</span>
      </h1>

      <FilterBar
        filterType={filterType}
        sortBy={sortBy}
        onFilterChange={setFilterType}
        onSortChange={setSortBy}
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
      ) : filteredResults.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-400 text-lg">
            No se encontraron resultados para "{query}"
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
          {filteredResults.map((item) => (
            <MovieCard
              key={`${item.type}-${item.id}`}
              item={item}
              type={item.type}
            />
          ))}
        </div>
      )}
    </div>
  );
}
