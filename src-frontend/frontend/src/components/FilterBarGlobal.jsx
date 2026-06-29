export default function FilterBar({
  sortBy = 'popularity',
  onSortChange,
}) {
  return (
    <div className="bg-gray-900 rounded-lg p-4 space-y-4">


      <div>
        <label className="block text-sm font-semibold mb-2">Ordenar por:</label>
        <select
          value={sortBy}
          onChange={(e) => onSortChange && onSortChange(e.target.value)}
          className="input-field"
        >
         <option value="dateadd">Fecha de inserción</option>
          <option value="userating">Calificación de los usuarios</option>
          <option value="numrating">Número de votos de los usuarios</option>
          <option value="title">Título</option>
        </select>
      </div>
    </div>
  );
}
