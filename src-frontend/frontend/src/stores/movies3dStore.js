import { create } from 'zustand';
import { movies3dService } from '../services/api';

// Ids de TMDB de las películas aprobadas como 3D. Se cargan una vez al abrir
// la app y sirven para poner la etiqueta "3D" en las tarjetas de cualquier listado.
export const useMovies3dStore = create((set, get) => ({
  ids: new Set(),
  loaded: false,

  load: async () => {
    try {
      const res = await movies3dService.getApprovedIds();
      const list = Array.isArray(res.data) ? res.data : [];
      set({ ids: new Set(list.map(Number)), loaded: true });
    } catch (err) {
      console.error('Error loading 3D movies:', err);
      set({ loaded: true });
    }
  },

  add: (tmdbId) => {
    const ids = new Set(get().ids);
    ids.add(Number(tmdbId));
    set({ ids });
  },

  remove: (tmdbId) => {
    const ids = new Set(get().ids);
    ids.delete(Number(tmdbId));
    set({ ids });
  },
}));
