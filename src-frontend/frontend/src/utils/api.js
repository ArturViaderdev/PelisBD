import axios from 'axios';

const API_BASE_URL = 'http://localhost:3000/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

const publicPaths = [
  '/movies/search',
  '/movies/popular',
  '/movies/trending',
  '/movies/categories',
  '/tv/search',
  '/tv/popular',
  '/tv/trending',
];

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  const isPublicRoute = publicPaths.some((path) =>
    config.url?.startsWith(path)
  );

  if (token && !isPublicRoute) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const isPublicRoute = publicPaths.some((path) =>
      error.config?.url?.startsWith(path)
    );

    if (error.response?.status === 401 && !isPublicRoute) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }

    return Promise.reject(error);
  }
);

export default api;
