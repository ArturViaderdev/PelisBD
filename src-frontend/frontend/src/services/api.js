import api from '../utils/api';

// Auth
export const authService = {
  register: (email, password, user) =>
    api.post('/auth/register', { email, password, userName:user }),
  login: (email, password) =>
    api.post('/auth/login', { email, password }),
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
};

// Movies
export const moviesService = {
  getPopular: (page = 1) =>
    api.get(`/movies/popular?page=${page}`),
  getTrending: (timeWindow = 'week') =>
    api.get(`/movies/trending?timeWindow=${timeWindow}`),
  getCategories: () =>
    api.get('/movies/categories'),
  searchMovies: (query, page = 1) =>
    api.get('/movies/search', {
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
  api.get('/movies/popular', {
    params: { page },
  }),
  getTrending: (timeWindow = 'week') =>
    api.get(`/tv/trending?timeWindow=${timeWindow}`),
  searchTV: (query, page = 1) =>
    api.get(`/tv/search?query=${query}&page=${page}`),
  getTVDetail: (id) =>
    api.get(`/tv/${id}`),
  getTVByCategory: (category, page = 1) =>
    api.get(`/tv/category/${category}?page=${page}`),
};

// User Lists
export const userService = {
  addToWatched: (type, itemId) =>
    api.post('/user/watched', { type, itemId }),
  removeFromWatched: (type, itemId) =>
    api.delete(`/user/watched/${type}/${itemId}`),
  getWatchedList: () =>
    api.get('/user/watched'),

  addToWatchlist: (type, itemId) =>
    api.post('/user/watchlist', { type, itemId }),
  removeFromWatchlist: (type, itemId) =>
    api.delete(`/user/watchlist/${type}/${itemId}`),
  getWatchlist: () =>
    api.get('/user/watchlist'),

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
