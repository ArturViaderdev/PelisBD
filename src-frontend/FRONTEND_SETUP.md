# 🎬 PelisBD - Frontend Setup Completado

## ✅ Proyec to Creado

He creado un frontend completo para tu aplicación de películas y series usando:

- **React 18** con Vite (bundler rápido)
- **React Router** para navegación
- **Zustand** para estado global
- **Axios** para peticiones HTTP
- **Tailwind CSS** para estilos
- **React Icons** para iconos

## 📁 Estructura del Proyecto

```
frontend/
├── src/
│   ├── components/          # Componentes reutilizables
│   │   ├── Header.jsx       # Navegación y búsqueda
│   │   ├── Footer.jsx       # Pie de página
│   │   ├── MovieCard.jsx    # Tarjeta de película/serie
│   │   ├── RatingStars.jsx  # Sistema de calificación
│   │   ├── CommentSection.jsx
│   │   ├── EpisodeTracker.jsx  # Rastreador de episodios
│   │   ├── FilterBar.jsx    # Filtros y ordenación
│   │   └── CategoryList.jsx # Categorías
│   ├── pages/               # Páginas de la aplicación
│   │   ├── Home.jsx         # Inicio
│   │   ├── Login.jsx        # Inicio de sesión
│   │   ├── Register.jsx     # Registro
│   │   ├── Movies.jsx       # Lista de películas
│   │   ├── TV.jsx           # Lista de series
│   │   ├── MovieDetail.jsx  # Detalle de película
│   │   ├── TVDetail.jsx     # Detalle de serie
│   │   ├── SearchResults.jsx
│   │   ├── Watchlist.jsx    # Mi lista
│   │   └── Watched.jsx      # Visto
│   ├── services/
│   │   └── api.js           # Llamadas a la API
│   ├── stores/              # Estado global (Zustand)
│   │   ├── authStore.js
│   │   └── moviesStore.js
│   ├── utils/
│   │   └── api.js           # Configuración de Axios
│   ├── App.jsx              # Enrutamiento
│   ├── main.jsx             # Punto de entrada
│   └── index.css            # Estilos globales
├── index.html
├── vite.config.js
├── tailwind.config.js
├── postcss.config.js
└── package.json
```

## 🚀 Cómo Usar

### 1. Navega al directorio frontend:
```bash
cd /home/ryzenpc/Documentos/cybernarium/webpelisperp/pelisbd/frontend
```

### 2. Instala las dependencias (ya está hecho):
```bash
npm install
```

### 3. Inicia el servidor de desarrollo:
```bash
npm run dev
```

El frontend estará disponible en: **http://localhost:5173**

### 4. Construir para producción:
```bash
npm run build
```

## 📋 Funcionalidades Implementadas

✅ **Autenticación**
- Registro de usuarios
- Inicio de sesión
- Logout
- Persistencia de sesión en localStorage

✅ **Exploración de Contenido**
- Películas y series populares
- Contenido en tendencia
- Búsqueda por nombre
- Categorías principales (19 géneros)
- Paginación

✅ **Gestión Personal**
- Marcar como visto
- Agregar a mi lista
- Mi lista de películas/series
- Lista de películas vistas

✅ **Calificación y Comentarios**
- Calificación con estrellas (1-5)
- Comentarios públicos y privados
- Sistema de foro
- Editar y eliminar comentarios propios

✅ **Series**
- Rastreador de episodios
- Marcar temporadas completas como vistas
- Marcar episodios individuales

✅ **Filtros y Ordenación**
- Filtrar por estado (todo, vistos, mi lista, calificados)
- Ordenar por popularidad, calificación, recientes, título

## 🔌 Integración con tu API

El frontend se comunica con tu API en el puerto **3000** usando estos endpoints:

### Autenticación
```
POST /api/auth/register
POST /api/auth/login
```

### Películas y Series
```
GET /api/movies/popular
GET /api/movies/trending
GET /api/movies/search
GET /api/movies/:id

GET /api/tv/popular
GET /api/tv/trending
GET /api/tv/search
GET /api/tv/:id
```

### Usuario
```
GET /api/user/watched
POST /api/user/watched
DELETE /api/user/watched/:type/:id

GET /api/user/watchlist
POST /api/user/watchlist
DELETE /api/user/watchlist/:type/:id

POST /api/user/tv/:id/episode
POST /api/user/tv/:id/season
```

### Calificaciones y Comentarios
```
POST /api/reviews/:type/:id/rate
GET /api/reviews/:type/:id/ratings

POST /api/reviews/:type/:id/comments
GET /api/reviews/:type/:id/comments
DELETE /api/reviews/comments/:id
PUT /api/reviews/comments/:id
```

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Uso |
|-----------|---------|-----|
| React | 18 | Framework UI |
| Vite | 8 | Build tool y dev server |
| React Router | 6 | Enrutamiento |
| Zustand | - | Gestión de estado |
| Axios | - | Cliente HTTP |
| Tailwind CSS | 3 | Estilos |
| React Icons | - | Iconos |

## 🎨 Características de Diseño

- 🌙 Tema oscuro predeterminado
- 📱 Diseño responsive (móvil, tablet, desktop)
- ⚡ Carga lazy de imágenes
- 🎭 Animaciones y transiciones suaves
- ♿ Accesible con semántica HTML correcta

## 📝 Variables de Entorno

Crea un archivo `.env.local`:

```env
VITE_API_URL=http://localhost:3000/api
```

## 🚦 Rutas Disponibles

| Ruta | Descripción | Autenticación |
|------|-----------|-----------------|
| `/` | Inicio | - |
| `/movies` | Películas populares | - |
| `/tv` | Series populares | - |
| `/movie/:id` | Detalle de película | - |
| `/tv/:id` | Detalle de serie | - |
| `/search?q=query` | Búsqueda | - |
| `/watchlist` | Mi lista | ✅ Requerida |
| `/watched` | Películas vistas | ✅ Requerida |
| `/login` | Inicio de sesión | - |
| `/register` | Registro | - |

## 💡 Próximos Pasos

1. **Desarrolla tu API** en el puerto 3000
2. **Implementa los endpoints** que consume el frontend
3. **Prueba la autenticación** primero
4. **Luego implementa** la búsqueda y exploración
5. **Finalmente** calificaciones y comentarios

## 🔗 Integración con TMDB

El frontend espera que tu API obtenga los datos de TMDB. La API debe:

1. Obtener datos de TMDB
2. Combinar con datos del usuario (watchlist, ratings, comments)
3. Retornar JSON formateado

Las imágenes se cargan directamente desde TMDB CDN:
```
https://image.tmdb.org/t/p/w342{poster_path}
https://image.tmdb.org/t/p/w1280{backdrop_path}
```

## 📚 Recursos Útiles

- [React Docs](https://react.dev)
- [React Router Docs](https://reactrouter.com)
- [Zustand Docs](https://github.com/pmndrs/zustand)
- [Tailwind CSS Docs](https://tailwindcss.com/docs)
- [TMDB API Docs](https://developer.themoviedb.org/docs)

## ✨ Notas Importantes

- El frontend usa JWT tokens almacenados en localStorage
- La autenticación persiste entre sesiones
- Los comentarios privados solo los ve el autor
- Los comentarios públicos se ven en el foro de la película/serie
- Las series pueden rastrear episodios individuales

## 🎯 El Frontend Está Listo

Todo el código está implementado y listo para conectarse con tu API.
El servidor de desarrollo está configurado y puede iniciarse con `npm run dev`.

¡Ahora solo necesitas desarrollar el backend en Node.js/Express! 🚀
