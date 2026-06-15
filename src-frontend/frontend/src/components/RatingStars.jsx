import { FiStar } from 'react-icons/fi';
import { useState } from 'react';

export default function RatingStars({ rating = 0, onRate, readOnly = false }) {
  const [hoverRating, setHoverRating] = useState(0);

  return (
    <div className="flex items-center gap-2">
      <div className="flex gap-1">
        {[1, 2, 3, 4, 5].map((star) => (
          <button
            key={star}
            onClick={() => !readOnly && onRate && onRate(star)}
            onMouseEnter={() => !readOnly && setHoverRating(star)}
            onMouseLeave={() => setHoverRating(0)}
            disabled={readOnly}
            className={`transition ${readOnly ? 'cursor-default' : 'cursor-pointer hover:scale-110'}`}
          >
            <FiStar
              size={24}
              className={`transition ${
                star <= (hoverRating || rating)
                  ? 'fill-yellow-400 text-yellow-400'
                  : 'text-gray-400'
              }`}
            />
          </button>
        ))}
      </div>
      {rating > 0 && (
        <span className="text-sm font-semibold">
          {rating}/5
        </span>
      )}
    </div>
  );
}
