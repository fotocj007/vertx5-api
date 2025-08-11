@echo off
setlocal

echo =================================================================
echo            Game Server - Custom Startup Script
echo =================================================================

:: -----------------------------------------------------------------
:: 步骤 1: 强制指定 Java 21 的路径
:: -----------------------------------------------------------------
echo [+] Forcing use of Java 21...
set "JAVA_HOME=C:\Users\Betta\.jdks\corretto-21.0.8"
echo    JAVA_HOME is set to: %JAVA_HOME%


:: -----------------------------------------------------------------
:: 步骤 2: 配置 JVM 运行参数
:: -----------------------------------------------------------------
echo [+] Configuring JVM options...
set "JAVA_OPTS=-server -Xms2G -Xmx2G -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=logs/heapdump_%%p.hprof -Xlog:gc*:file=logs/gc.log:time,level,tags:filecount=10,filesize=50m -Djava.awt.headless=true -Dfile.encoding=UTF-8"
echo    JAVA_OPTS are set.


:: -----------------------------------------------------------------
:: 步骤 3: 准备运行环境 (创建日志目录)
:: -----------------------------------------------------------------
if not exist "logs" (
    echo [+] Creating 'logs' directory for heap dumps and GC logs...
    mkdir logs
)


:: -----------------------------------------------------------------
:: 步骤 4: 调用官方脚本启动应用 (已修正: 去掉 'run' 命令)
:: -----------------------------------------------------------------
echo.
echo =================================================================
echo                 Starting the application...
echo =================================================================
call .\bin\game-server.bat com.game.MainVerticle


echo.
echo =================================================================
echo            Server process has terminated.
echo =================================================================
pause
endlocal