package com.game.core.redis;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Redis 管理器
 * 负责Redis连接池的初始化和API获取
 */
public class RedisManager {
  private static final Logger logger = LogManager.getLogger(RedisManager.class);
  private static volatile RedisAPI redisAPI;

  /**
   * 初始化Redis连接池
   *
   * @param vertx      Vertx 实例
   * @param fullConfig 完整配置对象
   */
  public static void initialize(Vertx vertx, JsonObject fullConfig) {
    if (null == redisAPI) {
      synchronized (RedisManager.class) {
        if (null == redisAPI) {
          logger.info("RedisManager initialized.");

          // 从总配置中提取 redis 部分
          JsonObject redisConfig = fullConfig.getJsonObject("redis");
          Objects.requireNonNull(redisConfig, "Redis configuration section is missing in the config file.");

          RedisOptions options = new RedisOptions()
            .setConnectionString(redisConfig.getString("uri"))
            .setMaxPoolSize(redisConfig.getJsonObject("pool", new JsonObject()).getInteger("maxSize", 16))
            .setMaxPoolWaiting(redisConfig.getJsonObject("pool", new JsonObject()).getInteger("maxWait", 32));

          Redis client = Redis.createClient(vertx, options);
          redisAPI = RedisAPI.api(client);
          logger.info("Redis connection pool initialized.");
        }
      }
    }
  }

  /**
   * 获取Redis API实例
   *
   * @return RedisAPI 实例
   */
  public static RedisAPI getApi() {
    Objects.requireNonNull(redisAPI, "RedisManager has not been initialized. Call initialize() first.");
    return redisAPI;
  }
}
