package com.game.server.verticle;

import com.game.core.redis.RedisManager;
import com.game.core.router.RouterFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HTTP服务器Verticle
 * 负责启动HTTP服务器和路由配置
 */
public class HttpServerVerticle extends AbstractVerticle {
  private static final Logger logger = LogManager.getLogger(HttpServerVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) {
    try {
      // 1. 初始化 Redis (使用合并后的config)
      RedisManager.initialize(vertx, config()); // RedisManager现在直接接收整个配置对象

      // 2. 创建 OpenAPI 路由 (使用路由工厂单例)
      RouterFactory routerFactory = RouterFactory.getInstance(vertx);

      routerFactory.createRouter()
        .onSuccess(router -> {
          logger.info("OpenAPI router created successfully.");

          // 3. 创建并启动 HTTP 服务器 (根据新的配置结构获取端口)
          int port = config().getJsonObject("http").getInteger("port", 8080);
          vertx.createHttpServer()
            .requestHandler(router)
            .listen(port)
            .onSuccess(server -> {
              logger.warn("HTTP server started on port {}", server.actualPort());
              startPromise.complete();
            })
            .onFailure(err -> {
              logger.error("Failed to start HTTP server", err);
              startPromise.fail(err);
            });
        })
        .onFailure(err -> {
          logger.error("Failed to create router", err);
          startPromise.fail(err);
        });

    } catch (Exception e) {
      logger.error("Failed to start HttpServerVerticle", e);
      startPromise.fail(e);
    }
  }


}
