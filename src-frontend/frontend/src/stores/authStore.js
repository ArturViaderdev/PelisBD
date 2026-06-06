import { create } from 'zustand';

const getStoredUser = () => {
  try {
    const rawUser = localStorage.getItem('user');
    return rawUser ? JSON.parse(rawUser) : null;
  } catch {
    localStorage.removeItem('user');
    return null;
  }
};

const getStoredToken = () => {
  return localStorage.getItem('token') || null;
};

export const useAuthStore = create((set) => {
  const user = getStoredUser();
  const token = getStoredToken();

  return {
    user,
    token,
    isAuthenticated: !!token,
    isLoading: false,
    error: null,

    setUser: (user, token) => {
      localStorage.setItem('user', JSON.stringify(user));
      localStorage.setItem('token', token);
      set({ user, token, isAuthenticated: true, error: null });
    },

    logout: () => {
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      set({ user: null, token: null, isAuthenticated: false, error: null });
    },

    setLoading: (isLoading) => set({ isLoading }),
                                   setError: (error) => set({ error }),
                                   clearError: () => set({ error: null }),
  };
});
