#!/bin/bash

# =================================================================
#            Game Server - Stop Script (with PID file)
# =================================================================

# 定义 PID 文件路径
PID_FILE="server.pid"

# 检查 PID 文件是否存在
if [ ! -f "$PID_FILE" ]; then
    echo "Server is not running (PID file not found)."
    exit 1
fi

# 读取 PID
PID=$(cat "$PID_FILE")

# 检查 PID 是否为空或无效
if [ -z "$PID" ]; then
    echo "PID file is empty. Cannot stop the server."
    exit 1
fi

echo "Found server process with PID: $PID"
echo "Attempting to gracefully stop the server..."

# 发送 SIGTERM 信号
kill "$PID"

# 等待最多 30 秒让其优雅关闭
echo "Waiting for server to shut down..."
for i in {1..30}; do
    if ! kill -0 "$PID" 2>/dev/null; then
        echo "Server successfully stopped."
        rm -f "$PID_FILE" # 成功关闭后删除PID文件
        exit 0
    fi
    echo -n "." # 打印一个点表示正在等待
    sleep 1
done

# 如果超时仍未关闭，则强制关闭
echo "" # 换行
echo "Server did not stop gracefully after 30 seconds. Forcing shutdown..."
kill -9 "$PID"

# 清理工作
rm -f "$PID_FILE"
echo "Server forcefully stopped. PID file removed."

exit 0
