# PelisBD

**PelisBD** es una aplicación web para llevar el control de películas y series vistas, pendientes y comentadas. Permite marcar películas y series como vistas o pendientes, puntuar y comentar títulos, y en el caso de las series, llevar el seguimiento por temporada y episodio.

La aplicación está formada por:

- **Backend** propio en Java + Spring Boot.
- **Frontend** en Node.js, generado inicialmente con ayuda de IA y posteriormente modificado manualmente.

## Características

- Lista de películas pendientes.
- Lista de películas vistas.
- Valoración de películas y series.
- Comentarios en películas y series.
- Seguimiento de temporadas y episodios vistos en series.
- Integración con TMDB para catálogo y metadatos.
- Sistema de autenticación con JWT.
- Envío de correo para confirmación de cuenta.

## Tecnologías

- Java 25
- Spring Boot
- Spring Security
- PostgreSQL
- Docker / Docker Compose
- Node.js
- Vite
- Nginx
- TMDB API

## Repositorio

```bash
git clone https://github.com/ArturViaderdev/PelisBD.git
cd PelisBD
```

---

## Configuración del backend

En el backend se utiliza un archivo `.env.local` con las variables de entorno necesarias.

```bash
cd src-springboot
cp env.txt .env.local
nano .env.local
```

Debes completar estos valores:

```env
MAIL_USERNAME=
MAIL_PASSWORD=
MAIL_HOST=
MAIL_PORT=
TMDB_API_KEY=
APP_JWT_SECRET=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
FRONTEND_URL=
API_KEY=
```

### Qué significa cada variable

- `MAIL_USERNAME` / `MAIL_PASSWORD` / `MAIL_HOST` / `MAIL_PORT`: configuración del envío de correo.
- `TMDB_API_KEY`: clave de acceso a la API de TMDB.
- `APP_JWT_SECRET`: secreto para firmar y validar los JWT.
- `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD`: credenciales de la base de datos definida en `docker-compose.yml`.
- `FRONTEND_URL`: URL del frontend para generar enlaces correctos en correos de confirmación.
- `API_KEY`: clave adicional para proteger el acceso al webservice.

### Generar claves seguras

Para generar `APP_JWT_SECRET` y `API_KEY`, ejecuta este comando dos veces:

```bash
openssl rand -hex 32
```

### Ejecutar el backend

Puedes ejecutar el backend desde IntelliJ IDEA o compilarlo con Maven:

```bash
mvn clean package
```

La compilación ejecuta todos los tests. El `.jar` resultante se generará en el directorio `target/`.

### Si da error con Java

Antes de compilar, puede ser necesario ajustar las variables de entorno del JDK:

```bash
export JAVA_HOME=/usr/lib/jvm/java-25-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
source ~/.bashrc
```

---

## Configuración del frontend

En el frontend se usan archivos `.env.test` o `.env.production`, según el entorno.

```bash
cd src-frontend/frontend
cp env.txt .env.test
# o
cp env.txt .env.production
nano .env.test
# o
nano .env.production
```

Variables necesarias:

```env
VITE_API_BASE_URL=
VITE_API_KEY=
```

### Qué significa cada variable

- `VITE_API_BASE_URL`: URL base de la API Java.
- `VITE_API_KEY`: la misma API key generada previamente.

### Ejecutar el frontend

Modo desarrollo / pruebas:

```bash
npm install
npm run dev
```

Compilación para producción:

```bash
npm run build
```

Los archivos generados aparecerán en `dist/`. Después puedes copiarlos a la carpeta que sirva Apache/Nginx en Docker.

```bash
cp -rf dist/* ../docker-deploy/html
```

---

## Despliegue con Docker

Para despliegues más completos se puede usar el ZIP publicado en **Releases**, junto con un `docker compose` más avanzado que incluye un contenedor para ejecutar el backend.

También existe una rama llamada `FirstProductionVersion`, pensada para producción. Esa versión no incluye Swagger y está más endurecida en seguridad.

### Pasos de despliegue

1. Compilar el frontend.
2. Copiar los archivos generados a `docker-deploy/html`.
3. Configurar las variables de entorno del backend.
4. Ajustar Nginx si se va a desplegar en un servidor real.
5. Levantar los servicios con Docker Compose.

### Configuración de producción

Para un entorno real con HTTPS:

- usar `nginxproduction.conf`,
- configurar el `server_name`,
- generar certificados `.pem` con `certbot`,
- abrir los puertos `80` y `443`.

En producción, Nginx se encarga de redirigir y exponer la aplicación por HTTPS.

### Arrancar el despliegue

Desde la raíz de `docker-deploy`:

```bash
docker compose up -d
```

---

## Endpoints de la API

La documentación de Swagger está disponible por defecto en:

```text
http://localhost:3000/swagger-ui/index.html
```

### Autenticación

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/confirmemail`

### Películas

- `GET /api/movies/popular`
- `GET /api/movies/trending`
- `GET /api/movies/{id}`
- `GET /api/movies/{id}/videos`
- `GET /api/movies/categories`
- `GET /api/movies/category/{id}`

### Series

- `GET /api/tv/popular`
- `GET /api/tv/trending`
- `GET /api/tv/{id}`
- `GET /api/tv/{id}/videos`
- `GET /api/tv/{id}/season/{seasonNumber}`
- `GET /api/tv/{id}/season/{seasonNumber}/episode/{episodeNumber}`
- `GET /api/tv/categories`
- `GET /api/tv/category/{id}`

### Búsqueda

- `GET /api/search/movie`
- `GET /api/search/tv`
- `GET /api/search/multi`

### Estado de usuario

- `GET /api/user/watchlist`
- `POST /api/user/watchlist`
- `DELETE /api/user/watchlist/{type}/{itemId}`
- `GET /api/user/watchlist/status/{type}/{tmdbId}`
- `GET /api/user/watched`
- `POST /api/user/watched`
- `DELETE /api/user/watched/{type}/{itemId}`
- `GET /api/user/watched/status/{type}/{tmdbId}`
- `POST /api/user/tv/{tvId}/episode`
- `GET /api/user/tv/{tvId}/season/{seasonNumber}`

### Comentarios y valoraciones

- `POST /api/reviews/{type}/{id}/rate`
- `GET /api/reviews/{type}/{id}/ratings`
- `GET /api/reviews/{type}/{itemId}/comments`
- `POST /api/reviews/{type}/{itemId}/comments`
- `PUT /api/reviews/comments/{commentId}`
- `DELETE /api/reviews/comments/{commentId}`

### Administración

- `GET /api/admin/comments`
- `DELETE /api/admin/comments/{commentId}`

---

## Notas de instalación

- La API key de TMDB debe ser válida.
- El frontend debe apuntar a la URL correcta del backend.
- La base de datos debe coincidir con las credenciales definidas en Docker Compose.
- Para producción, se recomienda usar la rama `FirstProductionVersion` y el ZIP de Releases.

