import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from './stores/authStore';
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

// Componente de ruta privada
function PrivateRoute({ children }) {
  const { isAuthenticated } = useAuthStore();
  return isAuthenticated ? children : <Navigate to="/login" />;
}

function App() {
  return (
    <Router>
      <Header />
      <main className="flex-1 bg-gray-950 text-white">
        <Routes>
          {/* Rutas públicas */}
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
          <Route path="/tv/:id/season/:seasonNumber/episode/:episodeNumber" element={<EpisodeDetail />}
        
          
/>
          {/* Rutas privadas */}
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

          {/* ✅ Nueva ruta: Detalle de temporada */}
          <Route path="/tv/:id/season/:seasonNumber" element={<SeasonDetail />} />
        </Routes>
      </main>
      <Footer />
    </Router>
  );
}

export default App;
