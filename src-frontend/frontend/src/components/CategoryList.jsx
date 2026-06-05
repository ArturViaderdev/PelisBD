import { useNavigate } from 'react-router-dom';

export default function CategoryList({ categories = [], onSelectCategory, type = 'movie' }) {
  const navigate = useNavigate();

  const defaultCategories = [
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

  const categoryList = categories.length > 0 ? categories : defaultCategories;

  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-bold">Categorías</h2>
      <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-3">
        {categoryList.map((category) => (
          <button
            key={category.id}
            onClick={() => {
              onSelectCategory && onSelectCategory(category.id);
              navigate(`/${type}?category=${category.id}`);
            }}
            className="p-4 bg-gray-800 hover:bg-primary rounded-lg transition text-center font-medium hover:text-white"
          >
            {category.name}
          </button>
        ))}
      </div>
    </div>
  );
}
