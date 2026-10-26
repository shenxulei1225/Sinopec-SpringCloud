#!/bin/bash

echo "测试不同的节点ID格式..."

# 测试一些常见的节点ID格式
declare -a nodeIds=(
    "ns=2;s=test"
    "ns=2;i=1"
    "ns=2;i=2"
    "ns=2;i=3"
    "ns=2;s=TEST"
    "ns=2;s=Test"
    "ns=2;s=test-1"
    "ns=2;s=Test1"
    "ns=2;s=Int1"
    "ns=2;s=Int2"
    "ns=2;s=Counter1"
    "ns=2;s=Tag1"
    "ns=2;s=Tag2"
    "ns=2;s=Value1"
    "ns=2;s=Value2"
)

for nodeId in "${nodeIds[@]}"; do
    echo "测试节点: $nodeId"
    encoded_nodeId=$(echo "$nodeId" | sed 's/=/\%3D/g' | sed 's/;/\%3B/g')
    response=$(curl -s -X GET "http://localhost:8081/api/opcua/read/$encoded_nodeId")
    
    # 检查是否成功
    if echo "$response" | grep -q '"success":true'; then
        value=$(echo "$response" | grep -o '"value":[^,]*' | cut -d':' -f2)
        echo "  ✓ 成功 - 值: $value"
    else
        echo "  ✗ 失败"
    fi
    echo ""
done

echo "测试完成！" 