#!/usr/bin/env bash
# 平台 seed / 分类树 path 修复后清理 Redis 分类树缓存。
# 直接 psql 导入不会走 CategoryServiceImpl.evictCache，否则会命中旧树（仅根节点、无 children）。
set -euo pipefail

REDIS_HOST="${REDIS_HOST:-127.0.0.1}"
REDIS_PORT="${REDIS_PORT:-6379}"
REDIS_DB="${REDIS_DB:-0}"
REDIS_PASSWORD="${REDIS_PASSWORD:-123456}"

if ! command -v redis-cli >/dev/null 2>&1; then
  echo "warn: redis-cli not found, skip category tree cache eviction"
  exit 0
fi

AUTH_ARGS=()
if [[ -n "${REDIS_PASSWORD}" ]]; then
  AUTH_ARGS=(-a "${REDIS_PASSWORD}" --no-auth-warning)
fi

PATTERN="dynamicbusiness:category:tree:*"
KEYS=$(
  redis-cli -h "${REDIS_HOST}" -p "${REDIS_PORT}" -n "${REDIS_DB}" "${AUTH_ARGS[@]}" --raw KEYS "${PATTERN}" 2>/dev/null || true
)

if [[ -z "${KEYS// /}" ]]; then
  echo ">> category tree cache: no keys (${PATTERN})"
  exit 0
fi

# shellcheck disable=SC2086
redis-cli -h "${REDIS_HOST}" -p "${REDIS_PORT}" -n "${REDIS_DB}" "${AUTH_ARGS[@]}" DEL ${KEYS} >/dev/null
KEY_COUNT=$(echo "${KEYS}" | wc -w | tr -d ' ')
echo ">> category tree cache evicted: ${KEY_COUNT} key(s)"
