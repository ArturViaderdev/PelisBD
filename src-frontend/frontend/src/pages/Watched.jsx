import { useEffect, useState } from 'react';
import { userService } from '../services/api';
import { useAuthStore } from '../stores/authStore';
import MovieCard from '../components/MovieCard';
import FilterBar from '../components/FilterBar';
import { useNavigate } from 'react-router-dom';

export default function Watched() {
  const { isAuthenticated } = useAuthStore();
  const [items, setItems] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [page, setPage] = useState(1); // ✅ page=1 es la primera
  const [size] = useState(12);
  const [sort, setSort] = useState('recent');
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    const fetchWatched = async () => {
      try {
        setIsLoading(true);
        const response = await userService.getWatchedList(page, size, sort);
        const data = response.data;

        setItems(data.content || []);
        setTotalPages(data.totalPages || 1);
        setTotalElements(data.totalElements || 0);
      } catch (error) {
        console.error('Error fetching watched list:', error);
        setItems([]);
      } finally {
        setIsLoading(false);
      }
    };

    fetchWatched();
  }, [isAuthenticated, page, size, sort, navigate]);

  const handlePageChange = (newPage) => {
    if (newPage >= 1 && newPage <= totalPages) {
      setPage(newPage);
    }
  };

  const handleSortChange = (newSort) => {
    setSort(newSort);
    setPage(1); // Resetear a la primera página
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 space-y-8">
      <h1 className="text-3xl font-bold">Películas y Series Vistas</h1>

      <FilterBar
        filterType="all"
        sortBy={sort}
        onFilterChange={() => {}} // No se usa
        onSortChange={handleSortChange}
      />

      {isLoading ? (
        <div className="text-center py-12">
          <p className="text-gray-400">Cargando...</p>
        </div>
      ) : items.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-gray-400 text-lg">Aún no has marcado nada como visto</p>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {items.map((item) => (
              <MovieCard
                key={`${item.type}-${item.id}`}
                item={item}
                type={item.type}
              />
            ))}
          </div>

          {/* Paginación */}
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
        </>
      )}
    </div>
  );
}
