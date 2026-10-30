// Prueba de firestore.rules y de las Cloud Functions contra los emuladores locales.
// Usa el proyecto "demo-reviewlab": no necesita credenciales ni toca el proyecto real.
//   cd tools/emulator-tests && npm install && npm test
const fs = require('fs');
const path = require('path');
const {
  initializeTestEnvironment, assertSucceeds, assertFails,
} = require('@firebase/rules-unit-testing');
const {
  doc, setDoc, getDoc, updateDoc, deleteDoc, addDoc, collection, getDocs, query, where,
  arrayUnion, arrayRemove, serverTimestamp, writeBatch,
} = require('firebase/firestore');

const REPO = process.argv[2];
let passed = 0;
let failed = 0;

async function check(name, fn) {
  try {
    await fn();
    passed++;
    console.log(`  ok   ${name}`);
  } catch (e) {
    failed++;
    console.log(`  FAIL ${name}: ${e.message}`);
  }
}

async function waitFor(name, fn, timeoutMs = 20000) {
  const start = Date.now();
  while (Date.now() - start < timeoutMs) {
    const value = await fn();
    if (value) return value;
    await new Promise((r) => setTimeout(r, 500));
  }
  throw new Error(`timeout esperando: ${name}`);
}

(async () => {
  const env = await initializeTestEnvironment({
    projectId: 'demo-reviewlab',
    firestore: {
      host: '127.0.0.1',
      port: 8080,
      rules: fs.readFileSync(path.join(REPO, 'firestore.rules'), 'utf8'),
    },
  });
  await env.clearFirestore();

  await env.withSecurityRulesDisabled(async (ctx) => {
    const db = ctx.firestore();
    await setDoc(doc(db, 'users/bob'), { username: 'bob', name: 'Bob', email: 'b@x.com' });
    await setDoc(doc(db, 'users/carol'), { username: 'carol', name: 'Carol', email: 'c@x.com' });
    await setDoc(doc(db, 'products/p1'), { name: 'Audífonos', category: 'Electrónica' });
    await setDoc(doc(db, 'reviews/r1'), {
      userId: 'bob', productId: 'p1', productName: 'Audífonos', rating: 4, title: 'Buenos',
      body: 'b', recommended: true, user: { name: 'Bob', username: 'bob', profileImage: null },
      likedBy: ['carol'],
    });
    // Reseña sembrada sin el campo likedBy (como las del Sprint 10)
    await setDoc(doc(db, 'reviews/r0'), {
      userId: 'bob', productId: 'p1', rating: 3, title: 'Vieja', body: 'b', recommended: false,
    });
  });

  const alice = env.authenticatedContext('alice').firestore();
  const bob = env.authenticatedContext('bob').firestore();
  const anon = env.unauthenticatedContext().firestore();

  console.log('\nReglas: users');
  await check('alice crea su perfil', () => assertSucceeds(setDoc(doc(alice, 'users/alice'), {
    username: 'alice', name: 'Alice', email: 'a@x.com', country: '', bio: '', profileImage: null,
    createdAt: serverTimestamp(),
  })));
  await check('alice no crea el perfil de otro', () => assertFails(setDoc(doc(alice, 'users/mallory'), {
    username: 'm', name: 'M',
  })));
  await check('alice edita nombre/país/bio', () => assertSucceeds(updateDoc(doc(alice, 'users/alice'), {
    name: 'Alice', country: 'CO', bio: 'hola',
  })));
  await check('alice no cambia su username', () => assertFails(updateDoc(doc(alice, 'users/alice'), { username: 'x' })));
  await check('sin sesión no se leen perfiles', () => assertFails(getDoc(doc(anon, 'users/bob'))));

  console.log('\nReglas: likes');
  await check('alice da like', () => assertSucceeds(updateDoc(doc(alice, 'reviews/r1'), { likedBy: arrayUnion('alice') })));
  await check('like a reseña sin campo likedBy', () => assertSucceeds(updateDoc(doc(alice, 'reviews/r0'), { likedBy: arrayUnion('alice') })));
  await check('alice no agrega el uid de otro', () => assertFails(updateDoc(doc(alice, 'reviews/r1'), { likedBy: arrayUnion('dave') })));
  await check('alice no reemplaza la lista', () => assertFails(updateDoc(doc(alice, 'reviews/r1'), { likedBy: ['alice'] })));
  await check('alice no quita el like de carol', () => assertFails(updateDoc(doc(alice, 'reviews/r1'), { likedBy: arrayRemove('carol') })));
  await check('like + editar título a la vez: denegado', () => assertFails(updateDoc(doc(alice, 'reviews/r1'), {
    likedBy: arrayRemove('alice'), title: 'hack',
  })));
  await check('alice quita su like', () => assertSucceeds(updateDoc(doc(alice, 'reviews/r1'), { likedBy: arrayRemove('alice') })));

  console.log('\nReglas: reseñas');
  await check('el autor edita su reseña', () => assertSucceeds(updateDoc(doc(bob, 'reviews/r1'), {
    rating: 5, title: 'Muy buenos', body: 'b', recommended: true,
  })));
  await check('el autor no toca likedBy al editar', () => assertFails(updateDoc(doc(bob, 'reviews/r1'), {
    title: 'x', likedBy: [],
  })));
  await check('otro usuario no edita la reseña', () => assertFails(updateDoc(doc(alice, 'reviews/r1'), { title: 'x' })));
  await check('crear reseña con likes iniciales: denegado', () => assertFails(addDoc(collection(alice, 'reviews'), {
    userId: 'alice', productId: 'p1', rating: 5, title: 't', body: 'b', recommended: true, likedBy: ['alice'],
  })));
  await check('crear reseña de producto inexistente: denegado', () => assertFails(addDoc(collection(alice, 'reviews'), {
    userId: 'alice', productId: 'nope', rating: 5, title: 't', body: 'b', recommended: true, likedBy: [],
  })));
  await check('crear reseña a nombre de otro: denegado', () => assertFails(addDoc(collection(alice, 'reviews'), {
    userId: 'bob', productId: 'p1', rating: 5, title: 't', body: 'b', recommended: true, likedBy: [],
  })));

  console.log('\nReglas: saved');
  await check('alice guarda un producto', () => assertSucceeds(setDoc(doc(alice, 'saved/alice_p1'), {
    userId: 'alice', productId: 'p1', createdAt: serverTimestamp(),
  })));
  await check('alice no guarda a nombre de bob', () => assertFails(setDoc(doc(alice, 'saved/bob_p1'), {
    userId: 'bob', productId: 'p1', createdAt: serverTimestamp(),
  })));
  await check('no se guarda un producto inexistente', () => assertFails(setDoc(doc(alice, 'saved/alice_nope'), {
    userId: 'alice', productId: 'nope', createdAt: serverTimestamp(),
  })));
  await check('el id debe ser uid_producto', () => assertFails(setDoc(doc(alice, 'saved/cualquiera'), {
    userId: 'alice', productId: 'p1', createdAt: serverTimestamp(),
  })));
  await check('bob no lee los guardados de alice', () => assertFails(getDoc(doc(bob, 'saved/alice_p1'))));
  await check('bob no consulta los guardados de alice', () => assertFails(getDocs(query(collection(bob, 'saved'), where('userId', '==', 'alice')))));
  await check('alice consulta los suyos', () => assertSucceeds(getDocs(query(collection(alice, 'saved'), where('userId', '==', 'alice')))));
  await check('bob no quita el guardado de alice', () => assertFails(deleteDoc(doc(bob, 'saved/alice_p1'))));
  await check('alice quita su guardado', () => assertSucceeds(deleteDoc(doc(alice, 'saved/alice_p1'))));

  console.log('\nReglas: follows');
  await check('alice sigue a bob', () => assertSucceeds(setDoc(doc(alice, 'follows/alice_bob'), {
    followerId: 'alice', followedId: 'bob', createdAt: serverTimestamp(),
  })));
  await check('alice no crea un follow a nombre de bob', () => assertFails(setDoc(doc(alice, 'follows/bob_carol'), {
    followerId: 'bob', followedId: 'carol', createdAt: serverTimestamp(),
  })));
  await check('alice no se sigue a sí misma', () => assertFails(setDoc(doc(alice, 'follows/alice_alice'), {
    followerId: 'alice', followedId: 'alice', createdAt: serverTimestamp(),
  })));
  await check('id del documento debe coincidir', () => assertFails(setDoc(doc(alice, 'follows/cualquiera'), {
    followerId: 'alice', followedId: 'carol', createdAt: serverTimestamp(),
  })));
  await check('no se sigue a un usuario inexistente', () => assertFails(setDoc(doc(alice, 'follows/alice_ghost'), {
    followerId: 'alice', followedId: 'ghost', createdAt: serverTimestamp(),
  })));
  await check('bob no borra el follow de alice', () => assertFails(deleteDoc(doc(bob, 'follows/alice_bob'))));
  await check('consulta de seguidores de bob', () => assertSucceeds(getDocs(query(collection(bob, 'follows'), where('followedId', '==', 'bob')))));

  console.log('\nReglas: tokens y bandeja');
  await check('alice registra su token', () => assertSucceeds(setDoc(doc(alice, 'users/alice/fcmTokens/tok123'), {
    token: 'tok123', platform: 'android', updatedAt: serverTimestamp(),
  })));
  await check('bob no lee tokens de alice', () => assertFails(getDoc(doc(bob, 'users/alice/fcmTokens/tok123'))));
  await check('alice no se crea notificaciones', () => assertFails(addDoc(collection(alice, 'users/alice/notifications'), {
    type: 'like', read: false,
  })));

  console.log('\nCloud Functions');
  await check('like de alice -> notificación para bob', async () => {
    await updateDoc(doc(alice, 'reviews/r1'), { likedBy: arrayUnion('alice') });
    const n = await waitFor('notificación like', async () => {
      const s = await getDocs(query(collection(bob, 'users/bob/notifications'), where('type', '==', 'like')));
      return s.docs.find((d) => d.data().actorId === 'alice');
    });
    const data = n.data();
    if (data.actorName !== 'Alice' || data.productId !== 'p1' || data.read !== false) {
      throw new Error(JSON.stringify(data));
    }
  });
  await check('quitar y volver a dar like no duplica la notificación', async () => {
    await updateDoc(doc(alice, 'reviews/r1'), { likedBy: arrayRemove('alice') });
    await updateDoc(doc(alice, 'reviews/r1'), { likedBy: arrayUnion('alice') });
    await new Promise((r) => setTimeout(r, 4000));
    const s = await getDocs(collection(bob, 'users/bob/notifications'));
    const likes = s.docs.filter((d) => d.id === 'like_r1_alice');
    if (likes.length !== 1) throw new Error(`hay ${likes.length}`);
  });
  await check('bob marca la notificación como leída', async () => {
    const s = await getDocs(collection(bob, 'users/bob/notifications'));
    await assertSucceeds(updateDoc(s.docs[0].ref, { read: true }));
    await assertFails(updateDoc(s.docs[0].ref, { actorName: 'x' }));
  });
  await check('follow de alice -> notificación para bob', async () => {
    await waitFor('notificación follow', async () => {
      const s = await getDocs(query(collection(bob, 'users/bob/notifications'), where('type', '==', 'follow')));
      return s.docs.find((d) => d.data().actorId === 'alice' && d.data().actorName === 'Alice');
    });
  });
  await check('alice deja de seguir a bob', () => assertSucceeds(deleteDoc(doc(alice, 'follows/alice_bob'))));
  await check('integridad: copia del autor falsificada se corrige', async () => {
    const ref = await addDoc(collection(alice, 'reviews'), {
      userId: 'alice', productId: 'p1', productName: 'Producto falso', rating: 5, title: 'Top',
      body: 'b', recommended: true, user: { name: 'Admin oficial', username: 'admin', profileImage: null },
      likedBy: [], createdAt: serverTimestamp(),
    });
    await waitFor('autor corregido', async () => {
      const d = (await getDoc(ref)).data();
      return d.user.name === 'Alice' && d.user.username === 'alice' && d.productName === 'Audífonos';
    });
  });
  await check('integridad: cambiar nombre actualiza sus reseñas', async () => {
    await updateDoc(doc(alice, 'users/alice'), { name: 'Alice Pérez', country: 'CO', bio: '' });
    await waitFor('reseñas sincronizadas', async () => {
      const s = await getDocs(query(collection(alice, 'reviews'), where('userId', '==', 'alice')));
      return s.size > 0 && s.docs.every((d) => d.data().user.name === 'Alice Pérez');
    });
  });
  await check('like propio no genera notificación', async () => {
    const before = (await getDocs(collection(alice, 'users/alice/notifications'))).size;
    const s = await getDocs(query(collection(alice, 'reviews'), where('userId', '==', 'alice')));
    await updateDoc(s.docs[0].ref, { likedBy: arrayUnion('alice') });
    await new Promise((r) => setTimeout(r, 4000));
    const after = (await getDocs(collection(alice, 'users/alice/notifications'))).size;
    if (after !== before) throw new Error(`${before} -> ${after}`);
  });

  await env.cleanup();
  console.log(`\n${passed} ok, ${failed} fallidas`);
  process.exit(failed === 0 ? 0 : 1);
})().catch((e) => {
  console.error(e);
  process.exit(1);
});
