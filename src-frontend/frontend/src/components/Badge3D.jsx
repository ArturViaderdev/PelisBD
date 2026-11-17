// Etiqueta "3D" para pósters y títulos.
export default function Badge3D({ className = '' }) {
  return (
    <span
      className={`inline-flex items-center rounded bg-cyan-500 px-2 py-0.5 text-xs font-bold tracking-wider text-black shadow ${className}`}
      title="Película disponible en 3D"
    >
      3D
    </span>
  );
}
