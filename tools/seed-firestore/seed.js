// Carga datos de prueba en Firestore (usuarios semilla, productos y reseñas).
// Usa el Admin SDK, que ignora las reglas de seguridad. Es idempotente: usa ids fijos, así que
// ejecutarlo de nuevo sobrescribe los mismos documentos sin duplicar nada.
//
//   cd tools/seed-firestore && npm install
//   $env:GOOGLE_APPLICATION_CREDENTIALS = "C:\ruta\a\clave-de-servicio.json"   (PowerShell)
//   node seed.js
const admin = require('firebase-admin');

admin.initializeApp({ credential: admin.credential.applicationDefault(), projectId: 'reviewlab-b60be' });
const db = admin.firestore();
const ts = (iso) => admin.firestore.Timestamp.fromDate(new Date(iso));

// Estos usuarios no tienen cuenta en Authentication: solo sirven para ver "perfil de otro usuario".
const users = {
  seed_ada: { username: 'ada', name: 'Ada Lovelace', email: 'ada@example.com', country: 'Reino Unido', bio: 'Me gusta probar gadgets antes que nadie.', profileImage: null, createdAt: ts('2025-03-10T10:00:00Z') },
  seed_linus: { username: 'linus', name: 'Linus Torres', email: 'linus@example.com', country: 'Colombia', bio: '', profileImage: null, createdAt: ts('2025-06-21T10:00:00Z') },
  seed_grace: { username: 'grace', name: 'Grace Hopper', email: 'grace@example.com', country: '', bio: 'Reseñas honestas, sin filtros.', profileImage: null, createdAt: ts('2024-11-02T10:00:00Z') },
};

const products = {
  p_audifonos: { name: 'Audífonos inalámbricos Pulse', category: 'Electrónica' },
  p_lampara: { name: 'Lámpara de escritorio LED', category: 'Hogar' },
  p_balon: { name: 'Balón de fútbol Pro', category: 'Deportes' },
  p_novela: { name: 'La sombra del viento', category: 'Libros' },
  p_chaqueta: { name: 'Chaqueta impermeable Trail', category: 'Moda' },
};

// [id, userId, productId, rating, title, body, fecha]
const reviewRows = [
  ['r1', 'seed_ada', 'p_audifonos', 5, 'Sonido excelente', 'La batería dura todo el día y el sonido es muy nítido.', '2026-09-01T12:00:00Z'],
  ['r2', 'seed_linus', 'p_audifonos', 4, 'Muy buenos por el precio', 'Cómodos, aunque el estuche es algo grande.', '2026-09-03T12:00:00Z'],
  ['r3', 'seed_grace', 'p_audifonos', 2, 'Se desconectan', 'Pierden la conexión cada cierto tiempo.', '2026-09-05T12:00:00Z'],
  ['r4', 'seed_ada', 'p_lampara', 5, 'Luz perfecta', 'Tres niveles de brillo y no cansa la vista.', '2026-09-07T12:00:00Z'],
  ['r5', 'seed_grace', 'p_lampara', 3, 'Cumple', 'Funciona bien pero el brazo es algo duro.', '2026-09-08T12:00:00Z'],
  ['r6', 'seed_linus', 'p_balon', 4, 'Buen balón', 'Buen bote y resistente al uso diario.', '2026-09-10T12:00:00Z'],
  ['r7', 'seed_ada', 'p_novela', 5, 'Una joya', 'Atrapante de principio a fin.', '2026-09-12T12:00:00Z'],
  ['r8', 'seed_grace', 'p_novela', 4, 'Muy recomendable', 'Los personajes están muy bien construidos.', '2026-09-13T12:00:00Z'],
  ['r9', 'seed_linus', 'p_chaqueta', 1, 'No es impermeable', 'Se mojó en la primera lluvia fuerte.', '2026-09-15T12:00:00Z'],
];

async function main() {
  const batch = db.batch();
  for (const [id, data] of Object.entries(users)) batch.set(db.collection('users').doc(id), data);
  for (const [id, data] of Object.entries(products)) batch.set(db.collection('products').doc(id), data);
  for (const [id, userId, productId, rating, title, body, date] of reviewRows) {
    const u = users[userId];
    batch.set(db.collection('reviews').doc(id), {
      userId,
      productId,
      productName: products[productId].name,
      rating,
      title,
      body,
      recommended: rating >= 4,
      // Copia desnormalizada del autor (misma forma que escribe la app)
      user: { name: u.name, username: u.username, profileImage: u.profileImage },
      createdAt: ts(date),
    });
  }
  await batch.commit();
  console.log(`Listo: ${Object.keys(users).length} usuarios, ${Object.keys(products).length} productos, ${reviewRows.length} reseñas.`);
}

main().catch((e) => { console.error(e); process.exit(1); });
