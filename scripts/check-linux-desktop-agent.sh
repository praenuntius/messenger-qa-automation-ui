#!/usr/bin/env bash
# Проверка готовности Linux-агента для desktop CI (label linux-desktop).
#
# Запуск на Linux ВМ (той же, где web/Selenoid):
#   ./scripts/check-linux-desktop-agent.sh

set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

ok=0
fail=0

check() {
  local name="$1"
  shift
  if "$@" >/dev/null 2>&1; then
    echo -e "${GREEN}OK${NC}  $name"
    ok=$((ok + 1))
  else
    echo -e "${RED}FAIL${NC}  $name"
    fail=$((fail + 1))
  fi
}

echo "=== Linux desktop agent checklist (label: linux-desktop) ==="
echo

check "Java 17+" bash -c 'java -version 2>&1 | grep -qE "version \"(17|18|19|2[0-9])"'
check "Maven" command -v mvn
check "Git" command -v git
check "curl" command -v curl
check "Xvfb" command -v Xvfb
check "allurectl" command -v allurectl
check "locale ru_RU.UTF-8" bash -c 'locale -a | grep -qiE "^ru_RU(\\.(utf8|UTF-8))?$"'

echo
echo "=== Network ==="
check "Allure TestOps" curl -sf --max-time 10 https://testops.domain.net
check "S3 preprod AppImage (desktop builds)" curl -sf --max-time 15 -I \
  https://s3-preprod.domain.net/fileupload/standalone/tdm.latest.AppImage

echo
echo "=== Summary ==="
echo "Passed: $ok, Failed: $fail"
echo
if [[ $fail -gt 0 ]]; then
  echo "Установка недостающего (Ubuntu):"
  echo "  sudo apt update && sudo apt install -y openjdk-17-jdk maven xvfb curl git locales"
  echo "  sudo locale-gen ru_RU.UTF-8"
  echo "  allurectl — https://github.com/allure-framework/allurectl/releases"
  exit 1
fi

echo "Агент готов. Добавьте label linux-desktop в Jenkins → Nodes."
