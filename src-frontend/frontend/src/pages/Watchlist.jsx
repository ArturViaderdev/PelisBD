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
  const [filterType, setFilterType] = useState('all');
  const [sortBy, setSortBy] = useState('recent');
  const navigate = useNavigate();

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    const fetchWatchlist = async () => {
      try {
        setIsLoading(true);
        const response = await userService.getWatchlist();
        setItems(response.data || []);
      } catch (error) {
        console.error('Error fetching watchlist:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchWatchlist();
  }, [isAuthenticated, navigate]);

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 space-y-8">
      <h1 className="text-3xl font-bold">Mi Lista</h1>

      <FilterBar
        filterType={filterType}
        sortBy={sortBy}
        onFilterChange={setFilterType}
        onSortChange={setSortBy}
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
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
          {items.map((item) => (
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
