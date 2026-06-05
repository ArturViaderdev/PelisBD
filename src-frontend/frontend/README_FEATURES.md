# PelisBD Frontend

Frontend moderna para la aplicación de películas y series. Hecha con React, Vite y Tailwind CSS.

## Características

✨ **Autenticación** - Registro e inicio de sesión de usuarios
📽️ **Exploración** - Navega películas y series populares
🔍 **Búsqueda** - Busca películas y series por nombre
⭐ **Calificación** - Califica películas y series con estrellas
💬 **Comentarios** - Comenta públicamente o privadamente
📋 **Listas** - Marca como visto o agrega a mi lista
📺 **Episodios** - Marca temporadas y episodios como vistos
🎬 **Categorías** - Explora por género
🔽 **Filtros** - Filtra y ordena contenido

## Requisitos previos

- Node.js 18+
- npm o yarn
- API en puerto 3000

## Instalación

1. **Instalar dependencias:**
```bash
npm install
```

2. **Configurar variables de entorno:**
```bash
cp .env.example .env.local
```

3. **Iniciar servidor de desarrollo:**
```bash
npm run dev
```

La aplicación estará disponible en `http://localhost:5173`

## Estructura del Proyecto

```
src/
├── components/       # Componentes reutilizables
├── pages/           # Páginas de la aplicación
├── services/        # Llamadas a la API
├── stores/          # Estado global (Zustand)
├── utils/           # Funciones auxiliares
├── index.css        # Estilos globales
├── App.jsx          # Componente raíz
└── main.jsx         # Punto de entrada
```

## Componentes principales

- **Header** - Navegación y búsqueda
- **MovieCard** - Tarjeta de película/serie
- **RatingStars** - Sistema de calificación
- **CommentSection** - Sección de comentarios
- **EpisodeTracker** - Rastreador de episodios
- **FilterBar** - Filtros y ordenación
- **CategoryList** - Lista de categorías

## Página principales

- **Home** - Página de inicio con contenido destacado
- **Movies** - Películas populares con paginación
- **TV** - Series populares con paginación
- **MovieDetail** - Detalles de película
- **TVDetail** - Detalles de serie
- **SearchResults** - Resultados de búsqueda
- **Watchlist** - Lista de películas para ver
- **Watched** - Lista de películas vistas
- **Login** - Inicio de sesión
- **Register** - Registro de usuario

## Rutas

- `/` - Inicio
- `/movies` - Películas
- `/tv` - Series
- `/movie/:id` - Detalle de película
- `/tv/:id` - Detalle de serie
- `/search?q=query` - Búsqueda
- `/watchlist` - Mi lista (privada)
- `/watched` - Visto (privada)
- `/login` - Inicio de sesión
- `/register` - Registro

## Scripts disponibles

- `npm run dev` - Inicia servidor de desarrollo
- `npm run build` - Construye para producción
- `npm run preview` - Visualiza construcción de producción
- `npm run lint` - Valida código

## Tecnologías

- **React 18** - Framework UI
- **Vite** - Build tool
- **React Router** - Enrutamiento
- **Zustand** - Gestor de estado
- **Axios** - Cliente HTTP
- **Tailwind CSS** - Estilos
- **React Icons** - Iconos

## Comunicación con API

El frontend se comunica con una API en `http://localhost:3000/api`.

### Endpoints esperados

#### Autenticación
- `POST /auth/register` - Registro
- `POST /auth/login` - Inicio de sesión

#### Películas
- `GET /movies/popular` - Películas populares
- `GET /movies/trending` - Películas en tendencia
- `GET /movies/search` - Buscar películas
- `GET /movies/:id` - Detalle de película

#### Series
- `GET /tv/popular` - Series populares
- `GET /tv/trending` - Series en tendencia
- `GET /tv/search` - Buscar series
- `GET /tv/:id` - Detalle de serie

#### Usuario
- `GET /user/watched` - Lista de vistos
- `GET /user/watchlist` - Mi lista
- `POST /user/watched` - Marcar como visto
- `POST /user/watchlist` - Agregar a lista
- `POST /user/tv/:id/episode` - Marcar episodio
- `POST /user/tv/:id/season` - Marcar temporada

#### Calificaciones y Comentarios
- `POST /reviews/:type/:id/rate` - Calificar
- `GET /reviews/:type/:id/ratings` - Obtener calificaciones
- `POST /reviews/:type/:id/comments` - Agregar comentario
- `GET /reviews/:type/:id/comments` - Obtener comentarios
- `DELETE /reviews/comments/:id` - Eliminar comentario

## Autenticación

La aplicación utiliza tokens JWT almacenados en `localStorage`. El token se envía en el header `Authorization: Bearer <token>` en cada petición.

## Notas de desarrollo

- El estado de autenticación se persiste en `localStorage`
- Las imágenes se cargan desde TMDB CDN
- Los comentarios pueden ser públicos o privados
- Solo usuarios autenticados pueden calificar y comentar
- Se soportan películas y series

## Licencia

MIT
