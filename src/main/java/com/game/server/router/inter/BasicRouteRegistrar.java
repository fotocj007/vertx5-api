package com.game.server.router.inter;

import io.vertx.ext.web.Router;

/**
 * 基本路由注册器接口
 * 用于在OpenAPI加载失败时提供基本路由功能
 */
public interface BasicRouteRegistrar {
    /**
     * 注册基本路由
     * @param router Router实例
     */
    void registerBasicRoutes(Router router);
}
