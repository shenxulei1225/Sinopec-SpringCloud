#!/usr/bin/env bash
# 直接改 system_user_role 不会触发 Spring @CacheEvict；需清 Redis 中 user_role_ids:{userId}
set -euo pipefail

ZHGL_USER_ID="${ZHGL_USER_ID:-200}"
REDIS_HOST="${REDIS_HOST:-127.0.0.1}"
REDIS_PORT="${REDIS_PORT:-6379}"
REDIS_PASSWORD="${REDIS_PASSWORD:-123456}"

if ! command -v redis-cli >/dev/null 2>&1; then
  echo "skip: redis-cli not found (clear key user_role_ids:${ZHGL_USER_ID} manually after role SQL)"
  exit 0
fi

KEY="user_role_ids:${ZHGL_USER_ID}"
if redis-cli -h "$REDIS_HOST" -p "$REDIS_PORT" -a "$REDIS_PASSWORD" DEL "$KEY" >/dev/null 2>&1; then
  echo ">> redis DEL ${KEY} (zhgl 角色缓存已清)"
else
  echo "warn: redis DEL ${KEY} failed; 请重启 system 或 redis-cli DEL ${KEY}"
fi
