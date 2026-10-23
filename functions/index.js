// Cloud Functions de ReviewLab (Sprint 11).
//
//   onReviewLiked       reviews/{id} actualizada  -> si entró un uid nuevo a likedBy, avisa al autor.
//   onUserFollowed      follows/{id} creado       -> avisa al usuario seguido.
//   onReviewCreated     reviews/{id} creada       -> integridad: rehace la copia del autor y del producto
//                                                   con los datos reales (el cliente podría mentir).
//   onUserProfileUpdated users/{uid} actualizado  -> integridad: propaga nombre/foto/username a la copia
//                                                   desnormalizada de todas sus reseñas.
//
// Las notificaciones se guardan en users/{uid}/notifications (bandeja de la app) y se envían por FCM
// a cada token de users/{uid}/fcmTokens. Los tokens que FCM rechaza se borran.
//
// El Admin SDK ignora las reglas de seguridad, por eso aquí se valida todo lo que se lee.
const { onDocumentCreated, onDocumentUpdated } = require('firebase-functions/v2/firestore');
const { setGlobalOptions } = require('firebase-functions/v2');
const logger = require('firebase-functions/logger');
const { initializeApp } = require('firebase-admin/app');
const { getFirestore, FieldValue } = require('firebase-admin/firestore');
const { getMessaging } = require('firebase-admin/messaging');

initializeApp();
setGlobalOptions({ region: 'us-central1', maxInstances: 10 });

const db = getFirestore();

// Debe coincidir con AppNotifications.SOCIAL_CHANNEL_ID en la app.
const ANDROID_CHANNEL_ID = 'social';
const BATCH_LIMIT = 450; // Firestore permite 500 escrituras por lote
const ALREADY_EXISTS = 6; // código gRPC de create() sobre un documento existente

const INVALID_TOKEN_ERRORS = new Set([
  'messaging/registration-token-not-registered',
  'messaging/invalid-registration-token',
  'messaging/invalid-argument',
]);

/** Nombre visible de un usuario, o null si no existe. */
async function displayNameOf(uid) {
  const snap = await db.collection('users').doc(uid).get();
  if (!snap.exists) return null;
  const data = snap.data();
  return data.name || data.username || null;
}

/** Envía la notificación push a todos los dispositivos de `uid` y limpia los tokens inválidos. */
async function sendPush(uid, title, body, data) {
  const tokensSnap = await db.collection('users').doc(uid).collection('fcmTokens').get();
  const tokens = tokensSnap.docs.map((d) => d.id);
  if (tokens.length === 0) {
    logger.info(`El usuario ${uid} no tiene dispositivos registrados`);
    return;
  }

  const response = await getMessaging().sendEachForMulticast({
    tokens,
    notification: { title, body },
    data, // solo strings
    android: { notification: { channelId: ANDROID_CHANNEL_ID } },
  });

  const invalid = [];
  response.responses.forEach((r, i) => {
    if (!r.success && INVALID_TOKEN_ERRORS.has(r.error?.code)) invalid.push(tokens[i]);
  });
  await Promise.all(
    invalid.map((t) => db.collection('users').doc(uid).collection('fcmTokens').doc(t).delete())
  );
  logger.info(`Push a ${uid}: ${response.successCount} enviados, ${invalid.length} tokens borrados`);
}

/**
 * Guarda la notificación en la bandeja del destinatario y la envía por FCM. El id es determinista
 * (p. ej. like_{reseña}_{usuario}): si alguien quita y vuelve a dar like, o deja de seguir y vuelve a
 * seguir, no se repite la notificación.
 */
async function notify(recipientId, notificationId, notification, title, body) {
  try {
    await db.collection('users').doc(recipientId).collection('notifications').doc(notificationId).create({
      ...notification,
      read: false,
      createdAt: FieldValue.serverTimestamp(),
    });
  } catch (e) {
    if (e.code === ALREADY_EXISTS) {
      logger.info(`Notificación ${notificationId} ya enviada a ${recipientId}`);
      return;
    }
    throw e;
  }
  await sendPush(recipientId, title, body, {
    type: notification.type,
    actorId: notification.actorId,
    productId: notification.productId || '',
  });
}

// ---------------------------------------------------------------------------------------------
// Notificación: like a una reseña
// ---------------------------------------------------------------------------------------------
exports.onReviewLiked = onDocumentUpdated('reviews/{reviewId}', async (event) => {
  const before = event.data.before.data() || {};
  const after = event.data.after.data() || {};
  const previous = new Set(before.likedBy || []);
  const newLikers = (after.likedBy || []).filter((uid) => !previous.has(uid));
  if (newLikers.length === 0) return; // no fue un like (edición, unlike, sincronización del autor)

  const authorId = after.userId;
  if (!authorId) return;

  for (const likerId of newLikers) {
    if (likerId === authorId) continue; // like a la propia reseña: no se notifica
    const likerName = await displayNameOf(likerId);
    if (!likerName) {
      logger.warn(`Like de un usuario inexistente (${likerId}) en ${event.params.reviewId}`);
      continue;
    }
    await notify(
      authorId,
      `like_${event.params.reviewId}_${likerId}`,
      {
        type: 'like',
        actorId: likerId,
        actorName: likerName,
        reviewId: event.params.reviewId,
        reviewTitle: after.title || '',
        productId: after.productId || '',
      },
      'Nuevo me gusta',
      `${likerName} le dio me gusta a tu reseña "${after.title || ''}"`
    );
  }
});

// ---------------------------------------------------------------------------------------------
// Notificación: nuevo seguidor
// ---------------------------------------------------------------------------------------------
exports.onUserFollowed = onDocumentCreated('follows/{followId}', async (event) => {
  const follow = event.data?.data();
  if (!follow) return;
  const { followerId, followedId } = follow;
  if (!followerId || !followedId || followerId === followedId) return;

  const followerName = await displayNameOf(followerId);
  if (!followerName) return;

  await notify(
    followedId,
    `follow_${followerId}`,
    { type: 'follow', actorId: followerId, actorName: followerName },
    'Nuevo seguidor',
    `${followerName} ha comenzado a seguirte`
  );
});

// ---------------------------------------------------------------------------------------------
// Integridad: la copia del autor y del producto de una reseña nueva sale de la base de datos,
// no de lo que envió el cliente. También asegura que likedBy empiece vacío.
// ---------------------------------------------------------------------------------------------
exports.onReviewCreated = onDocumentCreated('reviews/{reviewId}', async (event) => {
  const review = event.data?.data();
  if (!review) return;

  const [userSnap, productSnap] = await Promise.all([
    db.collection('users').doc(review.userId || '-').get(),
    db.collection('products').doc(review.productId || '-').get(),
  ]);
  if (!userSnap.exists || !productSnap.exists) {
    logger.warn(`Reseña ${event.params.reviewId} con autor o producto inexistente`);
    return;
  }

  const user = userSnap.data();
  const fixed = {
    user: {
      name: user.name || '',
      username: user.username || '',
      profileImage: user.profileImage || null,
    },
    productName: productSnap.data().name || '',
  };
  if (!Array.isArray(review.likedBy)) fixed.likedBy = [];

  const sent = review.user || {};
  const differs =
    ['name', 'username', 'profileImage'].some((k) => (sent[k] ?? null) !== fixed.user[k]) ||
    fixed.productName !== review.productName ||
    fixed.likedBy !== undefined;
  if (differs) await event.data.ref.update(fixed);
});

// ---------------------------------------------------------------------------------------------
// Integridad: si el usuario cambia nombre, username o foto, se actualiza la copia en sus reseñas.
// ---------------------------------------------------------------------------------------------
exports.onUserProfileUpdated = onDocumentUpdated('users/{userId}', async (event) => {
  const before = event.data.before.data() || {};
  const after = event.data.after.data() || {};
  const changed = ['name', 'username', 'profileImage'].some((k) => (before[k] ?? null) !== (after[k] ?? null));
  if (!changed) return;

  const author = {
    name: after.name || '',
    username: after.username || '',
    profileImage: after.profileImage || null,
  };
  const reviews = await db.collection('reviews').where('userId', '==', event.params.userId).get();

  for (let i = 0; i < reviews.docs.length; i += BATCH_LIMIT) {
    const batch = db.batch();
    reviews.docs.slice(i, i + BATCH_LIMIT).forEach((doc) => batch.update(doc.ref, { user: author }));
    await batch.commit();
  }
  logger.info(`Autor sincronizado en ${reviews.size} reseñas de ${event.params.userId}`);
});
