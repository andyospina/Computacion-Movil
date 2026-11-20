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
  arrayUnion, arrayRemove, serverTimestamp, writeBatch, GeoPoint,
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
  const carol = env.authenticatedContext('carol').firestore();
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
  await check('crear reseña con ubicación: permitido', () => assertSucceeds(addDoc(collection(alice, 'reviews'), {
    userId: 'alice', productId: 'p1', rating: 5, title: 't', body: 'b', recommended: true, likedBy: [],
    location: new GeoPoint(4.711, -74.072), createdAt: serverTimestamp(),
  })));
  await check('crear reseña con location null: permitido', () => assertSucceeds(addDoc(collection(alice, 'reviews'), {
    userId: 'alice', productId: 'p1', rating: 5, title: 't', body: 'b', recommended: true, likedBy: [], location: null,
  })));
  await check('location que no es un punto: denegado', () => assertFails(addDoc(collection(alice, 'reviews'), {
    userId: 'alice', productId: 'p1', rating: 5, title: 't', body: 'b', recommended: true, likedBy: [], location: 'Bogotá',
  })));
  await check('el autor no cambia la ubicación después', () => assertFails(updateDoc(doc(bob, 'reviews/r1'), {
    location: new GeoPoint(0, 0),
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

  console.log('\nReglas: reports');
  await check('alice reporta la reseña de bob', () => assertSucceeds(setDoc(doc(alice, 'reports/alice_r1'), {
    userId: 'alice', reviewId: 'r1', reason: 'spam', createdAt: serverTimestamp(),
  })));
  await check('alice no reporta a nombre de carol', () => assertFails(setDoc(doc(alice, 'reports/carol_r1'), {
    userId: 'carol', reviewId: 'r1', reason: 'spam', createdAt: serverTimestamp(),
  })));
  await check('el id debe ser uid_reseña', () => assertFails(setDoc(doc(alice, 'reports/cualquiera'), {
    userId: 'alice', reviewId: 'r1', reason: 'spam', createdAt: serverTimestamp(),
  })));
  await check('motivo inválido: denegado', () => assertFails(setDoc(doc(carol, 'reports/carol_r1'), {
    userId: 'carol', reviewId: 'r1', reason: 'me-cae-mal', createdAt: serverTimestamp(),
  })));
  await check('no se reporta una reseña inexistente', () => assertFails(setDoc(doc(alice, 'reports/alice_nope'), {
    userId: 'alice', reviewId: 'nope', reason: 'fake', createdAt: serverTimestamp(),
  })));
  await check('bob no reporta su propia reseña', () => assertFails(setDoc(doc(bob, 'reports/bob_r1'), {
    userId: 'bob', reviewId: 'r1', reason: 'other', createdAt: serverTimestamp(),
  })));
  await check('campos extra: denegado', () => assertFails(setDoc(doc(carol, 'reports/carol_r1'), {
    userId: 'carol', reviewId: 'r1', reason: 'spam', createdAt: serverTimestamp(), status: 'resolved',
  })));
  await check('alice consulta si ya reportó una reseña que aún no reportó (documento inexistente)', () => assertSucceeds(getDoc(doc(alice, 'reports/alice_r2'))));
  await check('alice no consulta reportes ajenos aunque no existan', () => assertFails(getDoc(doc(alice, 'reports/bob_r2'))));
  await check('alice lee su reporte', () => assertSucceeds(getDoc(doc(alice, 'reports/alice_r1'))));
  await check('bob no lee el reporte de alice', () => assertFails(getDoc(doc(bob, 'reports/alice_r1'))));
  await check('el reporte no se edita', () => assertFails(updateDoc(doc(alice, 'reports/alice_r1'), { reason: 'fake' })));
  await check('el reporte no se borra', () => assertFails(deleteDoc(doc(alice, 'reports/alice_r1'))));

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

  console.log('\nReglas: comments');
  const comment = (uid, extra = {}) => ({
    reviewId: 'r1', userId: uid, text: 'Buena reseña', user: { name: uid, username: uid, profileImage: null },
    createdAt: serverTimestamp(), ...extra,
  });
  await check('alice comenta una reseña', () => assertSucceeds(setDoc(doc(alice, 'comments/c-alice'), comment('alice'))));
  await check('sin sesión no se leen comentarios', () => assertFails(getDoc(doc(anon, 'comments/c-alice'))));
  await check('bob lee la conversación', () => assertSucceeds(getDocs(query(collection(bob, 'comments'), where('reviewId', '==', 'r1')))));
  await check('alice no comenta a nombre de carol', () => assertFails(setDoc(doc(alice, 'comments/c-x1'), comment('carol'))));
  await check('comentario vacío o solo espacios: denegado', () => assertFails(setDoc(doc(alice, 'comments/c-x2'), comment('alice', { text: '   ' }))));
  await check('comentario de más de 500 caracteres: denegado', () => assertFails(setDoc(doc(alice, 'comments/c-x3'), comment('alice', { text: 'a'.repeat(501) }))));
  await check('comentario en reseña inexistente: denegado', () => assertFails(setDoc(doc(alice, 'comments/c-x4'), comment('alice', { reviewId: 'nope' }))));
  await check('campos extra (likes falsos): denegado', () => assertFails(setDoc(doc(alice, 'comments/c-x5'), comment('alice', { likes: 99 }))));
  await check('fecha inventada por el cliente: denegado', () => assertFails(setDoc(doc(alice, 'comments/c-x6'), comment('alice', { createdAt: new Date(0) }))));
  await check('nadie edita un comentario (ni su autora)', () => assertFails(updateDoc(doc(alice, 'comments/c-alice'), { text: 'cambiado' })));
  await check('carol no borra el comentario de alice', async () => {
    await assertFails(deleteDoc(doc(carol, 'comments/c-alice')));
  });
  await check('bob (autor de la reseña) modera y borra el de alice', () => assertSucceeds(deleteDoc(doc(bob, 'comments/c-alice'))));
  await check('alice borra su propio comentario', async () => {
    await assertSucceeds(setDoc(doc(alice, 'comments/c-alice2'), comment('alice')));
    await assertSucceeds(deleteDoc(doc(alice, 'comments/c-alice2')));
  });

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

  await check('comentario de alice -> notificación para bob con la reseña', async () => {
    await setDoc(doc(alice, 'comments/c-notif'), {
      reviewId: 'r1', userId: 'alice', text: '¿Los recomiendas para correr?',
      user: { name: 'Falsa', username: 'x', profileImage: null }, createdAt: serverTimestamp(),
    });
    const n = await waitFor('notificación comment', async () => {
      const d = await getDoc(doc(bob, 'users/bob/notifications/comment_c-notif'));
      return d.exists() ? d : null;
    });
    const data = n.data();
    if (data.type !== 'comment' || data.actorId !== 'alice' || data.reviewId !== 'r1' || data.read !== false) {
      throw new Error(JSON.stringify(data));
    }
  });
  await check('integridad: copia del autor del comentario se corrige', async () => {
    await waitFor('autor del comentario corregido', async () => {
      const d = (await getDoc(doc(alice, 'comments/c-notif'))).data();
      return d && d.user.name.startsWith('Alice') && d.user.username === 'alice';
    });
  });
  await check('comentar la propia reseña no genera notificación', async () => {
    await setDoc(doc(bob, 'comments/c-bob'), {
      reviewId: 'r1', userId: 'bob', text: 'Gracias por leer', user: { name: 'Bob', username: 'bob', profileImage: null },
      createdAt: serverTimestamp(),
    });
    await new Promise((r) => setTimeout(r, 4000));
    if ((await getDoc(doc(bob, 'users/bob/notifications/comment_c-bob'))).exists()) throw new Error('se notificó');
  });
  await check('borrar la reseña borra su conversación', async () => {
    await assertSucceeds(deleteDoc(doc(bob, 'reviews/r1')));
    await waitFor('comentarios eliminados', async () => {
      const s = await getDocs(query(collection(bob, 'comments'), where('reviewId', '==', 'r1')));
      return s.empty;
    });
  });

  await env.cleanup();
  console.log(`\n${passed} ok, ${failed} fallidas`);
  process.exit(failed === 0 ? 0 : 1);
})().catch((e) => {
  console.error(e);
  process.exit(1);
});
