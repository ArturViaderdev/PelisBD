import { useEffect, useState } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { tvService, userService } from '../services/api';
import { useAuthStore } from '../stores/authStore';
import { FiCheck, FiArrowLeft } from 'react-icons/fi';
import { format } from 'date-fns';
import { es } from 'date-fns/locale';

export default function EpisodeDetail() {
  const { id, seasonNumber, episodeNumber } = useParams();
  const navigate = useNavigate();
  const location = useLocation(); // ✅ Necesario para detectar cambios
  const { user } = useAuthStore();
  const [episode, setEpisode] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  // ✅ Detectamos si los parámetros han cambiado
  useEffect(() => {
    const fetchEpisodeData = async () => {
      try {
        setIsLoading(true);
        setError(null);

        // ✅ Validar que todos los parámetros existan
        if (!id || !seasonNumber || !episodeNumber) {
          setError('Parámetros inválidos');
          return;
        }

        // ✅ Llamada directa al nuevo endpoint
        const response = await tvService.getTVEpisode(id, seasonNumber, episodeNumber);
        const data = response.data;

        if (!data) {
          setError('Episodio no encontrado');
          return;
        }

        setEpisode(data);
      } catch (err) {
        console.error('Error fetching episode:', err);
        setError('No se pudo cargar el episodio');
      } finally {
        setIsLoading(false);
      }
    };

    fetchEpisodeData();
  }, [id, seasonNumber, episodeNumber, location]); // ✅ Añadimos `location` como dependencia

  const handleMarkEpisode = async (watched) => {
    if (!user) return;

    try {
      await userService.markEpisode(id, seasonNumber, episodeNumber, watched);
      setEpisode(prev => prev ? { ...prev, watched } : null);
    } catch (error) {
      console.error('Error marking episode:', error);
    }
  };

  if (isLoading) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-12 text-center">
        <p className="text-gray-400">Cargando detalle del episodio...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-12 text-center">
        <p className="text-red-400 text-lg">{error}</p>
        <button
          onClick={() => navigate(-1)}
          className="mt-4 text-primary hover:underline flex items-center gap-1 mx-auto"
        >
          <FiArrowLeft size={16} /> Volver
        </button>
      </div>
    );
  }

  if (!episode) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-12 text-center">
        <p className="text-gray-400">Episodio no encontrado</p>
      </div>
    );
  }

  const tvName = episode.tv_name || 'Serie desconocida';
  const episodeName = episode.name || 'Episodio sin título';
  const overview = episode.overview || '';
  const airDate = episode.air_date;
  const stillPath = episode.still_path;

  const stillUrl = stillPath
    ? `https://image.tmdb.org/t/p/w342${stillPath}`
    : '/placeholder-poster.jpg';

  const isWatched = episode.watched;

  return (
    <div className="max-w-4xl mx-auto px-4 py-8 space-y-8">
      {/* Header con botón de volver */}
      <div className="flex items-center gap-4 mb-6">
        <button
          onClick={() => navigate(-1)}
          className="flex items-center gap-1 text-primary hover:underline"
        >
          <FiArrowLeft size={16} /> Volver
        </button>
      </div>

      {/* Banner */}
      <div className="flex flex-col md:flex-row gap-6 items-center md:items-start">
        <img
          src={stillUrl}
          alt={episodeName}
          className="w-40 h-56 object-cover rounded-lg shadow-lg"
        />

        <div className="flex-1">
          <h1 className="text-3xl font-bold text-white mb-2">
            {tvName}
          </h1>
          <p className="text-gray-400 text-sm mb-1">
            Temporada {seasonNumber} • Episodio {episode.episode_number}
          </p>
          <h2 className="text-2xl font-semibold text-white mb-3">
            {episodeName}
          </h2>
          {airDate && (
            <p className="text-gray-500 text-sm mb-4">
              Fecha de emisión: {format(new Date(airDate), 'd MMMM yyyy', { locale: es })}
            </p>
          )}
          {overview && (
            <p className="text-gray-300 text-sm leading-relaxed">
              {overview}
            </p>
          )}
        </div>
      </div>

      {/* Botón de marcar como visto */}
      {user && (
        <div className="text-center">
          <button
            onClick={() => handleMarkEpisode(!isWatched)}
            className={`flex items-center gap-2 px-6 py-3 rounded-lg transition mx-auto ${
              isWatched
                ? 'bg-red-600 hover:bg-red-500 text-white'
                : 'bg-green-600 hover:bg-green-500 text-white'
            }`}
          >
            <FiCheck size={16} /> {isWatched ? 'Quitar visto' : 'Marcar como visto'}
          </button>
        </div>
      )}
    </div>
  );
}
