# localSpringCRUD backend

Spring Boot + MongoDB backend for the contact/relations/events/documents management app.

## Required environment variables

MongoDB credentials are **required** and have no default, so the app fails fast on startup
instead of silently connecting with the wrong identity:

| Variable | Required | Default | Purpose |
|---|---|---|---|
| `MONGO_USERNAME` | yes | - | Mongo auth username |
| `MONGO_PASSWORD` | yes | - | Mongo auth password |
| `MONGO_HOST` | no | `localhost` | Mongo host |
| `MONGO_PORT` | no | `27017` | Mongo port |
| `MONGO_DATABASE` | no | `grigoredb` | Database name |
| `MONGO_AUTH_DATABASE` | no | `admin` | Auth database |
| `SERVER_PORT` | no | `8080` | HTTP port |
| `LOG_FILE` | no | `logs/springCRUD.log` | Log file path (relative, portable across OS/containers) |
| `IMMICH_BASE_URL` | no | `http://localhost:2283` | Base URL of your Immich instance |
| `IMMICH_API_KEY` | no | - | Immich API key (Immich: Account Settings > API Keys). Only needed if you use the Immich page - unset just makes those endpoints return a clear error instead of the app failing to start. |

Example local run:

```bash
MONGO_USERNAME=grigore MONGO_PASSWORD=your-password ./mvnw spring-boot:run
```

## ⚠️ Rotate your MongoDB password

The MongoDB credentials used to be committed in plaintext in `application.properties`
(now removed from this file in favor of env vars). Removing them from the current file
does **not** remove them from git history — if this repo has ever been pushed anywhere
those credentials are exposed. **Rotate the MongoDB password** for the `grigore` user
before relying on this fix for security.

## Known follow-ups (not done in this pass)

- Authentication is effectively disabled (`permitAll()` on all endpoints) even though
  Spring Security is wired up. Enabling real enforcement requires coordinated frontend
  work (the Angular app doesn't consistently attach auth headers today), so it wasn't
  changed here.
- Person/event relationship writes across the `persons` and `events` collections are
  not wrapped in a MongoDB multi-document transaction (this requires the deployment to
  run as a replica set, even a single-node one via `rs.initiate()`). The writes are now
  batched (`findAllById`/`saveAll`) to minimize the window for partial failure, but are
  not fully atomic across collections.
