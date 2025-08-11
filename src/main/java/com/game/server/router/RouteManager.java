package com.game.server.router;

import com.game.server.router.inter.BasicRouteRegistrar;
import com.game.server.router.inter.RouteRegistrar;
import com.game.server.router.player.PlayerRouteRegistrar;
import com.game.server.router.time.TimestampRouteRegistrar;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.openapi.router.RouterBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由管理器
 * 统一管理所有路由注册器，实现模块化管理（JSON版）
 */
public class RouteManager {
  private static final Logger logger = LogManager.getLogger(RouteManager.class);

  private final List<RouteRegistrar> routeRegistrars;

  public RouteManager() {
    this.routeRegistrars = new ArrayList<>();
    initializeRouteRegistrars();
  }

  /**
   * 注册基本路由（当OpenAPI加载失败时使用）
   *
   * @param router Router实例
   */
  public void registerBasicRoutes(Router router) {
    for (RouteRegistrar registrar : routeRegistrars) {
      try {
        if (registrar instanceof BasicRouteRegistrar) {
          ((BasicRouteRegistrar) registrar).registerBasicRoutes(router);
          logger.debug("Registered basic routes for {}", registrar.getClass().getSimpleName());
        }
      } catch (Exception e) {
        logger.error("Failed to register basic routes for {}", registrar.getClass().getSimpleName(), e);
      }
    }
    logger.info("Basic routes registered successfully");
  }


  /**
   * 初始化所有路由注册器
   */
  private void initializeRouteRegistrars() {
    // 注册玩家相关路由
    routeRegistrars.add(new PlayerRouteRegistrar());

    // 注册时间戳相关路由
    routeRegistrars.add(new TimestampRouteRegistrar());

    logger.info("Initialized {} route registrars", routeRegistrars.size());
  }

  /**
   * 根据 JSON 文件名注册对应的路由
   *
   * @param routerBuilder RouterBuilder实例
   * @param fileName      OpenAPI JSON 文件名
   */
  public void registerRoutesForFile(RouterBuilder routerBuilder, String fileName) {
    RouteRegistrar targetRegistrar = null;

    switch (fileName) {
      case "player.json":
        targetRegistrar = routeRegistrars.stream()
          .filter(r -> r instanceof PlayerRouteRegistrar)
          .findFirst().orElse(null);
        break;
      case "timestamp.json":
        targetRegistrar = routeRegistrars.stream()
          .filter(r -> r instanceof TimestampRouteRegistrar)
          .findFirst().orElse(null);
        break;
      default:
        logger.error("No route registrar found for file: {}", fileName);
        return;
    }

    if (null != targetRegistrar) {
      try {
        targetRegistrar.registerRoutes(routerBuilder);
        logger.info("Registered routes for {} using {}", fileName, targetRegistrar.getClass().getSimpleName());
      } catch (Exception e) {
        logger.error("Failed to register routes for {} using {}", fileName, targetRegistrar.getClass().getSimpleName(), e);
      }
    }
  }

}
