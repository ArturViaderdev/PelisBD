import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { moviesService, tvService } from '../services/api';
import MovieCard from '../components/MovieCard';
import FilterBar from '../components/FilterBar';

export default function SearchResults() {
  const [searchParams] = useSearchParams();
  const query = searchParams.get('q');
  const [results, setResults] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [filterType, setFilterType] = useState('all');
  const [sortBy, setSortBy] = useState('popularity');

  useEffect(() => {
    const searchContent = async () => {
      if (!query) return;

      try {
        setIsLoading(true);
        const [moviesRes, tvRes] = await Promise.all([
          moviesService.searchMovies(query),
          tvService.searchTV(query),
        ]);

        const combined = [
          ...moviesRes.data.results.map(item => ({ ...item, type: 'movie' })),
          ...tvRes.data.results.map(item => ({ ...item, type: 'tv' })),
        ];

        setResults(combined);
      } catch (error) {
        console.error('Error searching:', error);
      } finally {
        setIsLoading(false);
      }
    };

    searchContent();
  }, [query]);

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
          <p className="text-gray-400">Buscando contenido...</p>
        </div>
      ) : results.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-400 text-lg">
            No se encontraron resultados para "{query}"
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
          {results.map((item) => (
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
