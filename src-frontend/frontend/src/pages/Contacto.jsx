export default function Contacto() {
  return (
    <main style={{ minHeight: '100vh', padding: '2rem' }}>
      <section
        style={{
          maxWidth: '800px',
          margin: '0 auto',
          padding: '2rem',
          borderRadius: '16px',
          background: '#111827',
          color: '#f9fafb',
        }}
      >
        <h1>Contacto</h1>

        <p>
          Aplicación web realizada por Artur Viader Mataix como parte del
          proyecto final de Java Spring Boot en la It Academy de Barcelona Activa.
        </p>

        <p>
          Contacto:{' '}
          <a href="mailto:pelisbd@magictoolsweb.com">
            pelisbd@magictoolsweb.com
          </a>
        </p>
      </section>
    </main>
  )
}
