package com.game.core.router;

import com.game.server.router.RouteManager;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.openapi.router.RequestExtractor;
import io.vertx.ext.web.openapi.router.RouterBuilder;
import io.vertx.openapi.contract.OpenAPIContract;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 路由工厂类（单例模式）
 * 负责创建和配置OpenAPI路由
 * 确保路由只加载一次，避免重复创建
 */
public class RouterFactory {
    private static final Logger logger = LogManager.getLogger(RouterFactory.class);

    private static volatile RouterFactory instance;
    private static volatile Future<Router> cachedRouterFuture;
    private static final Object lock = new Object();

    private final Vertx vertx;

    private RouterFactory(Vertx vertx) {
        this.vertx = vertx;
    }

    public static RouterFactory getInstance(Vertx vertx) {
        if (null == instance) {
            synchronized (lock) {
                if (null == instance) {
                    instance = new RouterFactory(vertx);
                }
            }
        }
        return instance;
    }

    public Future<Router> createRouter() {
        if (null != cachedRouterFuture) {
            logger.info("Returning cached router future");
            return cachedRouterFuture;
        }

        synchronized (lock) {
            if (null != cachedRouterFuture) {
                logger.info("Returning cached router future (double-check)");
                return cachedRouterFuture;
            }

            logger.info("Creating new router instance");
            cachedRouterFuture = createRouterInternal();
            return cachedRouterFuture;
        }
    }

    private Future<Router> createRouterInternal() {
        RouteManager routeManager = new RouteManager();
        Router mainRouter = Router.router(vertx);
//        mainRouter.route().handler(BodyHandler.create());
        logger.info("Global BodyHandler has been added to the main router.");

        return scanOpenApiFiles()
                .compose(openApiFiles -> {
                    List<Future<Router>> routerFutures = new ArrayList<>();

                    for (String fileName : openApiFiles) {
                        Future<Router> routerFuture = createRouterFromFile(fileName, routeManager)
                                .recover(throwable -> {
                                    logger.warn("Failed to create router from file: " + fileName, throwable);
                                    return Future.succeededFuture(Router.router(vertx));
                                });
                        routerFutures.add(routerFuture);
                    }

                    return Future.all(routerFutures)
                            .compose(compositeFuture -> {
                                for (int i = 0; i < routerFutures.size(); i++) {
                                    Router subRouter = compositeFuture.resultAt(i);
                                    if (null != subRouter) {
                                        mainRouter.route("/*").subRouter(subRouter);
                                        logger.info("Mounted router for file: " + openApiFiles.get(i));
                                    }
                                }

                                logger.info("All OpenAPI routers created and mounted successfully. Total files: " + openApiFiles.size());
                                return Future.succeededFuture(mainRouter);
                            });
                })
                .recover(throwable -> {
                    logger.error("Failed to create OpenAPI routers, falling back to basic router", throwable);
                    Router fallbackRouter = Router.router(vertx);
                    routeManager.registerBasicRoutes(fallbackRouter);
                    return Future.succeededFuture(fallbackRouter);
                });
    }

    private Future<Router> createRouterFromFile(String fileName, RouteManager routeManager) {
        String classpathPath = "openapi/" + fileName;

        return OpenAPIContract.from(vertx, classpathPath)
                .compose(contract -> {
                    RouterBuilder routerBuilder = RouterBuilder.create(vertx, contract);
                    routeManager.registerRoutesForFile(routerBuilder, fileName);
                    return Future.succeededFuture(routerBuilder.createRouter());
                })
                .recover(throwable -> {
                    logger.error("Failed to create OpenAPI contract from file: " + classpathPath, throwable);
                    Router router = Router.router(vertx);
                    routeManager.registerBasicRoutes(router);
                    return Future.succeededFuture(router);
                });
    }

    private Future<List<String>> scanOpenApiFiles() {
        FileSystem fs = vertx.fileSystem();
        return fs.exists("openapi")
                .compose(exists -> {
                    if (!exists) {
                        return Future.failedFuture(new RuntimeException("OpenAPI directory 'openapi' not found"));
                    }
                    return fs.readDir("openapi")
                            .compose(files -> {
                                List<String> jsonFiles = files.stream()
                                        .filter(file -> file.toLowerCase(Locale.ROOT).endsWith(".json"))
                                        .map(file -> {
                                            String name = file;
                                            int slash = Math.max(file.lastIndexOf('/'), file.lastIndexOf('\\'));
                                            if (slash >= 0) {
                                                name = file.substring(slash + 1);
                                            }
                                            return name;
                                        })
                                        .collect(Collectors.toList());

                                logger.info("Found {} OpenAPI JSON files: {}", jsonFiles.size(), jsonFiles);
                                return Future.succeededFuture(jsonFiles);
                            });
                });
    }
}
