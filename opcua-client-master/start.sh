#!/bin/bash

echo "正在启动 OPC UA 客户端..."

# 检查Java版本
java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$java_version" -lt "8" ]; then
    echo "错误: 需要Java 8或更高版本"
    exit 1
fi

# 检查Maven是否安装
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven，请先安装Maven"
    exit 1
fi

# 清理并编译项目
echo "正在编译项目..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "编译失败，请检查错误信息"
    exit 1
fi

# 启动应用
echo "正在启动应用..."
echo "应用将在 http://localhost:8080 启动"
echo "按 Ctrl+C 停止应用"
echo ""

mvn spring-boot:run 