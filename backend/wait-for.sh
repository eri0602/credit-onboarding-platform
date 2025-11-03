done
#!/bin/sh
# Wait-for script that prefers nc (netcat). Usage:
#   wait-for.sh host port -- command args...
HOST="${1:-db}"
PORT="${2:-3306}"
shift 2 || true

echo "Waiting for ${HOST}:${PORT} ..."

# If nc is available use it; otherwise try bash /dev/tcp fallback.
if command -v nc >/dev/null 2>&1; then
  until nc -z "${HOST}" "${PORT}" >/dev/null 2>&1; do
    sleep 1
  done
elif command -v bash >/dev/null 2>&1; then
  # bash supports /dev/tcp
  until (echo > /dev/tcp/${HOST}/${PORT}) >/dev/null 2>&1; do
    sleep 1
  done
else
  echo "Warning: neither nc nor bash available; trying generic TCP probe (may fail)"
  while :; do
    (echo > /dev/tcp/${HOST}/${PORT}) >/dev/null 2>&1 && break || sleep 1
  done
fi

echo "${HOST}:${PORT} is available, starting command"
if [ "$1" = "--" ]; then
  shift
fi
exec "$@"
