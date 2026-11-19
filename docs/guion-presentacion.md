# Guion de presentación — ReviewLab (pitch de 12 minutos)

**Idea central (una frase):** *«ReviewLab es la app donde las reseñas dejan de ser un muro de texto y se vuelven una conversación
de confianza entre compradores reales.»*

**Reglas de la rúbrica a recordar:** máximo 12 min · asisten todos, no hace falta que hablen todos · pitch de venta, **sin mostrar código**
(salvo un adicional técnico) · **todo adicional se muestra en vivo** · tener todo listo antes de empezar (hay penalización por retraso).

## Reparto sugerido (ajusta nombres)
| Rol | Quién | Qué hace |
|---|---|---|
| Presentador/a (voz) | Persona A | Abre, hila el pitch y cierra |
| Demo en vivo | Persona B | Maneja el celular/emulador y el PC, siempre un paso adelante |
| Técnico + adicionales | Persona C | Explica adicionales técnicos y atiende el plan B |
| Apoyo | el resto | Asisten; vigilan el reloj y tienen el video de respaldo |

## Línea de tiempo (12:00)

| Min | Bloque | Qué se dice / muestra | Rúbrica |
|---|---|---|---|
| 0:00–1:00 | **Gancho y problema** | «¿Cuántas veces compraste algo por una reseña que resultó falsa?» Problema: reseñas dispersas, falsas o sin contexto. | Presentación |
| 1:00–2:00 | **Propuesta de valor** | ReviewLab: reseñas de compradores reales, con comunidad, en tiempo real. Para quién (compradores) y para quién más (marcas que quieren escuchar). | Presentación |
| 2:00–5:30 | **Recorrido de la app (en vivo o video)** | Flujo de una persona: **Registro/Login** → **Inicio** (tendencias, categorías, búsqueda) → **Detalle de producto** (estrellas, % que recomiendan, guardar) → **Escribir reseña** (estrellas, texto, ubicación opcional) → **Reseña publicada**. Mostrar el diseño: identidad lima/tinta, modo oscuro. | UX, UI, Requisitos |
| 5:30–6:30 | **Comunidad** | Perfil, seguidores, feed «Siguiendo», likes, notificaciones. | Requisitos |
| 6:30–10:00 | **ADICIONALES — EN VIVO** (ver abajo) | Cada uno con: problema → demo → beneficio. | Adicionales |
| 10:00–11:00 | **Calidad y versión Flutter** | Pruebas automáticas, sin desbordes en teléfonos pequeños; mismas pantallas replicadas en Flutter (2 diapositivas o 30 s en el emulador). | UI, Adicionales técnicos |
| 11:00–12:00 | **Cierre** | Resumen de 3 beneficios, próximos pasos (moderación, versión iOS con Flutter), llamado a la acción y «¿preguntas?». | Presentación |

## Adicionales para mostrar en vivo (de más a menos valor de negocio)

1. **Compartir reseña** *(beneficio: crecimiento orgánico)* — ~1 min
   - Demo: abrir una reseña ▸ ícono compartir ▸ elegir WhatsApp/Mensajes ▸ mostrar el mensaje listo («Emily reseñó «Audífonos»… ★★★★☆»).
   - Frase: «Cada reseña compartida es publicidad gratis y un nuevo usuario potencial.»
2. **Reportar reseña** *(beneficio: confianza y moderación)* — ~1 min
   - Demo: con otra cuenta ▸ «Reportar reseña» ▸ motivo ▸ confirmación ▸ botón cambia a «Ya reportaste…» ▸ mostrar en la consola de Firebase el documento `reports`.
   - Frase: «Una plataforma de reseñas vive de su credibilidad; los usuarios ayudan a limpiarla.»
3. **Comentarios en tiempo real** *(beneficio: engagement)* — ~1 min
   - Demo: dos celulares/emuladores: uno comenta, el otro lo ve aparecer sin recargar; el autor recibe notificación.
4. **Mapa de reseñas de las últimas 24 h** *(beneficio: descubrimiento local)* — ~45 s
   - Demo: abrir el mapa, tocar un grupo, ver el detalle.
5. **Notificaciones push (FCM)** *(beneficio: retención)* — se puede combinar con el 3.
6. **Técnicos** *(solo si sobra tiempo; aquí sí se puede mostrar código/terminal ~45 s)*: pruebas automáticas
   (unitarias, de integración y E2E), entorno con Firebase Emulator + datos falsos con Faker, reglas de seguridad probadas
   (mostrar `npm test` con los casos de `reports` en verde).

> Mostrar **solo los adicionales que funcionen sin riesgo**. Mejor 4 bien hechos que 6 con fallas.

## Guion hablado (resumen por bloque)

**Apertura (Persona A).**
«Buenas. Todos hemos comprado algo por una reseña… y nos hemos equivocado. ReviewLab nace para que decidir una compra sea fácil y confiable: reseñas de compradores reales, con una comunidad que las valida.»

**Recorrido (A habla, B maneja).**
«Ana abre la app. Entra con su cuenta (B hace login). En Inicio ve lo que está en tendencia esta semana y filtra por categoría. Abre la grabadora de voz: ve el promedio, cuántos la recomiendan y las reseñas destacadas. Decide escribir la suya: elige estrellas, título, comentario y, si quiere, comparte su ubicación aproximada. Publica, y la ve en su perfil.»

**Comunidad.**
«No es solo leer: puedes seguir a quienes reseñan bien, dar like, comentar y recibir notificaciones. Eso convierte a los compradores en una comunidad.»

**Adicionales (A presenta, C o B demuestra).**
«Ahora lo que nos diferencia — y lo mostramos en vivo.» (Usar cada fichero de arriba: problema → demo → beneficio.)

**Cierre.**
«ReviewLab reduce la incertidumbre al comprar, crece con cada reseña compartida y se protege con la ayuda de su propia comunidad. Ya tenemos la app Android completa y su interfaz replicada en Flutter, lista para llegar a iOS. Gracias — ¿preguntas?»

## Checklist previo (30 min antes)
- [ ] PC con la app funcionando, cable HDMI y adaptador probados; pantalla duplicada
- [ ] Emulador/celular cargado, **modo No molestar** activado, sin notificaciones personales
- [ ] Internet probado (o datos móviles). Plan B si falla la red: **video grabado** del recorrido
- [ ] Dos cuentas listas (A: Ana, B: otra persona) ya sembradas con datos (usar el *Seeder* debug o Faker)
- [ ] Reglas de Firestore desplegadas (si no, Reportar falla en producción)
- [ ] Una reseña ajena ya lista para reportar y otra con autor de nombre largo
- [ ] Segundo dispositivo para mostrar comentarios en tiempo real
- [ ] Cronómetro visible (12 min) y alguien que avise al minuto 10
- [ ] Diapositivas cortas (máx. 6) sin código: problema, solución, recorrido, adicionales, calidad, cierre
- [ ] Todos los integrantes presentes

## Si algo falla
- **No hay internet:** reproducir el video del recorrido; los adicionales que dependen de Firebase se muestran con el emulador local.
- **Un adicional falla en vivo:** decirlo en una frase («en el video lo vemos funcionando»), mostrar el video de 20 s de ese adicional y seguir. Cada adicional debería tener un clip de respaldo.
- **Te pasas de tiempo:** recortar «Comunidad» y «Calidad»; no recortar los adicionales (son 10 puntos).

## Posibles preguntas
- *¿Cómo evitan reseñas falsas?* Comprador verificado, reportes de la comunidad y reglas de seguridad (un reporte por usuario, no se puede reportar lo propio).
- *¿Cómo ganan dinero?* Marcas que quieren ver y responder reseñas, destacados y analítica (hacia dónde crece).
- *¿Por qué Flutter?* Para llegar a iOS reutilizando el diseño; ya replicamos 6 pantallas.
- *¿Qué sigue?* Panel de moderación, respuestas de marcas, buscador por voz/foto.
