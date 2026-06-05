import { FiCheck } from 'react-icons/fi';
import { useState } from 'react';

export default function EpisodeTracker({ seasons = [], onMarkEpisode }) {
  const [expandedSeason, setExpandedSeason] = useState(null);

  return (
    <div className="space-y-4">
      <h3 className="text-xl font-bold">Episodios</h3>

      {seasons.map((season) => (
        <div key={season.season_number} className="bg-gray-900 rounded-lg overflow-hidden">
          <button
            onClick={() =>
              setExpandedSeason(
                expandedSeason === season.season_number ? null : season.season_number
              )
            }
            className="w-full flex items-center justify-between p-4 hover:bg-gray-800 transition"
          >
            <span className="font-semibold">
              Temporada {season.season_number}
            </span>
            <span className="text-sm text-gray-400">
              {season.episode_count} episodios
            </span>
          </button>

          {expandedSeason === season.season_number && (
            <div className="border-t border-gray-800 p-4 space-y-2 bg-gray-950">
              {season.episodes?.map((episode) => (
                <label
                  key={episode.episode_number}
                  className="flex items-center gap-3 p-2 hover:bg-gray-800 rounded cursor-pointer"
                >
                  <input
                    type="checkbox"
                    checked={episode.watched || false}
                    onChange={() =>
                      onMarkEpisode &&
                      onMarkEpisode(
                        season.season_number,
                        episode.episode_number,
                        !episode.watched
                      )
                    }
                    className="w-4 h-4 cursor-pointer"
                  />
                  <div className="flex-1">
                    <p className="font-medium">
                      E{episode.episode_number}: {episode.name}
                    </p>
                    {episode.air_date && (
                      <p className="text-xs text-gray-400">
                        {new Date(episode.air_date).toLocaleDateString('es-ES')}
                      </p>
                    )}
                  </div>
                  {episode.watched && (
                    <FiCheck className="text-green-400" />
                  )}
                </label>
              ))}
            </div>
          )}
        </div>
      ))}
    </div>
  );
}
