package com.game.server;

import com.game.MainVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

@ExtendWith(VertxExtension.class)
public class MainVerticleTest {

  // This method will run before each @Test method
  @BeforeEach
  void deploy_main_verticle(Vertx vertx, VertxTestContext testContext) {
    // 2. 调用返回 Future 的 deployVerticle 方法
    Future<String> deploymentFuture = vertx.deployVerticle(new MainVerticle(), new DeploymentOptions());

    // 3. 在返回的 Future 上注册我们的测试上下文处理器
    //    onComplete 会在 Future 完成时 (无论成功或失败) 被调用
    deploymentFuture.onComplete(testContext.succeedingThenComplete());
  }

  // This is our "run" test. It doesn't do much, but it keeps the app running.
  @Test
  void application_starts_and_runs(Vertx vertx, VertxTestContext testContext) throws Throwable {
    // This test will "succeed" after 30 minutes, effectively keeping the server running
    // for you to test manually (e.g., with Postman or a browser).
    // You can adjust the time as needed.
    System.out.println("✅ Application running in test mode. It will stay alive for 30 minutes.");
    System.out.println("   You can now send requests to your server.");

    // We use awaitCompletion to block the test from exiting, keeping the app alive.
    // This is ONLY for manual testing. Real automated tests would not do this.
    testContext.awaitCompletion(30, TimeUnit.MINUTES);
  }
}
