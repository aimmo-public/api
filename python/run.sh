#!/bin/sh

set -eux

UID="$(id -u)"
GID="$(id -g)"
IMAGE="mini-python"
SPEC="Dockerfile"
APP_DIR="/code"
PORTS=""
# ENV_FILE=".env"

NO_CACHE=""
WIPE="" 
[ -n "$*" ] && [ '--wipe' = "$1" ] && { shift 1; WIPE="true"; NO_CACHE='--no-cache'; }

# hash arguments to crete container id
CONTAINER="$IMAGE-"`echo "$*" | md5sum | cut -c1-6`

# rebuild image and container if necessary
[ "true" = "$WIPE" ] && { for n in $(docker container list --all --filter name="$IMAGE" -q); do docker container rm "$n" ; done;  docker image rm -f "$IMAGE"; }

# build image if necessary
docker image list "$IMAGE" -q | grep -q . || cat Dockerfile "$SPEC" | docker build --progress=plain --build-arg UID --build-arg GID -f - -t "$IMAGE" $NO_CACHE .

# create container if necessary
docker run --rm --interactive --tty --user "$UID:$GID" $PORTS -v "$PWD":"$APP_DIR" -w "$APP_DIR" --env KEY_ID --env KEY_SECRET --name "$CONTAINER" "$IMAGE" "$@"

