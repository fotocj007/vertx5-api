# 配置文件说明

本目录包含了应用的所有外部配置文件，实现了配置与代码的分离。

## 文件结构

```
configs/
├── servers.json          # 服务器配置（端口等）
├── db/
│   └── redis.json        # Redis数据库配置
├── logging.json          # 当前使用的日志配置
├── logging-dev.json      # 开发环境日志配置
├── logging-prod.json     # 生产环境日志配置
└── README.md            # 本说明文件
```

## 快速开始

### 1. 切换到开发环境
```bash
# Windows
..\scripts\switch-env.bat dev

# Linux/Unix
../scripts/switch-env.sh dev
```

### 2. 切换到生产环境
```bash
# Windows
..\scripts\switch-env.bat prod

# Linux/Unix
../scripts/switch-env.sh prod
```

### 3. 查看当前配置状态
```bash
# Windows
..\scripts\switch-env.bat status

# Linux/Unix
../scripts/switch-env.sh status
```

## 配置文件说明

### servers.json
服务器相关配置，包含HTTP服务器端口等设置。

### db/redis.json
Redis数据库连接配置，包含连接字符串和连接池设置。

### logging.json
当前使用的日志配置文件。**不要直接编辑此文件**，而应该：
1. 编辑对应环境的配置文件（如 `logging-dev.json`）
2. 使用切换脚本应用配置

### logging-dev.json
开发环境日志配置：
- 日志级别：debug
- 详细的调试信息
- 较小的日志文件大小

### logging-prod.json
生产环境日志配置：
- 日志级别：warn
- 只记录警告和错误
- 较大的日志文件大小和保留数量

## 自定义配置

### 创建新的环境配置
1. 复制现有配置：
   ```bash
   cp logging-prod.json logging-staging.json
   ```

2. 编辑新配置文件

3. 应用配置：
   ```bash
   cp logging-staging.json logging.json
   ```

### 日志级别说明
- **debug**: 最详细，包含所有调试信息
- **info**: 一般信息，适合测试环境
- **warn**: 警告级别，适合生产环境
- **error**: 只记录错误，最精简

## 注意事项

1. **不要将此目录打包到JAR文件中**
2. **部署时确保此目录与JAR文件在同一级别**
3. **修改配置后无需重启应用**（日志配置支持动态更新）
4. **定期检查日志文件大小，避免磁盘空间不足**

## 故障排除

### 配置文件不生效
1. 检查文件路径是否正确
2. 检查JSON格式是否有效
3. 查看应用启动日志中的配置加载信息

### 环境切换脚本无法执行
1. Windows: 确保在PowerShell或CMD中执行
2. Linux/Unix: 确保脚本有执行权限 `chmod +x ../scripts/switch-env.sh`

### 日志级别不生效
1. 检查 `logging.json` 文件内容
2. 重启应用以确保配置生效
3. 查看应用启动日志中的日志配置应用信息

## 更多信息

详细的配置说明和使用方法请参考项目根目录的 `README.md` 文件。