import { useEffect, useState } from 'react';
import { userService } from '../services/api';
import MovieCard from '../components/MovieCard';
import FilterBar from '../components/FilterBarGlobal';

export default function GlobalWatched() {
    const [items, setItems] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [page, setPage] = useState(1);
    const [size] = useState(12);
    const [sort, setSort] = useState('dateadd');
    const [totalPages, setTotalPages] = useState(1);
    const [totalElements, setTotalElements] = useState(0);

    useEffect(() => {
        const fetchGlobalWatched = async () => {
            try {
                setIsLoading(true);
                const response = await userService.getGlobalWatchedList(page, size, sort);
                const data = response.data;

                setItems(data.content || []);
                setTotalPages(data.totalPages || 1);
                setTotalElements(data.totalElements || 0);
            } catch (error) {
                console.error('Error fetching global watched list:', error);
                setItems([]);
                setTotalPages(1);
                setTotalElements(0);
            } finally {
                setIsLoading(false);
            }
        };

        fetchGlobalWatched();
    }, [page, size, sort]);

    const handlePageChange = (newPage) => {
        if (newPage >= 1 && newPage <= totalPages) {
            setPage(newPage);
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }
    };

    const handleSortChange = (newSort) => {
        setSort(newSort);
        setPage(1);
    };

    return (
        <div className="max-w-7xl mx-auto px-4 py-8 space-y-8">
        <div className="space-y-2">
        <h1 className="text-3xl font-bold">Vistas por la comunidad</h1>
        <p className="text-gray-400">
        {totalElements} título{totalElements === 1 ? '' : 's'} vistos sin duplicados
        </p>
        </div>

        <FilterBar
        filterType="all"
        sortBy={sort}
        onFilterChange={() => {}}
        onSortChange={handleSortChange}
        />

        {isLoading ? (
            <div className="text-center py-12">
            <p className="text-gray-400">Cargando...</p>
            </div>
        ) : items.length === 0 ? (
            <div className="text-center py-12">
            <p className="text-gray-400 text-lg">
            Todavía no hay películas o series vistas
            </p>
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

            <div className="flex justify-between items-center gap-4 mt-8 flex-wrap">
            <button
            onClick={() => handlePageChange(page - 1)}
            disabled={page === 1}
            className="px-4 py-2 bg-gray-700 text-white rounded disabled:opacity-50 disabled:cursor-not-allowed"
            >
            Anterior
            </button>

            <div className="text-gray-400 text-sm sm:text-base text-center">
            Página {page} de {totalPages}
            </div>

            <button
            onClick={() => handlePageChange(page + 1)}
            disabled={page >= totalPages}
            className="px-4 py-2 bg-gray-700 text-white rounded disabled:opacity-50 disabled:cursor-not-allowed"
            >
            Siguiente
            </button>
            </div>
            </>
        )}
        </div>
    );
}
