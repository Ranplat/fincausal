#!/bin/zsh

# 设置项目根目录
PROJECT_ROOT=$(dirname "$0")

# 编译项目
echo "[$(date '+%Y-%m-%d %H:%M:%S')] 开始编译项目..."
mvn clean compile -X -Dorg.slf4j.simpleLogger.defaultLogLevel=info
if [ $? -ne 0 ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] 编译失败"
    exit 1
fi
echo "[$(date '+%Y-%m-%d %H:%M:%S')] 编译成功完成"

# 运行测试
echo "[$(date '+%Y-%m-%d %H:%M:%S')] 开始运行测试..."
mvn test -X -Dorg.slf4j.simpleLogger.defaultLogLevel=info
if [ $? -ne 0 ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] 测试失败"
    exit 1
fi
echo "[$(date '+%Y-%m-%d %H:%M:%S')] 测试运行完成"

echo "测试成功完成"