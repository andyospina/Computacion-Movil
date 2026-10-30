// Carga datos ficticios (Faker) en el emulador o en Firebase real con el mismo esquema que usa la app.
// Usa el Admin SDK, que ignora las reglas de seguridad (el cliente no puede escribir `products`).
//
//   cd tools/seed-firestore && npm install
//   Emulador (firebase emulators:start --config firebase.emulator.json en otra terminal):
//     node seed-faker.js --target=emulator --users=10 --products=6 --reviews=3
//   Producción (necesita credenciales y confirmar el id del proyecto, escrito a mano):
//     $env:GOOGLE_APPLICATION_CREDENTIALS = "C:\ruta\clave-de-servicio.json"
//     node seed-faker.js --target=production --confirm=reviewlab-b60be
//
// Los documentos llevan el prefijo de id `faker_`, así se pueden borrar sin tocar los datos reales.
const admin = require('firebase-admin');
const { faker } = require('@faker-js/faker/locale/es');

const args = Object.fromEntries(
  process.argv.slice(2).map((a) => a.replace(/^--/, '').split('=')).map(([k, v]) => [k, v ?? true]),
);
const target = args.target || 'emulator';
const PROJECT_ID = 'reviewlab-b60be';
const counts = {
  users: Number(args.users ?? 5),
  products: Number(args.products ?? 5),
  reviewsPerUser: Number(args.reviews ?? 2),
  followsPerUser: Number(args.follows ?? 2),
  savedPerUser: Number(args.saved ?? 2),
};

if (target === 'emulator') {
  process.env.FIRESTORE_EMULATOR_HOST = process.env.FIRESTORE_EMULATOR_HOST || '127.0.0.1:8080';
  admin.initializeApp({ projectId: args.project || 'demo-reviewlab' });
} else if (target === 'production') {
  if (args.confirm !== PROJECT_ID) {
    console.error(`Carga en producción rechazada: repite el id del proyecto con --confirm=${PROJECT_ID}`);
    process.exit(1);
  }
  if (process.env.FIRESTORE_EMULATOR_HOST) {
    console.error('FIRESTORE_EMULATOR_HOST está definido: la carga iría al emulador, no a producción. Quítalo.');
    process.exit(1);
  }
  admin.initializeApp({ credential: admin.credential.applicationDefault(), projectId: PROJECT_ID });
} else {
  console.error('--target debe ser "emulator" o "production"');
  process.exit(1);
}

const db = admin.firestore();
const ts = (d) => admin.firestore.Timestamp.fromDate(d);
const CATEGORIES = ['Electrónica', 'Hogar', 'Deportes', 'Libros', 'Moda'];

const users = Array.from({ length: counts.users }, () => {
  const name = faker.person.fullName();
  return {
    id: `faker_${faker.string.uuid()}`,
    name,
    username: faker.internet.username({ firstName: name.split(' ')[0] }).toLowerCase().slice(0, 30),
    email: faker.internet.email().toLowerCase(),
    country: faker.location.country(),
    bio: faker.lorem.sentence(8).slice(0, 300),
    profileImage: null,
    createdAt: ts(faker.date.past({ years: 2 })),
  };
});

const products = Array.from({ length: counts.products }, () => ({
  id: `faker_${faker.string.uuid()}`,
  name: faker.commerce.productName(),
  category: faker.helpers.arrayElement(CATEGORIES),
}));

const reviews = counts.products === 0 ? [] : users.flatMap((author) =>
  Array.from({ length: counts.reviewsPerUser }, () => {
    const product = faker.helpers.arrayElement(products);
    const rating = faker.number.int({ min: 1, max: 5 });
    return {
      id: `faker_${faker.string.uuid()}`,
      userId: author.id,
      productId: product.id,
      productName: product.name,
      rating,
      title: faker.lorem.sentence(3).replace(/\.$/, '').slice(0, 120),
      body: faker.lorem.paragraph(2).slice(0, 2000),
      recommended: rating >= 4,
      user: { name: author.name, username: author.username, profileImage: null },
      likedBy: faker.helpers.arrayElements(users.filter((u) => u.id !== author.id).map((u) => u.id), { min: 0, max: 3 }),
      createdAt: ts(faker.date.recent({ days: 90 })),
    };
  }),
);

const follows = users.flatMap((follower) =>
  faker.helpers.arrayElements(users.filter((u) => u.id !== follower.id), { min: 0, max: counts.followsPerUser })
    .map((followed) => ({ followerId: follower.id, followedId: followed.id, createdAt: ts(faker.date.recent({ days: 60 })) })),
);

const saved = users.flatMap((owner) =>
  faker.helpers.arrayElements(products, { min: 0, max: Math.min(counts.savedPerUser, products.length) })
    .map((p) => ({ userId: owner.id, productId: p.id, createdAt: ts(faker.date.recent({ days: 30 })) })),
);

async function write(collection, docs, idOf) {
  for (let i = 0; i < docs.length; i += 400) {
    const batch = db.batch();
    for (const doc of docs.slice(i, i + 400)) {
      const { id, ...data } = doc;
      batch.set(db.collection(collection).doc(idOf ? idOf(doc) : id), data);
    }
    await batch.commit();
  }
  return docs.length;
}

async function main() {
  const report = {};
  report.users = await write('users', users);
  report.products = await write('products', products);
  report.reviews = await write('reviews', reviews);
  report.follows = await write('follows', follows, (f) => `${f.followerId}_${f.followedId}`);
  report.saved = await write('saved', saved, (s) => `${s.userId}_${s.productId}`);
  console.log(`Carga en ${target} completa:`, report);
}

main().catch((e) => { console.error('Carga fallida:', e.message); process.exit(1); });
