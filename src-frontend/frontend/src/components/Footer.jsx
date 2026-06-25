export default function Footer() {
  return (
    <footer className="bg-gray-900 text-gray-400 mt-auto border-t border-gray-800">
      <div className="max-w-7xl mx-auto px-4 py-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div>
            <h3 className="text-white font-bold mb-4">PelisBD</h3>
            <p className="text-sm">
              Tu plataforma para descubrir, calificar y comentar tus películas y series favoritas.
            </p>
          </div>
          <div>
            <h4 className="text-white font-bold mb-4">Enlaces</h4>
            <ul className="space-y-2 text-sm">
              <li><a href="#" className="hover:text-white transition">Inicio</a></li>
              <li><a href="/movies" className="hover:text-white transition">Películas</a></li>
              <li><a href="/tv" className="hover:text-white transition">Series</a></li>
              <li><a href="/contacto" className="hover:text-white transition">Contacto</a></li>
            </ul>
          </div>
          <div>
            <h4 className="text-white font-bold mb-4">Información</h4>
            <p className="text-sm">
              Datos proporcionados por TMDB (The Movie Database)
            </p>
          </div>
        </div>
        <div className="border-t border-gray-800 mt-8 pt-8 text-center text-sm">
          <p>&copy; 2024 PelisBD. Todos los derechos reservados.</p>
        </div>
      </div>
    </footer>
  );
}
