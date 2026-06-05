# 🚀 Quick Start - PelisBD Frontend

## Instalación Rápida

### 1. Navega al directorio frontend:
```bash
cd frontend
```

### 2. Instala dependencias (si aún no lo has hecho):
```bash
npm install
```

### 3. Inicia el servidor de desarrollo:
```bash
npm run dev
```

### ✅ ¡Listo! Abre tu navegador en:
```
http://localhost:5173
```

## Comandos Principales

```bash
# Desarrollo
npm run dev

# Compilar para producción
npm run build

# Previsualizar build
npm run preview

# Linting
npm run lint
```

## Características Implementadas ✨

✅ Autenticación (login/register)
✅ Exploración de películas y series
✅ Búsqueda global
✅ Calificación con estrellas
✅ Comentarios públicos/privados
✅ Marcar como visto
✅ Mi lista (watchlist)
✅ Rastreador de episodios
✅ Filtros y ordenación
✅ Categorías por género
✅ Diseño responsive
✅ Tema oscuro

## Estructura de Carpetas

```
src/
├── components/    # 9 componentes reutilizables
├── pages/         # 10 páginas principales
├── services/      # Llamadas a API
├── stores/        # Estado global (Zustand)
└── utils/         # Configuración y utilidades
```

## Páginas Principales

- **Home** (/) - Inicio con contenido destacado
- **Películas** (/movies) - Lista de películas
- **Series** (/tv) - Lista de series
- **Detalle** (/movie/:id, /tv/:id) - Información completa
- **Búsqueda** (/search) - Resultados de búsqueda
- **Mi Lista** (/watchlist) - Películas para ver
- **Visto** (/watched) - Películas vistas
- **Login** (/login) - Iniciar sesión
- **Register** (/register) - Crear cuenta

## Puntos de Integración con API

Tu API (puerto 3000) debe proporcionar:

**Autenticación:**
- POST /api/auth/register
- POST /api/auth/login

**Contenido:**
- GET /api/movies/popular
- GET /api/tv/popular
- GET /api/movies/search
- GET /api/tv/search
- GET /api/movies/:id
- GET /api/tv/:id

**Usuario:**
- POST /api/user/watched
- GET /api/user/watchlist
- POST /api/reviews/:type/:id/rate
- POST /api/reviews/:type/:id/comments

Ver `FRONTEND_SETUP.md` para detalles completos de endpoints.

## Notas Técnicas

- **Framework**: React 18 + Vite
- **Estilos**: Tailwind CSS v3
- **Estado**: Zustand
- **HTTP**: Axios con interceptores
- **Enrutamiento**: React Router v6
- **Iconos**: React Icons

## Variables de Entorno

Crea `.env.local` (opcional):
```
VITE_API_URL=http://localhost:3000/api
```

## Troubleshooting

**Puerto 5173 en uso:**
```bash
npm run dev -- --port 3001
```

**Limpiar node_modules:**
```bash
rm -rf node_modules package-lock.json
npm install
```

**Build falla:**
```bash
npm run build
```

---

**El frontend está completamente funcional y listo para conectarse con tu backend.** 🎬✨
