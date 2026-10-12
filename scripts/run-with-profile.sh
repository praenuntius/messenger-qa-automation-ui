#!/usr/bin/env bash
# Запуск Maven-тестов с профилем из config/run-profiles/
#
# Usage:
#   ./scripts/run-with-profile.sh <profile-id> [extra mvn args...]
#
# Examples:
#   ./scripts/run-with-profile.sh web-selenoid-ci
#   ./scripts/run-with-profile.sh desktop-web-mac-local -Dtest=OwnerTest
#   ./scripts/run-with-profile.sh desktop-web-linux-ci -Dtest=OwnerTest#createHomespaceChannelWithReactionsDisabled

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROFILE_ID="${1:?Укажите profile id, например: web-selenoid-ci или desktop-web-linux-ci}"
shift

PROFILE_FILE="${REPO_ROOT}/config/run-profiles/${PROFILE_ID}.properties"
if [[ ! -f "${PROFILE_FILE}" ]]; then
  echo "Профиль не найден: ${PROFILE_FILE}" >&2
  echo "Доступные профили:" >&2
  ls -1 "${REPO_ROOT}/config/run-profiles/"*.properties 2>/dev/null | xargs -n1 basename | sed 's/.properties$//' >&2
  exit 1
fi

MODULE="web-tests"
MVN_PROPS=()

while IFS= read -r line || [[ -n "$line" ]]; do
  line="${line%%#*}"
  line="$(echo "$line" | xargs)"
  [[ -z "$line" ]] && continue
  key="${line%%=*}"
  value="${line#*=}"
  if [[ "$key" == profile.module ]]; then
    MODULE="$value"
    continue
  fi
  if [[ "$key" == profile.* ]] || [[ "$key" == maven.* ]]; then
    continue
  fi
  MVN_PROPS+=("-D${key}=${value}")
done < "${PROFILE_FILE}"

echo "==> Профиль: ${PROFILE_ID}"
echo "==> Модуль:  ${MODULE}"
echo "==> Файл:    ${PROFILE_FILE}"
echo "==> Maven:   mvn -pl ${MODULE} -am test ${MVN_PROPS[*]} $*"
echo

cd "${REPO_ROOT}"
exec mvn -pl "${MODULE}" -am test "${MVN_PROPS[@]}" "$@"
