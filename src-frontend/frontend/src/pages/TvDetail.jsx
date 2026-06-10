/**
 * TvDetail.jsx
 * Detalle de una serie con el componente VideoList reutilizable
 */

import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { tvService, reviewService, userService } from '../services/api';
import { useAuthStore } from '../stores/authStore';
import RatingStars from '../components/RatingStars';
import CommentSection from '../components/CommentSection';
import { FiBookmark, FiCheck } from 'react-icons/fi';
import VideoList from '../components/VideoList';

export default function TvDetail() {
  const { id } = useParams();
  const { user } = useAuthStore();
  const [tv, setTv] = useState(null);
  const [rating, setRating] = useState(0);
  const [comments, setComments] = useState([]);
  const [isWatched, setIsWatched] = useState(false);
  const [isWatchlisted, setIsWatchlisted] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true);
        const tvRes = await tvService.getTvDetail(id);
        setTv(tvRes.data);

        if (user) {
          const [ratingsRes, commentsRes] = await Promise.all([
            reviewService.getItemRatings('tv', id),
            reviewService.getComments('tv', id, false),
          ]);

          const userRating = ratingsRes.data.ratings?.find(r => r.userId === user.id);
          if (userRating) setRating(userRating.rating);
          setComments(commentsRes.data || []);
        }
      } catch (error) {
        console.error('Error fetching TV show details:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [id, user]);

  const handleRate = async (newRating) => {
    if (!user) {
      alert('Debes iniciar sesión para calificar');
      return;
    }
    try {
      await reviewService.rateItem('tv', id, newRating);
      setRating(newRating);
    } catch (error) {
      console.error('Error rating TV show:', error);
    }
  };

  const handleAddComment = async (text, isPublic) => {
    if (!user) return;
    try {
      const response = await reviewService.addComment('tv', id, text, isPublic);
      setComments([...comments, response.data]);
    } catch (error) {
      console.error('Error adding comment:', error);
    }
  };

  const handleDeleteComment = async (commentId) => {
    try {
      await reviewService.deleteComment(commentId);
      setComments(comments.filter(c => c.id !== commentId));
    } catch (error) {
      console.error('Error deleting comment:', error);
    }
  };

  const handleWatchedToggle = async () => {
    if (!user) return;
    try {
      if (isWatched) {
        await userService.removeFromWatched('tv', id);
      } else {
        await userService.addToWatched('tv', id);
      }
      setIsWatched(!isWatched);
    } catch (error) {
      console.error('Error updating watched status:', error);
    }
  };

  const handleWatchlistToggle = async () => {
    if (!user) return;
    try {
      if (isWatchlisted) {
        await userService.removeFromWatchlist('tv', id);
      } else {
        await userService.addToWatchlist('tv', id);
      }
      setIsWatchlisted(!isWatchlisted);
    } catch (error) {
      console.error('Error updating watchlist:', error);
    }
  };

  if (isLoading) return <div className="text-center py-12">Cargando...</div>;
  if (!tv) return <div className="text-center py-12">Serie no encontrada</div>;

  const backdropUrl = tv.backdrop_path
    ? `https://image.tmdb.org/t/p/w1280${tv.backdrop_path}`
    : '';

  return (
    <div className="max-w-6xl mx-auto px-4 py-8 space-y-8">
      {/* Backdrop */}
      {backdropUrl && (
        <div className="relative rounded-lg overflow-hidden h-96">
          <img
            src={backdropUrl}
            alt={tv.name}
            className="w-full h-full object-cover"
          />
          <div className="absolute inset-0 bg-black bg-opacity-40" />
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
        {/* Póster */}
        <div className="md:col-span-1">
          <img
            src={`https://image.tmdb.org/t/p/w342${tv.poster_path}`}
            alt={tv.name}
            className="w-full rounded-lg shadow-lg"
          />
        </div>

        {/* Información */}
        <div className="md:col-span-3 space-y-4">
          <h1 className="text-4xl font-bold">{tv.name}</h1>

          <div className="flex gap-4 flex-wrap">
            <button
              onClick={handleWatchedToggle}
              className={`flex items-center gap-2 px-4 py-2 rounded-lg transition ${
                isWatched
                  ? 'bg-green-600 text-white'
                  : 'bg-gray-800 text-gray-300 hover:bg-gray-700'
              }`}
            >
              <FiCheck /> {isWatched ? 'Visto' : 'Marcar como visto'}
            </button>
            <button
              onClick={handleWatchlistToggle}
              className={`flex items-center gap-2 px-4 py-2 rounded-lg transition ${
                isWatchlisted
                  ? 'bg-primary text-white'
                  : 'bg-gray-800 text-gray-300 hover:bg-gray-700'
              }`}
            >
              <FiBookmark /> {isWatchlisted ? 'En mi lista' : 'Agregar a mi lista'}
            </button>
          </div>

          <div>
            <p className="text-gray-400 text-sm mb-2">Calificación de TMDB</p>
            <div className="flex items-center gap-4">
              <div className="text-4xl font-bold text-yellow-400">
                {tv.vote_average?.toFixed(1)}
              </div>
              <div className="text-gray-400">
                {tv.vote_count?.toLocaleString()} votos
              </div>
            </div>
          </div>

          {user && (
            <div>
              <p className="text-gray-400 text-sm mb-2">Tu calificación</p>
              <RatingStars rating={rating} onRate={handleRate} />
            </div>
          )}

          <div>
            <p className="text-gray-400 text-sm mb-2">Descripción</p>
            <p className="text-white leading-relaxed">{tv.overview}</p>
          </div>

          {tv.first_air_date && (
            <div>
              <p className="text-gray-400 text-sm">
                Fecha de lanzamiento: {new Date(tv.first_air_date).toLocaleDateString('es-ES')}
              </p>
            </div>
          )}

          {tv.number_of_episodes && (
            <div>
              <p className="text-gray-400 text-sm">Episodios: {tv.number_of_episodes}</p>
            </div>
          )}

          {tv.number_of_seasons && (
            <div>
              <p className="text-gray-400 text-sm">Temporadas: {tv.number_of_seasons}</p>
            </div>
          )}

          {/* ✅ VideoList reutilizable */}
          {tv.videos && tv.videos.length > 0 && (
            <VideoList videos={tv.videos} title={tv.name} />
          )}
        </div>
      </div>

      {/* Comentarios */}
      {user && (
        <div className="border-t border-gray-800 pt-8">
          <CommentSection
            comments={comments}
            onAddComment={handleAddComment}
            onDeleteComment={handleDeleteComment}
            currentUserId={user.id}
          />
        </div>
      )}
    </div>
  );
}