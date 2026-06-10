import { FiFilter } from 'react-icons/fi';

export default function FilterBar({
  filterType = 'all',
  sortBy = 'popularity',
  onFilterChange,
  onSortChange,
}) {
  return (
    <div className="bg-gray-900 rounded-lg p-4 space-y-4">
      <div className="flex items-center gap-2 text-lg font-bold">
        <FiFilter /> Filtros y Ordenación
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-semibold mb-2">Ver:</label>
          <select
            value={filterType}
            onChange={(e) => onFilterChange && onFilterChange(e.target.value)}
            className="input-field"
          >
            <option value="all">Todos</option>
            <option value="watched">Vistos</option>
            <option value="watchlist">Mi Lista</option>
            <option value="rated">Calificados</option>
          </select>
        </div>

        <div>
          <label className="block text-sm font-semibold mb-2">Ordenar por:</label>
          <select
            value={sortBy}
            onChange={(e) => onSortChange && onSortChange(e.target.value)}
            className="input-field"
          >
            <option value="popularity">Popularidad</option>
            <option value="rating">Calificación</option>
            <option value="recent">Recientes</option>
            <option value="title">Título</option>
          </select>
        </div>
      </div>
    </div>
  );
}
