package com.game.server.router.time;

import com.game.server.handler.TimestampHandler;
import com.game.server.router.inter.RouteRegistrar;
import io.vertx.ext.web.openapi.router.RouterBuilder;

/**
 * 时间戳路由注册器
 * 专门管理时间戳相关的路由
 */
public class TimestampRouteRegistrar implements RouteRegistrar {

    private final TimestampHandler timestampHandler;

    public TimestampRouteRegistrar() {
        this.timestampHandler = new TimestampHandler();
    }

    @Override
    public void registerRoutes(RouterBuilder routerBuilder) {
        // 时间戳相关路由

      routerBuilder.getRoute("getServerTimestamp")
        .addHandler(timestampHandler::handleGetServerTimestamp);
    }
}
