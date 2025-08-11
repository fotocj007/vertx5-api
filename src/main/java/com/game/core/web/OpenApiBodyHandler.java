package com.game.core.web;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.router.RouterBuilder;
import io.vertx.openapi.validation.ValidatedRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * OpenAPI Body 预处理器
 * 在路由执行前，将 OpenAPI 验证过的 JSON Body 放入 RoutingContext 指定 key
 */
public class OpenApiBodyHandler implements Handler<RoutingContext> {

  private static final Logger logger = LogManager.getLogger(OpenApiBodyHandler.class);
  private final String contextKey;

  private OpenApiBodyHandler(String contextKey) {
    this.contextKey = contextKey;
  }

  public static OpenApiBodyHandler create(String contextKey) {
    return new OpenApiBodyHandler(contextKey);
  }

  @Override
  public void handle(RoutingContext ctx) {
    try {
      ValidatedRequest validated = ctx.get(RouterBuilder.KEY_META_DATA_VALIDATED_REQUEST);
      if (null != validated && null != validated.getBody()) {
        JsonObject body = validated.getBody().getJsonObject();
        if (null != body) {
          ctx.put(contextKey, body);
        }

//        Map<String, RequestParameter> headers =  validated.getHeaders();
//        if(null != headers){
//        }
      }
    } catch (Exception e) {
      logger.debug("Failed to extract OpenAPI validated body", e);
    }
    ctx.next();
  }
}
