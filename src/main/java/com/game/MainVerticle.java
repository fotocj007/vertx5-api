package com.game;

import com.game.core.config.ConfigLoader;
import com.game.core.config.LoggingConfigManager;
import com.game.server.verticle.HttpServerVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.VertxOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 程序主入口 (作为 Verticle)
 * 负责引导和部署
 */
// 3. 继承 AbstractVerticle
public class MainVerticle extends AbstractVerticle {
  private static final Logger logger = LogManager.getLogger(MainVerticle.class);

  // 4. 移除 main 方法，使用 start 方法作为入口
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");

    // 使用异步方式加载配置
    // 5. 不再需要手动创建 Vertx 实例，直接使用 this.vertx 或 vertx
    ConfigLoader.load(vertx)
      .onSuccess(config -> {
        // 首先应用日志配置
        LoggingConfigManager.applyLoggingConfig(config);

        logger.info("External configurations loaded successfully.");
        logger.debug("Loaded config: {}", config.encodePrettily());

        String verticleBlueprint = HttpServerVerticle.class.getName();
        // 6. 这里的 DeploymentOptions 不再需要 setConfig，因为 config 会被传递给子 Verticle
        //    不过为了清晰，也可以保留。更好的方式是通过 Vert.x 的配置传递。
        //    这里的 config() 方法就是从启动器传递过来的配置。
        DeploymentOptions options = new DeploymentOptions()
          .setConfig(config)
          .setInstances(VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE);

        vertx.deployVerticle(verticleBlueprint, options)
          .onSuccess(id -> {
            logger.info("HttpServerVerticle deployed successfully with ID: {}", id);
            startPromise.complete(); // 7. 部署成功，通知 Vert.x
          })
          .onFailure(err -> {
            logger.error("Failed to deploy HttpServerVerticle", err);
            startPromise.fail(err); // 8. 部署失败，通知 Vert.x
          });
      })
      .onFailure(err -> {
        logger.error("Failed to initialize the application", err);
        startPromise.fail(err); // 9. 初始化失败，通知 Vert.x
      });
  }
}
