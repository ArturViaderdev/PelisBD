// src/components/TVShowCard.jsx
import { Link } from 'react-router-dom';

export default function TVShowCard({ item, type = 'tv' }) {
  const { id, name, poster_path, first_air_date, vote_average, number_of_seasons } = item;

  // Formatear la fecha
  const formattedDate = first_air_date
    ? new Date(first_air_date).toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
      })
    : 'Sin fecha';

  // Estrellas para la puntuación
  const renderStars = () => {
    const stars = [];
    const rating = Math.round(vote_average / 2); // 0 a 5 estrellas

    for (let i = 0; i < 5; i++) {
      stars.push(
        <span key={i} className={`text-yellow-400 ${i < rating ? 'opacity-100' : 'opacity-30'}`}>
          ★
        </span>
      );
    }
    return stars;
  };

  return (
    <Link
      to={`/tv/${id}/season/1`}
      className="group block rounded-lg overflow-hidden bg-gray-800 shadow-md hover:shadow-xl transition-all duration-300 transform hover:scale-105"
      aria-label={`Ver detalles de ${name}`}
    >
      {/* Imagen del póster */}
      <div className="relative h-60 w-full overflow-hidden">
        {poster_path ? (
          <img
            src={`https://image.tmdb.org/t/p/w300${poster_path}`}
            alt={name}
            className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
            onError={(e) => {
              e.target.src = '/placeholder-poster.jpg'; // Imagen por defecto
            }}
          />
        ) : (
          <div className="w-full h-full bg-gradient-to-br from-gray-700 to-gray-900 flex items-center justify-center">
            <span className="text-gray-400 text-sm">Sin imagen</span>
          </div>
        )}
      </div>

      {/* Información */}
      <div className="p-3 space-y-1">
        <h3 className="font-semibold text-white text-sm line-clamp-1 group-hover:text-primary transition-colors">
          {name}
        </h3>

        <div className="flex items-center gap-1 text-xs text-gray-400">
          <span>{formattedDate}</span>
          <span>•</span>
          <span>{number_of_seasons} temp.</span>
        </div>

        <div className="flex items-center gap-1">
          <span className="text-yellow-400 font-semibold text-sm">{vote_average.toFixed(1)}</span>
          <span className="text-gray-400 text-xs">/10</span>
          {renderStars()}
        </div>
      </div>
    </Link>
  );
}
