import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../services/api';

export default function Register() {
  const [email, setEmail] = useState('');
  const [userName, setUserName] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccessMsg('');
    setIsLoading(true);

    try {
      await authService.register(email, userName, password);
      setSuccessMsg(
        'Registro completado. Revisa tu correo para confirmar la cuenta.'
      );
      // Opcional:
      // setTimeout(() => navigate('/login'), 2000);
    } catch (err) {
      setError(
        err.response?.data?.message ||
        'Error al registrarse'
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-4">
    <div className="card w-full max-w-md text-black">
    <h2 className="text-3xl font-bold text-center mb-8">Registrarse</h2>

    {error && (
      <div className="mb-4 p-4 bg-red-100 border border-red-300 rounded-lg text-red-800">
      {error}
      </div>
    )}

    {successMsg && (
      <div className="mb-4 p-4 bg-green-100 border border-green-300 rounded-lg text-green-800">
      {successMsg}
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
    Nombre de usuario
    </label>
    <input
    type="text"
    value={userName}
    onChange={(e) => setUserName(e.target.value)}
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
    {isLoading ? 'Creando cuenta...' : 'Registrarse'}
    </button>
    </form>

    <p className="text-center mt-6 text-gray-600">
    ¿Ya tienes cuenta?{' '}
    <Link to="/login" className="text-primary hover:text-indigo-600">
    Inicia sesión aquí
    </Link>
    </p>
    </div>
    </div>
  );
}
