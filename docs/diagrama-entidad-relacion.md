# Diagrama entidad-relación — ReviewLab

ReviewLab actualmente usa datos locales en memoria (`data/Local*Provider.kt`)
en lugar de una base de datos, pero el modelo de entidades ya está definido
y listo para mapearse a una base de datos (por ejemplo Room/SQLite) en un
sprint futuro.

```mermaid
erDiagram
    USER ||--o{ REVIEW : "escribe"
    PRODUCT ||--o{ REVIEW : "recibe"
    USER ||--o{ NOTIFICATION : "recibe"

    USER {
        string name
        string initials
        int memberSinceYear
        int reviewCount
        int usefulVotes
        double averageRating
    }

    PRODUCT {
        string id PK
        string name
        string category
        double rating
        int reviewCount
        int recommendPercent
    }

    REVIEW {
        string id PK
        string productId FK
        string author FK
        boolean verifiedBuyer
        int rating
        string title
        string body
        string date
        int usefulCount
        boolean recommended
        boolean hasPhoto
        string status
    }

    NOTIFICATION {
        string id PK
        boolean highlighted
        string message
        string time
    }
```

## Notas

- `REVIEW.productId` referencia a `PRODUCT.id`.
- `REVIEW.author` referencia al nombre de `USER` (hoy solo existe un usuario
  local, `LocalUserProvider.currentUser`).
- `NOTIFICATION` no está asociada a un usuario específico todavía; en la app
  actual todas las notificaciones son globales para la sesión activa.
