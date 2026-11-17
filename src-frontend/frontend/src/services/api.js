import axios from 'axios';
import { expireSession, isTokenExpired } from '../utils/session';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
});

api.interceptors.request.use(
  (config) => {
    const apiKey = import.meta.env.VITE_API_KEY;
    if (apiKey) {
      config.headers = config.headers ?? {};
      config.headers['X-API-Key'] = apiKey;
    }

    const token = localStorage.getItem('token');
    if (token) {
      // Token caducado: no se envía la petición, se cierra sesión y se va al login.
      if (isTokenExpired(token)) {
        expireSession();
        return Promise.reject(new axios.CanceledError('Sesión caducada'));
      }

      config.headers = config.headers ?? {};
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Solo si la petición iba con token (usuario logueado) y el backend lo rechaza.
    // Las peticiones de usuarios no logueados no llevan token y no se ven afectadas.
    const sentToken = Boolean(error.config?.headers?.Authorization);
    const status = error.response?.status;
    const isLoginRequest = error.config?.url?.startsWith('/auth/');

    if (sentToken && !isLoginRequest && status === 401) {
      expireSession();
    }

    return Promise.reject(error);
  }
);

export default api;

export const authService = {
  register: (email, user, password) =>
    api.post('/auth/register', { email, password, userName: user }),
  login: (email, password) =>
    api.post('/auth/login', { email, password }),
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
  confirmEmail: (token) =>
    api.post(`/auth/confirmemail?token=${token}`),
};

export const moviesService = {
  getPopular: (page = 1) =>
    api.get(`/movies/popular?page=${page}`),
  getTrending: (page = 1, timeWindow = 'week') =>
    api.get(`/movies/trending?page=${page}&timeWindow=${timeWindow}`),
  getCategories: () =>
    api.get('/movies/categories'),
  searchMovies: (query, page = 1) =>
    api.get('/search/movie', {
      params: { query, page },
    }),
  getMovieDetail: (id) =>
    api.get(`/movies/${id}`),
  getMoviesByCategory: (category, page = 1) =>
    api.get(`/movies/category/${category}?page=${page}`),
};

export const tvService = {
  getPopular: (page = 1) =>
    api.get(`/tv/popular?page=${page}`),
  getTrending: (page = 1, timeWindow = 'week') =>
    api.get(`/tv/trending?page=${page}&timeWindow=${timeWindow}`),
  searchTV: (query, page = 1) =>
    api.get(`/search/tv?query=${query}&page=${page}`),
  getTVDetail: (id) =>
    api.get(`/tv/${id}`),
  getCategories: () =>
    api.get('/tv/categories'),
  getTVSeasonDetail: (tvId, seasonNumber) =>
    api.get(`/tv/${tvId}/season/${seasonNumber}`),
  getTVByCategory: (category, page = 1) =>
    api.get(`/tv/category/${category}?page=${page}`),
  getTVEpisode: (tvId, seasonNumber, episodeNumber) =>
    api.get(`/tv/${tvId}/season/${seasonNumber}/episode/${episodeNumber}`),
};

export const searchService = {
  search: (query, page = 1) =>
    api.get('/search/multi', {
      params: {
        query,
        page,
        include_adult: false,
      },
    }),
};

export const userService = {
  addToWatched: (type, itemId) =>
    api.post('/user/watched', { type, itemId }),
  removeFromWatched: (type, itemId) =>
    api.delete(`/user/watched/${type}/${itemId}`),
  getWatchedList: (page = 1, size = 12, sort = 'recent') =>
    api.get('/user/watched', {
      params: { page, size, sort },
    }),
  isMovieWatched: (type, tmdbId) =>
    api.get(`/user/watched/status/${type}/${tmdbId}`),
  addToWatchlist: (type, itemId) =>
    api.post('/user/watchlist', { type, itemId }),
  removeFromWatchlist: (type, itemId) =>
    api.delete(`/user/watchlist/${type}/${itemId}`),
  getWatchlist: (page = 0, size = 12, sort = 'recent') =>
    api.get('/user/watchlist', {
      params: { page, size, sort },
    }),
  isMovieInWatchList: (type, tmdbId) =>
    api.get(`/user/watchlist/status/${type}/${tmdbId}`),
  markEpisode: (tvId, season, episode, watched = true) =>
    api.post(`/user/tv/${tvId}/episode`, { season, episode, watched }),
  getGlobalWatchedList: (page = 1, size = 12, sort = 'dateadd') =>
    api.get('/watched', { params: { page, size, sort } }),
  markSeason: (tvId, season, watched = true) =>
    api.post(`/user/tv/${tvId}/season`, { season, watched }),
  getSeriesInProgress: () =>
    api.get('/user/series-in-progress'),
};

// Películas 3D: propuestas de usuarios aprobadas por el administrador
export const movies3dService = {
  // Público
  getApproved: (page = 1, size = 12) =>
    api.get('/movies3d', { params: { page, size } }),
  getApprovedIds: () => api.get('/movies3d/ids'),
  getStatus: (tmdbId) => api.get(`/movies3d/${tmdbId}/status`),
  // Usuario
  request: (tmdbId) => api.post(`/user/movies3d/${tmdbId}/request`),
  cancelRequest: (tmdbId) => api.delete(`/user/movies3d/${tmdbId}/request`),
  // Administrador
  getPendingRequests: () => api.get('/admin/movies3d/requests'),
  approve: (tmdbId) => api.post(`/admin/movies3d/${tmdbId}/approve`),
  reject: (tmdbId) => api.post(`/admin/movies3d/${tmdbId}/reject`),
  remove: (tmdbId) => api.delete(`/admin/movies3d/${tmdbId}`),
};

export const reviewService = {
  rateItem: (type, itemId, rating) =>
    api.post(`/reviews/${type}/${itemId}/rate`, { rating }),
  getItemRatings: (type, itemId) =>
    api.get(`/reviews/${type}/${itemId}/ratings`),
  addComment: (type, itemId, text, isPublic = false) =>
    api.post(`/reviews/${type}/${itemId}/comments`, { text, isPublic }),
  getComments: (type, itemId, onlyPublic = false) =>
    api.get(`/reviews/${type}/${itemId}/comments?onlyPublic=${onlyPublic}`),
  deleteComment: (commentId) =>
    api.delete(`/reviews/comments/${commentId}`),
  updateComment: (commentId, text) =>
    api.put(`/reviews/comments/${commentId}`, { text }),
};

export const adminCommentsService = {
  getComments: (page = 0, size = 20, type = '', search = '') =>
    api.get('/admin/comments', {
      params: { page, size, type, search },
    }),
  deleteComment: (commentId) =>
    api.delete(`/admin/comments/${commentId}`),
};

export const getCategoryList = async () => {
  try {
    const response = await api.get('/movies/categories');
    return response.data;
  } catch (error) {
    console.error('Error fetching categories:', error);
    return [];
  }
};

export const getTvCategories = async () => {
  try {
    const response = await api.get('/tv/categories');
    return Array.isArray(response.data) ? response.data : response.data.genres || [];
  } catch (error) {
    return [
      { id: 10759, name: 'Acción y Aventura' },
      { id: 16, name: 'Animación' },
      { id: 35, name: 'Comedia' },
      { id: 80, name: 'Crimen' },
      { id: 99, name: 'Documental' },
      { id: 18, name: 'Drama' },
      { id: 10751, name: 'Familia' },
      { id: 10765, name: 'Fantasía' },
      { id: 10762, name: 'Reality' },
      { id: 10763, name: 'Telenovela' },
      { id: 10764, name: 'Soap' },
      { id: 10766, name: 'Misterio' },
      { id: 10767, name: 'Romance' },
      { id: 10768, name: 'Sci-Fi' },
      { id: 10769, name: 'Guerra' },
      { id: 10770, name: 'Película para TV' },
    ];
  }
};
