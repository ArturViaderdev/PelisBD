import { useState } from 'react';
import { FiX, FiLock, FiGlobe } from 'react-icons/fi';

export default function CommentSection({
  comments = [],
  onAddComment,
  onDeleteComment,
  currentUserId,
  isLoading = false,
  onEditComment,
  isEditing = null,
  setIsEditing = null,
}) {
  const [newComment, setNewComment] = useState('');
  const [isPublic, setIsPublic] = useState(true);
  const [error, setError] = useState('');

  const canSendPrivateComment = () => {
    const now = new Date();
    const fiveMinutesAgo = new Date(now.getTime() - 5 * 60000);

    const recentPrivate = comments.filter(
      (c) => !c.public && new Date(c.createdAt) > fiveMinutesAgo
    );

    return recentPrivate.length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!newComment.trim()) {
      setError('El comentario no puede estar vacío');
      return;
    }

    if (newComment.length > 1000) {
      setError('El comentario no puede tener más de 1000 caracteres');
      return;
    }

    if (!isPublic && !canSendPrivateComment()) {
      setError('No puedes enviar más de un comentario privado en menos de 5 minutos');
      return;
    }

    try {
      await onAddComment(newComment, isPublic);
      setNewComment('');
      setIsPublic(true);
    } catch (err) {
      setError('Error al publicar el comentario. Inténtalo de nuevo.');
    }
  };

  const handleEdit = (commentId, text) => {
    if (!text || !text.trim()) {
      return;
    }
    if (onEditComment) {
      onEditComment(commentId, text.trim());
      if (setIsEditing) setIsEditing(null);
    }
  };

  const handleCancelEdit = () => {
    if (setIsEditing) setIsEditing(null);
  };

  const handleEditSave = (commentId) => {
    const textarea = document.querySelector(`[data-edit-id="${commentId}"] textarea`);
    const text = textarea?.value || '';
    handleEdit(commentId, text);
  };

  const renderComment = (comment) => {
    const isOwn = comment.userId === currentUserId;

    if (!comment.public && !isOwn) {
      return null;
    }

    return (
      <div
        key={comment.id}
        className="bg-gray-900 rounded-lg p-4 space-y-2 border border-gray-800"
      >
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <div className="flex items-center gap-2 mb-1">
              <h4 className="font-semibold text-white">{comment.userName}</h4>

              {!comment.public && isOwn && (
                <span className="text-xs bg-gray-700 px-2 py-1 rounded flex items-center gap-1">
                  <FiLock size={12} /> Privado
                </span>
              )}

              {comment.public && (
                <span className="text-xs bg-primary px-2 py-1 rounded flex items-center gap-1">
                  <FiGlobe size={12} /> Público
                </span>
              )}
            </div>
            <p className="text-xs text-gray-400">
              {new Date(comment.createdAt).toLocaleDateString('es-ES')}
            </p>
          </div>

          {isOwn && !isEditing && (
            <div className="flex gap-1">
              <button
                onClick={() => setIsEditing?.(comment.id)}
                className="text-gray-400 hover:text-blue-400 transition"
                title="Editar"
              >
                ✏️
              </button>
              <button
                onClick={() => onDeleteComment?.(comment.id)}
                className="text-gray-400 hover:text-red-400 transition"
                title="Eliminar"
              >
                <FiX />
              </button>
            </div>
          )}
        </div>

        {isEditing === comment.id ? (
          <div className="space-y-2" data-edit-id={comment.id}>
            <textarea
              defaultValue={comment.text}
              className="w-full p-2 border rounded bg-gray-800 text-white text-sm"
              rows="3"
              placeholder="Escribe tu nuevo comentario..."
              autoFocus
            />
            <div className="flex justify-end gap-2">
              <button
                onClick={handleCancelEdit}
                className="text-gray-400 hover:text-gray-200 text-sm"
              >
                Cancelar
              </button>
              <button
                onClick={() => handleEditSave(comment.id)}
                className="btn-primary text-sm px-3 py-1"
              >
                Guardar
              </button>
            </div>
          </div>
        ) : (
          <p className="text-gray-200 text-sm leading-relaxed">{comment.text}</p>
        )}
      </div>
    );
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
            className="input-field resize-none h-24 w-full"
            maxLength={1000}
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

            <div className="flex items-center gap-2">
              {error && (
                <span className="text-red-400 text-xs">{error}</span>
              )}
              <button
                type="submit"
                disabled={isLoading || !newComment.trim()}
                className="btn-primary text-sm disabled:opacity-50"
              >
                {isLoading ? 'Publicando...' : 'Comentar'}
              </button>
            </div>
          </div>
        </form>
      </div>

      <div className="space-y-4">
        {comments.length === 0 ? (
          <p className="text-gray-400 text-center py-8">
            No hay comentarios aún. ¡Sé el primero en comentar!
          </p>
        ) : (
          comments
            .filter(c => c.public || c.userId === currentUserId)
            .map(renderComment)
        )}
      </div>
    </div>
  );
}
