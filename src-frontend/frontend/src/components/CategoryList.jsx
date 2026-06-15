// src/components/CategoryList.jsx

import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getCategoryList, getTvCategories } from '../services/api';

export default function CategoryList({ onSelectCategory, type = 'movie' }) {
  const navigate = useNavigate();
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const data = type === 'tv' 
          ? await getTvCategories() 
          : await getCategoryList();

        const categoriesArray = Array.isArray(data) ? data : data.genres || [];

        setCategories(categoriesArray);
      } catch (error) {
        console.error(`Error loading ${type} categories:`, error);
        const defaultCategories = type === 'tv'
          ? [
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
            ]
          : [
              { id: 28, name: 'Acción' },
              { id: 12, name: 'Aventura' },
              { id: 16, name: 'Animación' },
              { id: 35, name: 'Comedia' },
              { id: 80, name: 'Crimen' },
              { id: 99, name: 'Documental' },
              { id: 18, name: 'Drama' },
              { id: 10751, name: 'Familia' },
              { id: 14, name: 'Fantasía' },
              { id: 36, name: 'Historia' },
              { id: 27, name: 'Terror' },
              { id: 10402, name: 'Música' },
              { id: 9648, name: 'Misterio' },
              { id: 10749, name: 'Romance' },
              { id: 878, name: 'Ciencia Ficción' },
              { id: 10770, name: 'Película para TV' },
              { id: 53, name: 'Thriller' },
              { id: 10752, name: 'Guerra' },
              { id: 37, name: 'Western' },
            ];

        setCategories(defaultCategories);
      }
    };

    fetchCategories();
  }, [type]);

  const handleCategoryClick = (categoryId) => {
    onSelectCategory && onSelectCategory(categoryId);
    navigate(`/${type}/category/${categoryId}`);
  };

  // ✅ Añade un fallback si no hay categorías
  if (!Array.isArray(categories) || categories.length === 0) {
    return (
      <div className="space-y-4">
        <h2 className="text-2xl font-bold">
          {type === 'movie' ? 'Categorías de películas' : 'Categorías de series'}
        </h2>
        <p className="text-gray-400">Cargando categorías...</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-bold">
        {type === 'movie' ? 'Categorías de películas' : 'Categorías de series'}
      </h2>
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-3">
        {categories.map((category) => (
          <button
            key={category.id}
            onClick={() => handleCategoryClick(category.id)}
            className="p-4 bg-gray-800 hover:bg-primary rounded-lg transition text-center font-medium hover:text-white"
          >
            {category.name}
          </button>
        ))}
      </div>
    </div>
  );
}
