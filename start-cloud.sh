#!/bin/bash
# 本云服务器的启动入口。
# Nacos 安装在 /root/nacos，Java 使用系统 JDK 17。
# Maven 本地仓库沿用 start-microservices.sh：~/MavenRepositoy。
# 用法与 start-microservices.sh 相同，另加 nacos 子命令：
#   ./start-cloud.sh nacos          # 启动 Nacos
#   ./start-cloud.sh nacos status
#   ./start-cloud.sh base|biz|bmp|all

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="${JAVA_HOME}/bin:${PATH}"
export NACOS_HOME=/root/nacos
export MAVEN_REPO_LOCAL="${HOME}/MavenRepositoy"
mkdir -p "${MAVEN_REPO_LOCAL}"

if [[ ! -x "${JAVA_HOME}/bin/java" ]]; then
    echo "未找到 Java: ${JAVA_HOME}/bin/java" >&2
    exit 1
fi
if [[ ! -f "${NACOS_HOME}/bin/startup.sh" ]]; then
    echo "未找到 Nacos 启动脚本: ${NACOS_HOME}/bin/startup.sh" >&2
    exit 1
fi

usage() {
    cat <<EOF
云服务器启动脚本
  Java:  ${JAVA_HOME}
  Nacos: ${NACOS_HOME}  (127.0.0.1:8848，命名空间 dev)
  Maven: ${MAVEN_REPO_LOCAL}

  ./start-cloud.sh nacos [status|stop|logs|-f]
  ./start-cloud.sh base
  ./start-cloud.sh biz
  ./start-cloud.sh bmp
  ./start-cloud.sh all
  ./start-cloud.sh status
EOF
}

cmd="${1:-}"
case "${cmd}" in
    ""|help|-h|--help)
        usage
        ;;
    nacos)
        shift
        exec "${SCRIPT_DIR}/start-nacos.sh" "$@"
        ;;
    *)
        exec "${SCRIPT_DIR}/start-microservices.sh" "$@"
        ;;
esac
