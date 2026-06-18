import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { tvService, reviewService, userService } from '../services/api';
import { useAuthStore } from '../stores/authStore';
import RatingStars from '../components/RatingStars';
import CommentSection from '../components/CommentSection';
import EpisodeTracker from '../components/EpisodeTracker';
import { FiBookmark, FiCheck } from 'react-icons/fi';
import VideoList from '../components/VideoList';

export default function TVDetail() {
  const { id } = useParams();
  const { user } = useAuthStore();
  const [tvShow, setTVShow] = useState(null);
  const [rating, setRating] = useState(0);
  const [comments, setComments] = useState([]);
  const [isWatched, setIsWatched] = useState(false);
  const [isWatchlisted, setIsWatchlisted] = useState(false);
  const [averageRating, setAverageRating] = useState(null);
  const [totalRatings, setTotalRatings] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(null);

  useEffect(() => {
    let mounted = true;

    const fetchData = async () => {
      try {
        setIsLoading(true);

        const tvRes = await tvService.getTVDetail(id);
        const tvData = tvRes.data;

        if (mounted) {
          setTVShow(tvData);
          setIsWatched(Boolean(tvData.watched));
          setIsWatchlisted(Boolean(tvData.watchListed));
        }

        if (user) {
          try {
            const watchedRes = await userService.isMovieWatched('tv', id);
            if (mounted) setIsWatched(Boolean(watchedRes.data?.value));
          } catch (err) {
            console.error('Error checking watched status:', err);
            if (mounted) setIsWatched(Boolean(tvData.watched));
          }

          try {
            const watchlistRes = await userService.isMovieInWatchList('tv', id);
            if (mounted) setIsWatchlisted(Boolean(watchlistRes.data?.value));
          } catch (err) {
            console.error('Error checking watchlisted status:', err);
            if (mounted) setIsWatchlisted(Boolean(tvData.watchListed));
          }

          try {
            const ratingsRes = await reviewService.getItemRatings('tv', id);
            if (mounted) {
              setRating(ratingsRes.data?.userRating ?? 0);
              setAverageRating(ratingsRes.data?.averageRating);
              setTotalRatings(ratingsRes.data?.totalRatings);
            }
          } catch (err) {
            console.error('Error loading ratings:', err);
            if (mounted) {
              setRating(0);
              setAverageRating(null);
              setTotalRatings(null);
            }
          }

          try {
            const commentsRes = await reviewService.getComments('tv', id, false);
            if (mounted) setComments(commentsRes.data || []);
          } catch (err) {
            console.error('Comments not available yet:', err);
            if (mounted) setComments([]);
          }
        }
      } catch (error) {
        console.error('Error fetching TV details:', error);
        if (mounted) setTVShow(null);
      } finally {
        if (mounted) setIsLoading(false);
      }
    };

    fetchData();

    return () => {
      mounted = false;
    };
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
      console.error('Error rating TV:', error);
    }
  };

  const handleMarkEpisode = async (season, episode, watched) => {
    if (!user) return;
    try {
      await userService.markEpisode(id, season, episode, watched);
    } catch (error) {
      console.error('Error marking episode:', error);
    }
  };

  const handleAddComment = async (text, isPublic) => {
    if (!user) return;
    try {
      const response = await reviewService.addComment('tv', id, text, isPublic);
      setComments(prev => [...prev, response.data]);
    } catch (error) {
      console.error('Error adding comment:', error);
    }
  };

  const handleDeleteComment = async (commentId) => {
    try {
      await reviewService.deleteComment(commentId);
      setComments(prev => prev.filter(c => c.id !== commentId));
    } catch (error) {
      console.error('Error deleting comment:', error);
    }
  };

  const handleUpdateComment = async (commentId, text) => {
    try {
      await reviewService.updateComment(commentId, text);
      setComments(prev => prev.map(c => c.id === commentId ? { ...c, text } : c));
      setIsEditing(null);
    } catch (err) {
      console.error('Error al editar:', err);
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
  if (!tvShow) return <div className="text-center py-12">Serie no encontrada</div>;

  const backdropUrl = tvShow.backdrop_path
    ? `https://image.tmdb.org/t/p/w1280${tvShow.backdrop_path}`
    : '';

  return (
    <div className="max-w-6xl mx-auto px-4 py-8 space-y-8">
      {backdropUrl && (
        <div className="relative rounded-lg overflow-hidden h-96">
          <img
            src={backdropUrl}
            alt={tvShow.name}
            className="w-full h-full object-cover"
          />
          <div className="absolute inset-0 bg-black bg-opacity-40" />
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
        <div className="md:col-span-1">
          <img
            src={`https://image.tmdb.org/t/p/w342${tvShow.poster_path}`}
            alt={tvShow.name}
            className="w-full rounded-lg shadow-lg"
          />
        </div>

        <div className="md:col-span-3 space-y-4">
          <h1 className="text-4xl font-bold">{tvShow.name}</h1>

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
                {tvShow.vote_average?.toFixed(1) || '—'}
              </div>
              <div className="text-gray-400">
                {tvShow.vote_count?.toLocaleString() || '0'} votos
              </div>
            </div>
          </div>

          <div>
            <p className="text-gray-400 text-sm mb-2">Puntuación media del usuario</p>
            <div className="flex items-center gap-4">
              <div className="text-4xl font-bold text-yellow-400">
                {averageRating?.toFixed(1) || '—'}
              </div>
              <div className="text-gray-400">
                {totalRatings?.toLocaleString() || '0'} calificaciones
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
            <p className="text-white leading-relaxed">{tvShow.overview}</p>
          </div>

          <div className="grid grid-cols-2 gap-4 text-sm">
            {tvShow.first_air_date && (
              <div>
                <p className="text-gray-400">Fecha de estreno</p>
                <p>{new Date(tvShow.first_air_date).toLocaleDateString('es-ES')}</p>
              </div>
            )}
            {tvShow.number_of_seasons && (
              <div>
                <p className="text-gray-400">Temporadas</p>
                <p>{tvShow.number_of_seasons}</p>
              </div>
            )}
            {tvShow.number_of_episodes && (
              <div>
                <p className="text-gray-400">Episodios</p>
                <p>{tvShow.number_of_episodes}</p>
              </div>
            )}
            {tvShow.status && (
              <div>
                <p className="text-gray-400">Estado</p>
                <p>{tvShow.status}</p>
              </div>
            )}
          </div>
        </div>
      </div>

      {tvShow.videos && tvShow.videos.length > 0 && (
        <VideoList videos={tvShow.videos} title={tvShow.name} />
      )}

      {tvShow.seasons && tvShow.seasons.length > 0 && (
        <div className="border-t border-gray-800 pt-8">
          <h2 className="text-2xl font-bold mb-6">Temporadas</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {tvShow.seasons.map((season) => {
              const watchedEpisodes = (season.episodes || []).filter(e => e.watched);
              const isComplete = watchedEpisodes.length === season.episode_count;

              return (
                <a
                  key={season.season_number}
                  href={`/tv/${id}/season/${season.season_number}`}
                  className="block p-4 bg-gray-800 rounded-lg hover:bg-gray-700 transition text-center"
                >
                  <div className="text-xl font-bold text-primary">
                    Temporada {season.season_number}
                  </div>
                  <div className="text-gray-400 text-sm mt-1">
                    {season.episode_count} episodios
                  </div>
                  {season.air_date && (
                    <div className="text-gray-500 text-xs mt-1">
                      {new Date(season.air_date).toLocaleDateString('es-ES')}
                    </div>
                  )}
                  {isComplete && (
                    <div className="text-green-400 text-xs mt-1 flex items-center justify-center gap-1">
                      <FiCheck size={12} /> Completada
                    </div>
                  )}
                </a>
              );
            })}
          </div>
        </div>
      )}

      {user && (
        <div className="border-t border-gray-800 pt-8">
          <CommentSection
            comments={comments}
            onAddComment={handleAddComment}
            onDeleteComment={handleDeleteComment}
            onEditComment={handleUpdateComment}
            currentUserId={user?.id}
            isEditing={isEditing}
            setIsEditing={setIsEditing}
          />
        </div>
      )}
    </div>
  );
}
