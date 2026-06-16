import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { moviesService, reviewService, userService } from '../services/api';
import { useAuthStore } from '../stores/authStore';
import RatingStars from '../components/RatingStars';
import CommentSection from '../components/CommentSection';
import { FiBookmark, FiCheck } from 'react-icons/fi';
import VideoList from '../components/VideoList'; // ✅ Importar el componente reutilizable

export default function MovieDetail() {
  const { id } = useParams();
  const { user } = useAuthStore();
  const [movie, setMovie] = useState(null);
  const [rating, setRating] = useState(0);
  const [comments, setComments] = useState([]);
  const [isWatched, setIsWatched] = useState(false);
  const [isWatchlisted, setIsWatchlisted] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true);
        const movieRes = await moviesService.getMovieDetail(id);
        setMovie(movieRes.data);

        if (user) {
         try {
            const watchedRes = await userService.isMovieWatched('movie',id);
            setIsWatched(watchedRes.data.value);
        } catch (err) {
          console.error('Error checking watched status:', err);
          setIsWatched(false);
        }
        try {
            const watchedRes = await userService.isMovieInWatchList('movie',id);
            setIsWatchlisted(watchedRes.data.value);
        } catch (err) {
            console.error('Error checking watchlisted status:', err);
            setIsWatchlisted(false);
        }
        
          const [ratingsRes, commentsRes] = await Promise.all([
            reviewService.getItemRatings('movie', id),
            reviewService.getComments('movie', id, false),
          ]);

          const userRating = ratingsRes.data.ratings?.find(r => r.userId === user.id);
          if (userRating) setRating(userRating.rating);
          setComments(commentsRes.data || []);
        }
      } catch (error) {
        console.error('Error fetching movie details:', error);
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
      await reviewService.rateItem('movie', id, newRating);
      setRating(newRating);
    } catch (error) {
      console.error('Error rating movie:', error);
    }
  };

  const handleAddComment = async (text, isPublic) => {
    if (!user) return;
    try {
      const response = await reviewService.addComment('movie', id, text, isPublic);
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
        await userService.removeFromWatched('movie', id);
      } else {
        await userService.addToWatched('movie', id);
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
        await userService.removeFromWatchlist('movie', id);
      } else {
        await userService.addToWatchlist('movie', id);
      }
      setIsWatchlisted(!isWatchlisted);
    } catch (error) {
      console.error('Error updating watchlist:', error);
    }
  };

  if (isLoading) return <div className="text-center py-12">Cargando...</div>;
  if (!movie) return <div className="text-center py-12">Película no encontrada</div>;

  const backdropUrl = movie.backdrop_path
    ? `https://image.tmdb.org/t/p/w1280${movie.backdrop_path}`
    : '';

  return (
    <div className="max-w-6xl mx-auto px-4 py-8 space-y-8">
      {/* Backdrop */}
      {backdropUrl && (
        <div className="relative rounded-lg overflow-hidden h-96">
          <img
            src={backdropUrl}
            alt={movie.title}
            className="w-full h-full object-cover"
          />
          <div className="absolute inset-0 bg-black bg-opacity-40" />
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
        {/* Póster */}
        <div className="md:col-span-1">
          <img
            src={`https://image.tmdb.org/t/p/w342${movie.poster_path}`}
            alt={movie.title}
            className="w-full rounded-lg shadow-lg"
          />
        </div>

        {/* Información */}
        <div className="md:col-span-3 space-y-4">
          <h1 className="text-4xl font-bold">{movie.title}</h1>

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
                {movie.vote_average?.toFixed(1)}
              </div>
              <div className="text-gray-400">
                {movie.vote_count?.toLocaleString()} votos
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
            <p className="text-white leading-relaxed">{movie.overview}</p>
          </div>

          {movie.release_date && (
            <div>
              <p className="text-gray-400 text-sm">
                Fecha de lanzamiento: {new Date(movie.release_date).toLocaleDateString('es-ES')}
              </p>
            </div>
          )}

          {movie.runtime && (
            <div>
              <p className="text-gray-400 text-sm">Duración: {movie.runtime} minutos</p>
            </div>
          )}

          {/* ✅ VideoList reutilizable */}
          {movie.videos && movie.videos.length > 0 && (
            <VideoList videos={movie.videos} title={movie.title} />
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
