import { useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';

// Guarda la página (y opcionalmente el orden) de un listado en la URL:
//   /watched?page=3&sort=userating
// Así, al abrir el detalle de una película y volver atrás, se mantiene la página.
// La página en la URL siempre empieza en 1.
export function useListParams({ defaultSort } = {}) {
  const [searchParams, setSearchParams] = useSearchParams();

  const page = Math.max(1, parseInt(searchParams.get('page'), 10) || 1);
  const sort = searchParams.get('sort') || defaultSort;

  const setPage = useCallback(
    (valueOrUpdater) => {
      setSearchParams((prev) => {
        const current = Math.max(1, parseInt(prev.get('page'), 10) || 1);
        const next =
          typeof valueOrUpdater === 'function'
            ? valueOrUpdater(current)
            : valueOrUpdater;

        const params = new URLSearchParams(prev);
        if (!next || next <= 1) params.delete('page');
        else params.set('page', String(next));
        return params;
      });
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },
    [setSearchParams]
  );

  // Cambiar el orden vuelve a la primera página.
  const setSort = useCallback(
    (newSort) => {
      setSearchParams((prev) => {
        const params = new URLSearchParams(prev);
        if (!newSort || newSort === defaultSort) params.delete('sort');
        else params.set('sort', newSort);
        params.delete('page');
        return params;
      });
    },
    [setSearchParams, defaultSort]
  );

  return { page, setPage, sort, setSort };
}
