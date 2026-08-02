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

## Profile photos

Stored in their own `person_photos` collection (not on the `Person` document itself,
so listing/searching persons never drags binary image data along). Endpoints:
`GET/PUT/DELETE /person/{id}/photo`, `PUT` expects a multipart `file` field. Max upload
size is 10MB (`spring.servlet.multipart.max-file-size`).

A person can optionally be linked to an Immich person via `Person.immichPersonId`.
This is a one-time import source, not a live link: `POST /person/{id}/photo/import-from-immich`
fetches that Immich person's current thumbnail once and saves it into `person_photos`
like a normal upload. After that the app has its own copy and no longer needs Immich for
that photo - the link only matters again if you want to re-import.

## Backups

`./scripts/dump-mongo.sh [output-dir]` dumps the database both ways: native BSON via
`mongodump` (restorable with `mongorestore`, preserves types) and per-collection JSON via
`mongoexport` (human-readable, easy to inspect/diff). It reads the same `MONGO_*` env vars
as the app. Requires the
[MongoDB Database Tools](https://www.mongodb.com/try/download/database-tools) and
[mongosh](https://www.mongodb.com/try/download/shell) on `PATH`.

```bash
MONGO_USERNAME=grigore MONGO_PASSWORD=your-password ./scripts/dump-mongo.sh
```

Defaults to `backups/<timestamp>/` if no output directory is given.

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
