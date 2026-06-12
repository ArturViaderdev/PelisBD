// src/pages/SearchResultsTV.jsx
import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { tvService } from '../services/api';
import TVShowCard from '../components/TVShowCard';
import FilterBar from '../components/FilterBar';
import Pagination from '../components/Pagination';

export default function SearchResultsTV() {
  const [searchParams] = useSearchParams();
  const query = (searchParams.get('q') || '').trim();
  const page = parseInt(searchParams.get('page')) || 1;

  const [results, setResults] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [totalPages, setTotalPages] = useState(1);
  const [filterType, setFilterType] = useState('all');
  const [sortBy, setSortBy] = useState('popularity');

  useEffect(() => {
    const searchSeries = async () => {
      if (!query) {
        setResults([]);
        return;
      }

      try {
        setIsLoading(true);
        const response = await tvService.searchTV(query, page);
        const data = response.data;
        const movies = (data.results || []).map((item) => ({
          ...item,
          type: 'tv',
        }));

        setResults(movies);
        setTotalPages(data.total_pages || 1);
      } catch (error) {
        console.error('Error searching series:', error);
        setResults([]);
      } finally {
        setIsLoading(false);
      }
    };

    searchSeries();
  }, [query, page]);

  const filteredResults = useMemo(() => {
    let data = [...results];

    if (filterType !== 'all') {
      data = data.filter((item) => item.type === filterType);
    }

    if (sortBy === 'title') {
      data.sort((a, b) => (a.name || '').localeCompare(b.name || ''));
    } else if (sortBy === 'date') {
      data.sort(
        (a, b) =>
          new Date(b.first_air_date || 0).getTime() -
          new Date(a.first_air_date || 0).getTime()
      );
    }

    return data;
  }, [results, filterType, sortBy]);

  const handlePageChange = (newPage) => {
    const newSearchParams = new URLSearchParams(searchParams);
    newSearchParams.set('page', newPage);
    window.history.replaceState(null, '', `?${newSearchParams}`);
  };

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
          <p className="text-gray-400">Buscando series...</p>
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
            No se encontraron series para "{query}"
          </p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {filteredResults.map((item) => (
              <TVShowCard
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
