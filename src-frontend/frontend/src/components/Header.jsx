// src/components/Header.jsx
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { FiSearch, FiLogOut, FiUser } from 'react-icons/fi';
import { useAuthStore } from '../stores/authStore';
import { useMoviesStore } from '../stores/moviesStore';
import { useState, useEffect } from 'react';

export default function Header() {
  const { user, logout } = useAuthStore();
  const { setSearchQuery, reset } = useMoviesStore();
  const navigate = useNavigate();
  const location = useLocation();
  const [searchInput, setSearchInput] = useState('');
  const [searchType, setSearchType] = useState('both');

  useEffect(() => {
    const urlParams = new URLSearchParams(location.search);
    const query = urlParams.get('q') || '';
    const type = urlParams.get('type') || 'both';

    setSearchInput(query);
    setSearchType(type);

    if (location.pathname === '/search-movies') {
      setSearchType('movies');
    } else if (location.pathname === '/search-tv') {
      setSearchType('series');
    } else if (location.pathname === '/search') {
      setSearchType('both');
    }
  }, [location.search, location.pathname]);

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchInput.trim()) {
      setSearchQuery(searchInput);
      reset();

      const pathMap = {
        movies: '/search-movies',
        series: '/search-tv',
        both: '/search',
      };

      const path = pathMap[searchType] || '/search';
      navigate(`${path}?q=${encodeURIComponent(searchInput)}&type=${searchType}`);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="bg-gray-900 text-white shadow-lg sticky top-0 z-50">
    <div className="max-w-7xl mx-auto px-4 py-4">
    <div className="flex items-center justify-between gap-4 flex-wrap">
    <Link to="/" className="text-2xl font-bold text-primary hover:text-indigo-400">
    PelisBD
    </Link>

    <form onSubmit={handleSearch} className="flex-1 max-w-md">
    <div className="relative flex gap-2">
    <input
    type="text"
    placeholder="Buscar películas o series..."
    value={searchInput}
    onChange={(e) => setSearchInput(e.target.value)}
    className="flex-1 px-4 py-2 rounded-lg bg-gray-800 text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary"
    />

    <select
    value={searchType}
    onChange={(e) => setSearchType(e.target.value)}
    className="px-3 py-2 rounded-lg bg-gray-800 text-white border border-gray-600 focus:outline-none focus:ring-2 focus:ring-primary text-sm"
    >
    <option value="both">Ambos</option>
    <option value="movies">Películas</option>
    <option value="series">Series</option>
    </select>

    <button
    type="submit"
    className="absolute right-3 top-2.5 text-gray-400 hover:text-white"
    >
    <FiSearch size={20} />
    </button>
    </div>
    </form>

    <nav className="flex items-center gap-4">
    {user ? (
      <>
      <Link
      to="/watchlist"
      className="hover:text-primary transition-colors"
      >
      Mi Lista
      </Link>

      <Link
      to="/watched"
      className="hover:text-primary transition-colors"
      >
      Visto
      </Link>

      <Link
      to="/community-watched"
      className="hover:text-primary transition-colors"
      >
      Visto por la comunidad
      </Link>

      {user.role === 'ADMIN' && (
        <Link
        to="/admin/comments"
        className="hover:text-primary transition-colors"
        >
        Admin comentarios
        </Link>
      )}

      <div className="flex items-center gap-3">
      <FiUser className="text-gray-400" />
      <span className="text-sm">{user.name}</span>
      <button
      onClick={handleLogout}
      className="text-red-400 hover:text-red-300 flex items-center gap-1"
      >
      <FiLogOut /> Salir
      </button>
      </div>
      </>
    ) : (
      <>
      <Link to="/login" className="btn-outline text-sm">
      Iniciar sesión
      </Link>
      <Link to="/register" className="btn-primary text-sm">
      Registrarse
      </Link>
      </>
    )}
    </nav>
    </div>
    </div>
    </header>
  );
}
