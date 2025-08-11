package com.game.core.config;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;

/**
 * 配置加载器
 * 异步读取外部配置文件并合并它们
 */
public class ConfigLoader {

    private static final String SERVERS_CONFIG_PATH = "configs/servers.json";
    private static final String REDIS_CONFIG_PATH = "configs/db/redis.json";
    private static final String LOGGING_CONFIG_PATH = "configs/logging.json";

    /**
     * 异步加载所有外部配置文件并合并它们。
     * @param vertx Vertx 实例
     * @return 一个包含合并后配置的 Future
     */
    public static Future<JsonObject> load(Vertx vertx) {
        FileSystem fs = vertx.fileSystem();

        // 异步读取各个配置文件
        Future<JsonObject> serversConfigFuture = fs.readFile(SERVERS_CONFIG_PATH)
                .map(buffer -> new JsonObject(buffer.toString()));

        Future<JsonObject> redisConfigFuture = fs.readFile(REDIS_CONFIG_PATH)
                .map(buffer -> new JsonObject(buffer.toString()));

        Future<JsonObject> loggingConfigFuture = fs.readFile(LOGGING_CONFIG_PATH)
                .map(buffer -> new JsonObject(buffer.toString()));

        // 使用 Future.join 等待所有文件读取完成
        return Future.join(serversConfigFuture, redisConfigFuture, loggingConfigFuture)
                .map(compositeResult -> {
                    JsonObject finalConfig = new JsonObject();
                    // 成功后合并所有配置
                    JsonObject serversConfig = compositeResult.resultAt(0);
                    JsonObject redisConfig = compositeResult.resultAt(1);
                    JsonObject loggingConfig = compositeResult.resultAt(2);
                    
                    finalConfig.mergeIn(serversConfig);
                    finalConfig.mergeIn(redisConfig);
                    finalConfig.mergeIn(loggingConfig);
                    
                    return finalConfig;
                });
    }
}