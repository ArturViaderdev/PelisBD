import { useMemo } from 'react';

export default function VideoList({ videos, title }) {
  const trailerVideo = useMemo(() => {
    return videos?.find(v => 
      (v.type === 'Trailer' || v.type === 'Teaser') && v.site === 'YouTube'
    );
  }, [videos]);

  const youtubeVideos = useMemo(() => {
    return videos?.filter(v => v.site === 'YouTube') || [];
  }, [videos]);

  return (
    <>
      {trailerVideo && (
        <div className="mt-6">
          <h3 className="text-xl font-semibold text-white mb-2">Tráiler</h3>
          <div className="relative pt-[56.25%] h-0 overflow-hidden rounded-lg shadow-lg">
            <iframe
              className="absolute top-0 left-0 w-full h-full"
              src={`https://www.youtube.com/embed/${trailerVideo.key}`}
              title={trailerVideo.name}
              frameBorder="0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
            ></iframe>
          </div>
        </div>
      )}

      {youtubeVideos.length > 0 && (
        <div className="mt-6">
          <h3 className="text-xl font-semibold text-white mb-2">Otros videos</h3>
          <ul className="space-y-2">
            {youtubeVideos.map((video) => (
              <li key={video.id} className="flex items-center gap-2">
                <a
                  href={`https://www.youtube.com/watch?v=${video.key}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-blue-400 hover:underline text-sm"
                >
                  {video.name || 'Video'} ({video.type})
                </a>
              </li>
            ))}
          </ul>
        </div>
      )}
    </>
  );
}
