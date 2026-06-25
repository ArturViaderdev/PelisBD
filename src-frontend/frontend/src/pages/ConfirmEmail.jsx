import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { authService } from '../services/api';
import { Alert } from '../components/Alert';

export default function ConfirmEmail() {
  const [searchParams] = useSearchParams();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  const token = searchParams.get('token');

  useEffect(() => {
    if (!token) {
      setError('Token inválido o faltante');
      setLoading(false);
      return;
    }

    const confirmEmail = async () => {
      try {
        await authService.confirmEmail(token);
        setSuccess(true);
      } catch (err) {
        setError(err.response?.data?.message || 'Error al confirmar el correo');
      } finally {
        setLoading(false);
      }
    };

    confirmEmail();
  }, [token]);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-900">
        <div className="text-center">
          <div className="inline-block animate-spin rounded-full h-12 w-12 border-4 border-blue-500 border-t-transparent"></div>
          <p className="text-white mt-4">Confirmando tu correo...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-900 px-4">
      <div className="bg-gray-800 rounded-lg shadow-xl p-8 max-w-md w-full text-center">
        {success ? (
          <>
            <div className="text-green-500 text-6xl mb-4">✅</div>
            <h2 className="text-2xl font-bold text-white mb-4">¡Correo confirmado!</h2>
            <p className="text-gray-300 mb-6">
              Tu cuenta ha sido activada correctamente.
            </p>
            <button
              onClick={() => navigate('/login')}
              className="btn-primary px-6 py-2 rounded-lg text-white font-medium"
            >
              Ir al inicio de sesión
            </button>
          </>
        ) : (
          <>
            <div className="text-red-500 text-6xl mb-4">❌</div>
            <h2 className="text-2xl font-bold text-white mb-4">Error</h2>
            <p className="text-red-400 mb-6">{error}</p>
            <button
              onClick={() => navigate('/')}
              className="btn-primary px-6 py-2 rounded-lg text-white font-medium"
            >
              Volver al inicio
            </button>
          </>
        )}
      </div>
    </div>
  );
}
