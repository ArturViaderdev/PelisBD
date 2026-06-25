import { Link } from 'react-router-dom';
import { FiBookmark, FiCheck } from 'react-icons/fi';
import { useEffect, useState } from 'react';
import { useAuthStore } from '../stores/authStore';
import { userService } from '../services/api';

export default function TVShowCard({
  item,
  type = 'tv',
  onWatchlistToggle,
  onWatchedToggle,
}) {
  const {
    id,
    name,
    poster_path,
    first_air_date,
    vote_average,
    number_of_seasons,
    watchListed,
    watched,
  } = item;

  const user = useAuthStore((state) => state.user);

  const [isWatchlisted, setIsWatchlisted] = useState(Boolean(watchListed));
  const [isWatched, setIsWatched] = useState(Boolean(watched));

  useEffect(() => {
    setIsWatchlisted(Boolean(watchListed));
    setIsWatched(Boolean(watched));
  }, [watchListed, watched, id]);

  const formattedDate = first_air_date
    ? new Date(first_air_date).toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
      })
    : 'Sin fecha';

  const handleWatchlistToggle = async (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (!user) return;

    try {
      if (isWatchlisted) {
        await userService.removeFromWatchlist(type, id);
      } else {
        await userService.addToWatchlist(type, id);
      }

      const nextValue = !isWatchlisted;
      setIsWatchlisted(nextValue);
      onWatchlistToggle?.(id, type, nextValue);
    } catch (error) {
      console.error('Error updating watchlist:', error);
    }
  };

  const handleWatchedToggle = async (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (!user) return;

    try {
      if (isWatched) {
        await userService.removeFromWatched(type, id);
      } else {
        await userService.addToWatched(type, id);
      }

      const nextValue = !isWatched;
      setIsWatched(nextValue);
      onWatchedToggle?.(id, type, nextValue);
    } catch (error) {
      console.error('Error updating watched status:', error);
    }
  };

  const renderStars = () => {
    const stars = [];
    const rating = Math.round((vote_average || 0) / 2);

    for (let i = 0; i < 5; i++) {
      stars.push(
        <span
          key={i}
          className={`text-yellow-400 ${i < rating ? 'opacity-100' : 'opacity-30'}`}
        >
          ★
        </span>
      );
    }

    return stars;
  };

  return (
    <div className="group block overflow-hidden rounded-lg bg-gray-800 shadow-md transition-all duration-300 hover:scale-105 hover:shadow-xl">
      <Link
        to={`/tv/${id}`}
        className="block"
        aria-label={`Ver detalles de ${name}`}
      >
        <div className="relative h-60 w-full overflow-hidden">
          {poster_path ? (
            <img
              src={`https://image.tmdb.org/t/p/w300${poster_path}`}
              alt={name}
              className="h-full w-full object-cover transition-transform duration-300 group-hover:scale-110"
            />
          ) : (
            <div className="flex h-full w-full items-center justify-center bg-gradient-to-br from-gray-700 to-gray-900">
              <span className="text-sm text-gray-400">Sin imagen</span>
            </div>
          )}
        </div>
      </Link>

      <div className="space-y-1 p-3">
        <h3 className="line-clamp-1 text-sm font-semibold text-white transition-colors group-hover:text-primary">
          {name}
        </h3>

        <div className="flex items-center gap-1 text-xs text-gray-400">
          <span>{formattedDate}</span>
          <span>•</span>
          <span>{number_of_seasons ?? 0} temp.</span>
        </div>

        <div className="flex items-center gap-1">
          <span className="text-sm font-semibold text-yellow-400">
            {typeof vote_average === 'number' ? vote_average.toFixed(1) : '0.0'}
          </span>
          <span className="text-xs text-gray-400">/10</span>
          {renderStars()}
        </div>

        {user && (
          <div className="mt-3 flex gap-2">
            <button
              type="button"
              onClick={handleWatchlistToggle}
              className={`flex flex-1 items-center justify-center gap-1 rounded py-2 text-sm transition ${
                isWatchlisted
                  ? 'bg-primary text-white'
                  : 'bg-gray-700 text-gray-200 hover:bg-gray-600'
              }`}
              aria-label={
                isWatchlisted
                  ? `Quitar ${name} de mi lista`
                  : `Añadir ${name} a mi lista`
              }
            >
              <FiBookmark size={16} />
              Lista
            </button>

            <button
              type="button"
              onClick={handleWatchedToggle}
              className={`flex flex-1 items-center justify-center gap-1 rounded py-2 text-sm transition ${
                isWatched
                  ? 'bg-green-600 text-white'
                  : 'bg-gray-700 text-gray-200 hover:bg-gray-600'
              }`}
              aria-label={
                isWatched
                  ? `Quitar ${name} de vistas`
                  : `Marcar ${name} como vista`
              }
            >
              <FiCheck size={16} />
              Visto
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
