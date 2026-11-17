import { useState } from 'react';
import { useNavigate, Link, useSearchParams } from 'react-router-dom';
import { authService } from '../services/api';
import { useAuthStore } from '../stores/authStore';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const { setUser } = useAuthStore();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const sessionExpired = searchParams.get('expired') === '1';

  // Página a la que volver tras iniciar sesión (solo rutas internas).
  const redirectParam = searchParams.get('redirect') || '/';
  const redirectTo =
    redirectParam.startsWith('/') && !redirectParam.startsWith('//')
      ? redirectParam
      : '/';

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const response = await authService.login(email, password);
      const data = response.data;

      const user = {
        id: data.id,
        email: data.email,
        userName: data.userName,
        role: data.role,
      };

      setUser(user, data.token);
      navigate(redirectTo, { replace: true });
    } catch (err) {
      setError(
        err.response?.data?.message ||
        'Error al iniciar sesión'
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
    <div className="card w-full max-w-md text-black">
    <h2 className="text-3xl font-bold text-center mb-8">Iniciar Sesión</h2>

    {sessionExpired && !error && (
      <div className="mb-4 p-4 bg-yellow-100 border border-yellow-300 rounded-lg text-yellow-800">
      Tu sesión ha caducado. Vuelve a iniciar sesión.
      </div>
    )}

    {error && (
      <div className="mb-4 p-4 bg-red-100 border border-red-300 rounded-lg text-red-800">
      {error}
      </div>
    )}

    <form onSubmit={handleSubmit} className="space-y-4">
    <div>
    <label className="block text-sm font-semibold mb-2 text-black">
    Email
    </label>
    <input
    type="email"
    value={email}
    onChange={(e) => setEmail(e.target.value)}
    className="input-field"
    required
    />
    </div>

    <div>
    <label className="block text-sm font-semibold mb-2 text-black">
    Contraseña
    </label>
    <input
    type="password"
    value={password}
    onChange={(e) => setPassword(e.target.value)}
    className="input-field"
    required
    />
    </div>

    <button
    type="submit"
    disabled={isLoading}
    className="w-full btn-primary disabled:opacity-50"
    >
    {isLoading ? 'Iniciando sesión...' : 'Iniciar Sesión'}
    </button>
    </form>

    <p className="text-center mt-6 text-gray-600">
    ¿No tienes cuenta?{' '}
    <Link to="/register" className="text-primary hover:text-indigo-600">
    Regístrate aquí
    </Link>
    </p>
    </div>
    </div>
  );
}
