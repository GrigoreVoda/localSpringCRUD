#!/usr/bin/env bash
set -euo pipefail

# Dumps the app's MongoDB database two ways:
#   - native BSON via mongodump (restorable with mongorestore, preserves types)
#   - per-collection JSON via mongoexport (human-readable, easy to inspect/diff)
#
# Reads the same MONGO_* env vars the app itself uses, so it works against
# whatever instance the backend is currently pointed at.
#
# Usage:
#   MONGO_USERNAME=grigore MONGO_PASSWORD=secret ./scripts/dump-mongo.sh [output-dir]
#
# Requires the MongoDB Database Tools (mongodump, mongoexport) and mongosh on
# PATH:
#   https://www.mongodb.com/try/download/database-tools
#   https://www.mongodb.com/try/download/shell
#
# Note: person_photos holds binary image data - its JSON export will contain
# large base64 blobs ($binary). That's expected; the BSON dump is the more
# practical format for restoring that collection.

: "${MONGO_USERNAME:?MONGO_USERNAME is required}"
: "${MONGO_PASSWORD:?MONGO_PASSWORD is required}"
MONGO_HOST="${MONGO_HOST:-localhost}"
MONGO_PORT="${MONGO_PORT:-27017}"
MONGO_DATABASE="${MONGO_DATABASE:-grigoredb}"
MONGO_AUTH_DATABASE="${MONGO_AUTH_DATABASE:-admin}"

OUT_DIR="${1:-backups/$(date +%Y%m%d-%H%M%S)}"
mkdir -p "$OUT_DIR/bson" "$OUT_DIR/json"

CONN_ARGS=(
  --host "$MONGO_HOST"
  --port "$MONGO_PORT"
  --username "$MONGO_USERNAME"
  --password "$MONGO_PASSWORD"
  --authenticationDatabase "$MONGO_AUTH_DATABASE"
)

echo "Dumping '$MONGO_DATABASE' (BSON) to $OUT_DIR/bson ..."
mongodump "${CONN_ARGS[@]}" --db "$MONGO_DATABASE" --out "$OUT_DIR/bson"

echo "Listing collections ..."
COLLECTIONS=$(mongosh "mongodb://$MONGO_HOST:$MONGO_PORT/$MONGO_DATABASE" \
  --username "$MONGO_USERNAME" --password "$MONGO_PASSWORD" --authenticationDatabase "$MONGO_AUTH_DATABASE" \
  --quiet --eval "db.getCollectionNames().join('\n')")

echo "Dumping each collection to JSON ..."
while IFS= read -r collection; do
  [ -z "$collection" ] && continue
  echo "  - $collection"
  mongoexport "${CONN_ARGS[@]}" --db "$MONGO_DATABASE" --collection "$collection" \
    --jsonArray --pretty --out "$OUT_DIR/json/$collection.json"
done <<< "$COLLECTIONS"

echo "Done."
echo "  BSON: $OUT_DIR/bson/$MONGO_DATABASE/"
echo "  JSON: $OUT_DIR/json/*.json"
