package com.game.server.router.player;

import com.game.core.web.OpenApiBodyHandler;
import com.game.server.handler.PlayerHandler;
import com.game.server.router.inter.RouteRegistrar;
import io.vertx.ext.web.openapi.router.RouterBuilder;

/**
 * 玩家路由注册器
 * 专门管理玩家相关的路由
 */
public class PlayerRouteRegistrar implements RouteRegistrar {

    private final PlayerHandler playerHandler;

    public PlayerRouteRegistrar() {
        this.playerHandler = new PlayerHandler();
    }

    @Override
    public void registerRoutes(RouterBuilder routerBuilder) {
      routerBuilder.getRoute("getPlayerById")
        .addHandler(playerHandler::handleGetPlayerById);

      /******当上传参数为json时,需要添加 .addHandler(OpenApiBodyHandler.create("jsonBody")) *****/
      routerBuilder.getRoute("savePlayer")
        .addHandler(OpenApiBodyHandler.create("jsonBody"))
        .addHandler(playerHandler::handleSavePlayer);
    }
}
