import { useLocation, useNavigate } from 'react-router-dom';
import { FiArrowLeft } from 'react-icons/fi';

// Vuelve a la página anterior (por ejemplo, el listado en la página en que estabas).
// Si se entró directamente al detalle (sin historial en la app), va al inicio.
export default function BackButton({ fallback = '/' }) {
  const navigate = useNavigate();
  const location = useLocation();

  const handleBack = () => {
    if (location.key !== 'default') {
      navigate(-1);
    } else {
      navigate(fallback);
    }
  };

  return (
    <button
      type="button"
      onClick={handleBack}
      className="flex items-center gap-2 px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-gray-700 transition"
    >
      <FiArrowLeft /> Volver
    </button>
  );
}
