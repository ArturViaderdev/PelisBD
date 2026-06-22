import { Link } from 'react-router-dom';
import { FiBookmark, FiCheck, FiStar } from 'react-icons/fi';
import { useEffect, useState } from 'react';
import { useAuthStore } from '../stores/authStore';
import { userService } from '../services/api';

export default function MovieCard({
  item,
  type = 'movie',
  onWatchlistToggle,
  onWatchedToggle,
}) {
  const user = useAuthStore((state) => state.user);

  const [isWatchlisted, setIsWatchlisted] = useState(Boolean(item.watchListed));
  const [isWatched, setIsWatched] = useState(Boolean(item.watched));

  useEffect(() => {
    setIsWatchlisted(Boolean(item.watchListed));
    setIsWatched(Boolean(item.watched));
  }, [item.watchListed, item.watched, item.id]);

  const handleWatchedToggle = async (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (!user) return;

    try {
      if (isWatched) {
        await userService.removeFromWatched(type, item.id);
      } else {
        await userService.addToWatched(type, item.id);
      }

      const nextValue = !isWatched;
      setIsWatched(nextValue);
      onWatchedToggle?.(item.id, type, nextValue);
    } catch (error) {
      console.error('Error updating watched status:', error);
    }
  };

  const handleWatchlistToggle = async (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (!user) return;

    try {
      if (isWatchlisted) {
        await userService.removeFromWatchlist(type, item.id);
      } else {
        await userService.addToWatchlist(type, item.id);
      }

      const nextValue = !isWatchlisted;
      setIsWatchlisted(nextValue);
      onWatchlistToggle?.(item.id, type, nextValue);
    } catch (error) {
      console.error('Error updating watchlist:', error);
    }
  };

  const posterUrl = item.poster_path
    ? `https://image.tmdb.org/t/p/w342${item.poster_path}`
    : 'https://via.placeholder.com/342x513?text=Sin+Imagen';

  const title = item.title || item.name || 'Sin título';
  const year = item.release_date
    ? new Date(item.release_date).getFullYear()
    : item.first_air_date
      ? new Date(item.first_air_date).getFullYear()
      : '—';

  const userRating = item.averageRating ?? item.vote_average;
  const ratingDisplay =
    typeof userRating === 'number' ? userRating.toFixed(1) : 'N/A';

  return (
    <div className="group overflow-hidden rounded-lg card bg-gray-900 transition hover:scale-105">
      <Link to={`/${type}/${item.id}`} className="block">
        <div className="relative h-96 overflow-hidden bg-gray-800">
          <img
            src={posterUrl}
            alt={title}
            className="h-full w-full object-cover transition group-hover:scale-110"
            loading="lazy"
          />

          <div className="absolute inset-0 flex items-end bg-black bg-opacity-0 transition-all duration-300 group-hover:bg-opacity-60">
            <div className="w-full translate-y-full p-4 transition-transform duration-300 group-hover:translate-y-0">
              <h3 className="mb-2 line-clamp-2 font-bold text-white">{title}</h3>

              <div className="flex items-center justify-between">
                <div className="flex items-center gap-1 text-yellow-400">
                  <FiStar size={16} fill="currentColor" />
                  <span className="text-sm font-semibold">{ratingDisplay}</span>
                </div>

                <span className="text-sm text-gray-300">{year}</span>
              </div>
            </div>
          </div>
        </div>
      </Link>

      <div className="p-3 bg-gray-900">
        <h3 className="mb-1 truncate font-semibold text-white">{title}</h3>
        <p className="text-sm text-gray-400">{year}</p>

        {user && (
          <div className="mt-3 flex gap-2">
            <button
              type="button"
              onClick={handleWatchlistToggle}
              className={`flex flex-1 items-center justify-center gap-1 rounded py-2 text-sm transition ${
                isWatchlisted
                  ? 'bg-primary text-white'
                  : 'bg-gray-800 text-gray-300 hover:bg-gray-700'
              }`}
              aria-label={
                isWatchlisted
                  ? `Quitar ${title} de mi lista`
                  : `Añadir ${title} a mi lista`
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
                  : 'bg-gray-800 text-gray-300 hover:bg-gray-700'
              }`}
              aria-label={
                isWatched
                  ? `Quitar ${title} de vistas`
                  : `Marcar ${title} como vista`
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
