import { Link } from 'react-router-dom';
import { FiBookmark, FiCheck, FiStar } from 'react-icons/fi';
import { useState } from 'react';
import { useAuthStore } from '../stores/authStore';
import { userService } from '../services/api';

export default function MovieCard({ item, type = 'movie', onWatchlistToggle, onWatchedToggle }) {
  const [isWatchlisted, setIsWatchlisted] = useState(item.watchListed || false);
  const [isWatched, setIsWatched] = useState(item.watched || false);
  const user = useAuthStore(state => state.user);

  const handleWatchedToggle = async (movieId, movieType) => {
    if (!user) return;
    try {
      if (isWatched) {
        await userService.removeFromWatched(movieType, movieId);
      } else {
        await userService.addToWatched(movieType, movieId);
      }
      setIsWatched(!isWatched);
      onWatchedToggle?.(movieId, movieType, !isWatched);
    } catch (error) {
      console.error('Error updating watched status:', error);
    }
  };

  const handleWatchlistToggle = async (movieId, movieType) => {
    if (!user) return;
    try {
      if (isWatchlisted) {
        await userService.removeFromWatchlist(movieType, movieId);
      } else {
        await userService.addToWatchlist(movieType, movieId);
      }
      setIsWatchlisted(!isWatchlisted);
      onWatchlistToggle?.(movieId, movieType, !isWatchlisted);
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
    : new Date(item.first_air_date)?.getFullYear() || '—';

  const userRating = item.averageRating || item.vote_average;
  const ratingDisplay = userRating ? userRating.toFixed(1) : 'N/A';

  return (
    <div className="group relative overflow-hidden rounded-lg card transform transition hover:scale-105 bg-gray-900">
      <Link to={`/${type}/${item.id}`} className="block">
        <div className="relative h-96 overflow-hidden bg-gray-800">
          <img
            src={posterUrl}
            alt={title}
            className="w-full h-full object-cover transition group-hover:scale-110"
            loading="lazy"
          />
          <div className="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-60 transition-all duration-300 flex items-end">
            <div className="w-full p-4 translate-y-full group-hover:translate-y-0 transition-transform duration-300">
              <h3 className="font-bold text-white line-clamp-2 mb-2">{title}</h3>
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-1 text-yellow-400">
                  <FiStar size={16} fill="currentColor" />
                  <span className="text-sm font-semibold">{ratingDisplay}</span>
                </div>
                <span className="text-gray-300 text-sm">{year}</span>
              </div>
            </div>
          </div>
        </div>
      </Link>

      <div className="relative z-10 p-3 bg-gray-900">
        <h3 className="font-semibold text-white truncate mb-1">{title}</h3>
        <p className="text-gray-400 text-sm">{year}</p>

        <div className="flex gap-2 mt-3">
          <button
            onClick={(e) => {
              e.preventDefault();
              e.stopPropagation();
              handleWatchlistToggle(item.id, type);
            }}
            className={`flex-1 flex items-center justify-center gap-1 py-2 rounded text-sm transition ${
              isWatchlisted
                ? 'bg-primary text-white'
                : 'bg-gray-800 text-gray-300 hover:bg-gray-700'
            }`}
          >
            <FiBookmark size={16} /> Lista
          </button>

          <button
            onClick={(e) => {
              e.preventDefault();
              e.stopPropagation();
              handleWatchedToggle(item.id, type);
            }}
            className={`flex-1 flex items-center justify-center gap-1 py-2 rounded text-sm transition ${
              isWatched
                ? 'bg-green-600 text-white'
                : 'bg-gray-800 text-gray-300 hover:bg-gray-700'
            }`}
          >
            <FiCheck size={16} /> Visto
          </button>
        </div>
      </div>
    </div>
  );
}
