package com.game.server.router.inter;

import io.vertx.ext.web.openapi.router.RouterBuilder;

/**
 * 路由注册器接口
 * 用于统一管理路由注册，实现模块化管理
 */
public interface RouteRegistrar {

    /**
     * 注册路由
     * @param routerBuilder RouterBuilder实例
     */
    void registerRoutes(RouterBuilder routerBuilder);
}
