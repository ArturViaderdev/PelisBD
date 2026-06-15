import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { tvService, userService } from '../services/api';
import { useAuthStore } from '../stores/authStore';
import { FiCheck } from 'react-icons/fi';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';

export default function SeasonDetail() {
  const { id, seasonNumber } = useParams();
  const navigate = useNavigate();
  const { user } = useAuthStore();
  const [season, setSeason] = useState(null);
  const [tvShow, setTvShow] = useState(null); // ✅ Datos de la serie
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchSeasonAndShow = async () => {
      try {
        setIsLoading(true);
        const [seasonRes, showRes] = await Promise.all([
          tvService.getTVSeasonDetail(id, seasonNumber),
          tvService.getTVDetail(id) // ✅ Obtenemos los datos de la serie
        ]);

        const seasonData = seasonRes.data;
        const tvData = showRes.data;

        if (!seasonData || !seasonData.episodes) {
          setError('Temporada no encontrada');
          return;
        }

        setSeason(seasonData);
        setTvShow(tvData); // ✅ Guardamos datos de la serie
      } catch (err) {
        console.error('Error fetching data:', err);
        setError('No se pudo cargar la temporada o la serie');
      } finally {
        setIsLoading(false);
      }
    };

    fetchSeasonAndShow();
  }, [id, seasonNumber]);

  const handleMarkEpisode = async (episodeNumber, watched) => {
    if (!user) return;

    try {
      await userService.markEpisode(id, seasonNumber, episodeNumber, watched);
      setSeason(prev => {
        if (!prev) return prev;
        const updatedEpisodes = prev.episodes.map(ep => 
          ep.episode_number === episodeNumber 
            ? { ...ep, watched } 
            : ep
        );
        return { ...prev, episodes: updatedEpisodes };
      });
    } catch (error) {
      console.error('Error marking episode:', error);
    }
  };

  const handleMarkAll = async () => {
    if (!user) return;

    try {
      const allEpisodes = season?.episodes || [];
      const episodesToMark = allEpisodes.filter(ep => !ep.watched);

      if (episodesToMark.length === 0) {
        alert('Ya has visto todos los episodios de esta temporada');
        return;
      }

      await Promise.all(
        episodesToMark.map(ep => 
          userService.markEpisode(id, seasonNumber, ep.episode_number, true)
        )
      );

      setSeason(prev => {
        if (!prev) return prev;
        const updatedEpisodes = prev.episodes.map(ep => ({
          ...ep,
          watched: true
        }));
        return { ...prev, episodes: updatedEpisodes };
      });

      alert('Todos los episodios marcados como vistos');
    } catch (error) {
      console.error('Error marcando todos los episodios:', error);
      alert('Error al marcar todos los episodios');
    }
  };

  const handleEpisodeClick = (episodeNumber) => {
    navigate(`/tv/${id}/season/${seasonNumber}/episode/${episodeNumber}`);
  };

  if (isLoading) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-12 text-center">
        <p className="text-gray-400">Cargando temporada y serie...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-12 text-center">
        <p className="text-red-400 text-lg">{error}</p>
        <button
          onClick={() => navigate(-1)}
          className="mt-4 text-primary hover:underline"
        >
          Volver
        </button>
      </div>
    );
  }

  if (!season || !tvShow) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-12 text-center">
        <p className="text-gray-400">Temporada o serie no encontrada</p>
      </div>
    );
  }

  const isComplete = season.episodes?.every(ep => ep.watched);

  // ✅ URL del poster de la serie
  const posterUrl = tvShow.poster_path
    ? `https://image.tmdb.org/t/p/w342${tvShow.poster_path}`
    : '/placeholder-poster.jpg'; // Opcional: imagen por defecto

  return (
    <div className="max-w-4xl mx-auto px-4 py-8 space-y-8">
      {/* Banner de la serie */}
      <div className="flex flex-col md:flex-row gap-6 items-center md:items-start">
        <img
          src={posterUrl}
          alt={tvShow.name}
          className="w-40 h-56 object-cover rounded-lg shadow-lg"
        />

        <div className="flex-1 text-center md:text-left">
          <h1 className="text-3xl font-bold text-white mb-2">
            {tvShow.name}
          </h1>
          {season.name && (
            <p className="text-gray-300 text-sm mb-3">
              {season.name}
            </p>
          )}
          {tvShow.overview && (
            <p className="text-gray-300 text-sm leading-relaxed mt-4">
              {tvShow.overview}
            </p>
          )}
        </div>
      </div>

      {/* Botón para marcar todos */}
      {user && (
        <div className="text-center">
          <button
            onClick={handleMarkAll}
            className="flex items-center gap-2 px-6 py-3 bg-primary text-white rounded-lg hover:bg-indigo-600 transition mx-auto"
          >
            <FiCheck /> {isComplete ? 'Todos vistos' : 'Marcar todos como visto'}
          </button>
        </div>
      )}

      {/* Lista de episodios */}
      <div className="space-y-4">
        {season.episodes?.map((episode) => (
          <div
            key={episode.episode_number}
            className={`flex items-center gap-4 p-4 rounded-lg border transition cursor-pointer hover:bg-gray-700 ${
              episode.watched
                ? 'bg-green-900 border-green-700'
                : 'bg-gray-800 border-gray-700'
            }`}
            onClick={() => handleEpisodeClick(episode.episode_number)}
          >
            <div className="w-10 h-10 flex items-center justify-center bg-primary text-white rounded-full text-sm font-bold">
              {episode.episode_number}
            </div>

            <div className="flex-1">
              <h3 className="font-medium text-white">
                {episode.name || 'Episodio sin título'}
              </h3>
              {episode.overview && (
                <p className="text-gray-400 text-sm mt-1 line-clamp-2">
                  {episode.overview}
                </p>
              )}
            </div>

            {episode.air_date && (
              <div className="text-gray-500 text-xs text-right">
                {format(new Date(episode.air_date), 'd MMM', { locale: es })}
              </div>
            )}

            {/* ✅ Botón de marcado (no play) */}
            {user && (
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  handleMarkEpisode(episode.episode_number, !episode.watched);
                }}
                className={`ml-4 p-2 rounded-full transition ${
                  episode.watched
                    ? 'bg-red-600 hover:bg-red-500'
                    : 'bg-green-600 hover:bg-green-500'
                }`}
              >
                {episode.watched ? (
                  <FiCheck className="text-white" size={16} />
                ) : (
                  <FiCheck className="text-white" size={16} /> 
                )}
              </button>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
