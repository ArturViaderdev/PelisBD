// src/utils/api.js
import axios from 'axios';

// Crear instancia de axios
const api = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080', // Usa variable de entorno
});

// Interceptor: Añadir API Key a todos los headers
api.interceptors.request.use((config) => {
  const apiKey = process.env.API_KEY;
  if (apiKey) {
    config.headers['X-API-Key'] = apiKey; // ✅ Añade la API Key
  }

  // También puedes añadir token si lo necesitas
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
}, (error) => {
  return Promise.reject(error);
});

export default api;

// Auth
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

// Movies
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

// TV Shows
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
  search: (query, page = 1) => {
    return api.get(`/search/multi`, {
      params: {
        query,
        page,
        include_adult: false,
      },
    });
  },
};

// User Lists
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
  markSeason: (tvId, season, watched = true) =>
    api.post(`/user/tv/${tvId}/season`, { season, watched }),
};

// Ratings & Reviews
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
