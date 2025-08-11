#!/bin/bash

# =================================================================
#            Game Server - Custom Startup Script (with PID file)
# =================================================================

set -e

# 定义 PID 文件路径
PID_FILE="server.pid"

# 检查服务是否已在运行
if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    # 使用 kill -0 检查进程是否存在
    if kill -0 "$PID" 2>/dev/null; then
        echo "Server is already running with PID: $PID. Aborting."
        exit 1
    else
        # 进程不存在，但 PID 文件还在，可能是上次未正常关闭
        echo "Warning: Stale PID file found. Removing it."
        rm -f "$PID_FILE"
    fi
fi

echo "[+] Forcing use of Java 21..."
export JAVA_HOME="/root/.sdkman/candidates/java/21.0.8-amzn"

echo "[+] Configuring JVM options..."
export JAVA_OPTS="-server -Xms2G -Xmx2G -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=logs/heapdump_%p.hprof -Xlog:gc*:file=logs/gc.log:time,level,tags:filecount=10,filesize=50m -Djava.awt.headless=true -Dfile.encoding=UTF-8"

if [ ! -d "logs" ]; then
    echo "[+] Creating 'logs' directory..."
    mkdir -p logs
fi

echo "================================================================="
echo "                Starting the application in background..."
echo "================================================================="

# 使用 nohup 在后台运行，并将输出重定向
nohup ./bin/game-server com.game.MainVerticle > /dev/null 2>&1 &

# 获取最后一个后台进程的 PID
PID=$!

# 将 PID 写入文件
echo "$PID" > "$PID_FILE"

echo "Server started successfully with PID: $PID"
echo "Logs are being written to logs/output.log"

exit 0
