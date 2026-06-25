import { useEffect, useState } from 'react';
import { userService } from '../services/api';
import { useAuthStore } from '../stores/authStore';
import MovieCard from '../components/MovieCard';
import FilterBar from '../components/FilterBar';
import { useNavigate } from 'react-router-dom';

export default function Watchlist() {
  const { isAuthenticated } = useAuthStore();
  const [items, setItems] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [size] = useState(12); // Tamaño fijo por página
  const [sort, setSort] = useState('dateadd');
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

  const fetchWatchlist = async () => {
  try {
    setIsLoading(true);
    const response = await userService.getWatchlist(page + 1, size, sort); // ✅ page + 1
    const data = response.data;

    setItems(data.content || []);
    setTotalPages(data.totalPages || 1);
    setTotalElements(data.totalElements || 0);
  } catch (error) {
    console.error('Error fetching watchlist:', error);
    setItems([]);
  } finally {
    setIsLoading(false);
  }
};


    fetchWatchlist();
  }, [isAuthenticated, page, size, sort, navigate]);

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      setPage(newPage);
    }
  };

  const handleSortChange = (newSort) => {
    setSort(newSort);
    setPage(0); // Resetear a la primera página
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 space-y-8">
      <h1 className="text-3xl font-bold">Mi Lista</h1>

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
          <p className="text-gray-400 text-lg">Tu lista está vacía</p>
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
              disabled={page === 0}
              className="px-4 py-2 bg-gray-700 text-white rounded disabled:opacity-50"
            >
              Anterior
            </button>

            <span className="text-gray-400">
              Página {page + 1} de {totalPages}
            </span>

            <button
              onClick={() => handlePageChange(page + 1)}
              disabled={page >= totalPages - 1}
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
