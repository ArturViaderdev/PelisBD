import { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from './stores/authStore';
import { expireSession, isStoredSessionExpired } from './utils/session';
import Header from './components/Header';
import Footer from './components/Footer';

// Páginas
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Movies from './pages/Movies';
import TV from './pages/TV';
import MovieDetail from './pages/MovieDetail';
import TVDetail from './pages/TVDetail';
import SearchResults from './pages/SearchResults';
import Watchlist from './pages/Watchlist';
import Watched from './pages/Watched';
import TrendingMovies from './pages/TrendingMovies';
import TrendingTV from './pages/TrendingTV';
import CategoryMovies from './pages/CategoryMovies';
import CategoryTV from './pages/CategoryTV';
import SeasonDetail from './pages/SeasonDetail';
import EpisodeDetail from './pages/EpisodeDetail';
import SearchResultsTV from './pages/SearchResultsTV';
import SearchPage from './pages/SearchPage';
import ConfirmEmail from './pages/ConfirmEmail';
import AdminRoute from './routes/AdminRoute';
import Contacto from './pages/Contacto';
import AdminCommentsPage from './pages/AdminCommentsPage';
import GlobalWatched from './pages/GlobalWatched';
import SeriesInProgress from './pages/SeriesInProgress';
import Movies3D from './pages/Movies3D';
import AdminMovies3DPage from './pages/AdminMovies3DPage';
import { useMovies3dStore } from './stores/movies3dStore';

function PrivateRoute({ children }) {
  const { isAuthenticated } = useAuthStore();
  return isAuthenticated ? children : <Navigate to="/login" replace />;
}

// Comprueba si el token ha caducado al abrir la app, al volver a la pestaña
// y cada minuto. Si ha caducado, cierra sesión y redirige al login.
// Sin sesión iniciada no hace nada.
function SessionWatcher() {
  useEffect(() => {
    const check = () => {
      if (isStoredSessionExpired()) expireSession();
    };

    check();
    const interval = setInterval(check, 60 * 1000);
    document.addEventListener('visibilitychange', check);

    return () => {
      clearInterval(interval);
      document.removeEventListener('visibilitychange', check);
    };
  }, []);

  return null;
}

// Carga una vez la lista de películas 3D aprobadas (para la etiqueta "3D")
function Movies3DLoader() {
  const load = useMovies3dStore((s) => s.load);
  useEffect(() => {
    load();
  }, [load]);
  return null;
}

function App() {
  return (
    <Router basename="/">
      <SessionWatcher />
      <Movies3DLoader />
      <Header />
      <main className="flex-1 bg-gray-950 text-white">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/movies" element={<Movies />} />
          <Route path="/tv" element={<TV />} />
          <Route path="/movie/:id" element={<MovieDetail />} />
          <Route path="/tv/:id" element={<TVDetail />} />
          <Route path="/search-movies" element={<SearchResults />} />
          <Route path="/movies/trending" element={<TrendingMovies />} />
          <Route path="/tv/trending" element={<TrendingTV />} />
          <Route path="/movie/category/:id" element={<CategoryMovies />} />
          <Route path="/tv/category/:id" element={<CategoryTV />} />
          <Route path="/search-tv" element={<SearchResultsTV />} />
          <Route path="/search" element={<SearchPage />} />
          <Route path="/confirm-email" element={<ConfirmEmail />} />
          <Route path="/community-watched" element={<GlobalWatched />} />
          <Route path="/movies-3d" element={<Movies3D />} />
	  <Route path="/contacto" element={<Contacto />} />
          <Route path="/tv/:id/season/:seasonNumber" element={<SeasonDetail />} />
          <Route
            path="/tv/:id/season/:seasonNumber/episode/:episodeNumber"
            element={<EpisodeDetail />}
          />

          <Route
            path="/watchlist"
            element={
              <PrivateRoute>
                <Watchlist />
              </PrivateRoute>
            }
          />
          <Route
            path="/watched"
            element={
              <PrivateRoute>
                <Watched />
              </PrivateRoute>
            }
          />
          <Route
            path="/series-in-progress"
            element={
              <PrivateRoute>
                <SeriesInProgress />
              </PrivateRoute>
            }
          />

          <Route element={<AdminRoute />}>
            <Route path="/admin/comments" element={<AdminCommentsPage />} />
            <Route path="/admin/movies3d" element={<AdminMovies3DPage />} />
          </Route>
        </Routes>
      </main>
      <Footer />
    </Router>
  );
}

export default App;
