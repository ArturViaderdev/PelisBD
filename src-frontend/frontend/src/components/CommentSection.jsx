import { useState } from 'react';
import { FiX, FiLock, FiGlobe } from 'react-icons/fi';

export default function CommentSection({
  comments = [],
  onAddComment,
  onDeleteComment,
  currentUserId,
  isLoading = false,
}) {
  const [newComment, setNewComment] = useState('');
  const [isPublic, setIsPublic] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (newComment.trim() && onAddComment) {
      await onAddComment(newComment, isPublic);
      setNewComment('');
      setIsPublic(false);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h3 className="text-xl font-bold mb-4 flex items-center gap-2">
          Comentarios ({comments.length})
        </h3>

        <form onSubmit={handleSubmit} className="mb-6 space-y-3">
          <textarea
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            placeholder="Escribe un comentario..."
            className="input-field resize-none h-24"
          />

          <div className="flex items-center justify-between">
            <label className="flex items-center gap-2 cursor-pointer">
              <input
                type="checkbox"
                checked={isPublic}
                onChange={(e) => setIsPublic(e.target.checked)}
                className="w-4 h-4"
              />
              <div className="flex items-center gap-1 text-sm">
                {isPublic ? (
                  <>
                    <FiGlobe /> Público
                  </>
                ) : (
                  <>
                    <FiLock /> Privado
                  </>
                )}
              </div>
            </label>
            <button
              type="submit"
              disabled={isLoading || !newComment.trim()}
              className="btn-primary text-sm disabled:opacity-50"
            >
              {isLoading ? 'Publicando...' : 'Comentar'}
            </button>
          </div>
        </form>
      </div>

      <div className="space-y-4">
        {comments.length === 0 ? (
          <p className="text-gray-400 text-center py-8">
            No hay comentarios aún. ¡Sé el primero en comentar!
          </p>
        ) : (
          comments.map((comment) => (
            <div
              key={comment.id}
              className="bg-gray-900 rounded-lg p-4 space-y-2 border border-gray-800"
            >
              <div className="flex items-start justify-between">
                <div>
                  <div className="flex items-center gap-2">
                    <h4 className="font-semibold">{comment.userName}</h4>
                    {comment.isPublic && (
                      <span className="text-xs bg-primary px-2 py-1 rounded flex items-center gap-1">
                        <FiGlobe size={12} /> Público
                      </span>
                    )}
                  </div>
                  <p className="text-xs text-gray-400">
                    {new Date(comment.createdAt).toLocaleDateString('es-ES')}
                  </p>
                </div>
                {currentUserId === comment.userId && (
                  <button
                    onClick={() => onDeleteComment && onDeleteComment(comment.id)}
                    className="text-gray-400 hover:text-red-400 transition"
                  >
                    <FiX />
                  </button>
                )}
              </div>
              <p className="text-gray-200">{comment.text}</p>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
