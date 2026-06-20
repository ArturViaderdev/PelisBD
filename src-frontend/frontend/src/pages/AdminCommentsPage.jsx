import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { adminCommentsService, moviesService, tvService } from '../services/api';

export default function AdminCommentsPage() {
  const [comments, setComments] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(20);
  const [totalPages, setTotalPages] = useState(0);
  const [type, setType] = useState('');
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);

  const fetchComments = async () => {
    try {
      setLoading(true);
      const res = await adminCommentsService.getComments(page, size, type, search);
      setComments(res.data.content || []);
      setTotalPages(res.data.totalPages || 0);
    } catch (err) {
      console.error('Error loading admin comments:', err);
      setComments([]);
      setTotalPages(0);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchComments();
  }, [page, type]);

  const handleSearch = (e) => {
    e.preventDefault();
    setPage(0);
    fetchComments();
  };

  const handleDelete = async (commentId) => {
    if (!confirm('¿Eliminar este comentario?')) return;
    try {
      await adminCommentsService.deleteComment(commentId);
      setComments(prev => prev.filter(c => c.id !== commentId));
    } catch (err) {
      console.error('Error deleting comment:', err);
    }
  };

  const getContentLink = (comment) => {
    return comment.itemType === 'movie'
      ? `/movie/${comment.itemId}`
      : `/tv/${comment.itemId}`;
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 space-y-6">
      <h1 className="text-3xl font-bold">Administración de comentarios</h1>

      <form onSubmit={handleSearch} className="flex flex-wrap gap-3">
        <select
          value={type}
          onChange={(e) => {
            setType(e.target.value);
            setPage(0);
          }}
          className="input-field"
        >
          <option value="">Todos los tipos</option>
          <option value="MOVIE">Películas</option>
          <option value="TV">Series</option>
        </select>

        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Buscar texto..."
          className="input-field flex-1 min-w-64"
        />

        <button type="submit" className="btn-primary">
          Buscar
        </button>
      </form>

      {loading ? (
        <p className="text-gray-400">Cargando comentarios...</p>
      ) : (
        <div className="space-y-4">
          {comments.map((comment) => (
            <div key={comment.id} className="bg-gray-900 border border-gray-800 rounded-lg p-4 space-y-3">
              <div className="flex items-center justify-between gap-4">
                <div>
                  <p className="font-semibold text-white">{comment.userName}</p>
                  <p className="text-sm text-gray-400">
                    {comment.itemType} · ID {comment.itemId} · {comment.isPublic ? 'Público' : 'Privado'}
                  </p>
                </div>

                <div className="flex gap-2">
                  <Link
                    to={getContentLink(comment)}
                    className="text-primary hover:underline text-sm"
                  >
                    Ver contenido
                  </Link>
                  <button
                    onClick={() => handleDelete(comment.id)}
                    className="text-red-400 hover:text-red-300 text-sm"
                  >
                    Eliminar
                  </button>
                </div>
              </div>

              <p className="text-gray-200 whitespace-pre-wrap">{comment.commentText}</p>

              <p className="text-xs text-gray-500">
                {new Date(comment.createdAt).toLocaleString('es-ES')}
              </p>
            </div>
          ))}
        </div>
      )}

      <div className="flex items-center justify-center gap-3 pt-4">
        <button
          className="btn-secondary disabled:opacity-50"
          disabled={page === 0}
          onClick={() => setPage(p => p - 1)}
        >
          Anterior
        </button>
        <span className="text-gray-400">
          Página {page + 1} de {totalPages || 1}
        </span>
        <button
          className="btn-secondary disabled:opacity-50"
          disabled={page + 1 >= totalPages}
          onClick={() => setPage(p => p + 1)}
        >
          Siguiente
        </button>
      </div>
    </div>
  );
}
