import { create } from 'zustand';

export const useMoviesStore = create((set) => ({
  movies: [],
  tvShows: [],
  currentPage: 1,
  hasMorePages: true,
  isLoading: false,
  searchQuery: '',
  selectedCategory: null,
  filterType: 'all', // all, watched, watchlist, rated
  sortBy: 'popularity', // popularity, rating, recent

  setMovies: (movies) => set({ movies }),
  setTVShows: (tvShows) => set({ tvShows }),
  setCurrentPage: (page) => set({ currentPage: page }),
  setHasMorePages: (hasMore) => set({ hasMorePages: hasMore }),
  setIsLoading: (isLoading) => set({ isLoading }),
  setSearchQuery: (query) => set({ searchQuery: query, currentPage: 1 }),
  setSelectedCategory: (category) => set({ selectedCategory: category, currentPage: 1 }),
  setFilterType: (type) => set({ filterType: type, currentPage: 1 }),
  setSortBy: (sort) => set({ sortBy: sort, currentPage: 1 }),

  addMovies: (newMovies) =>
    set((state) => ({
      movies: [...state.movies, ...newMovies],
    })),

  addTVShows: (newShows) =>
    set((state) => ({
      tvShows: [...state.tvShows, ...newShows],
    })),

  reset: () =>
    set({
      movies: [],
      tvShows: [],
      currentPage: 1,
      hasMorePages: true,
      isLoading: false,
    }),
}));
