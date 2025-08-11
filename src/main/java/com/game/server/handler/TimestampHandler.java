package com.game.server.handler;

import com.game.core.code.ResponseCode;
import com.game.core.utils.ResponseUtil;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 时间戳路由处理器
 * 处理获取服务器时间戳的请求
 */
public class TimestampHandler {
  private static final Logger logger = LogManager.getLogger(TimestampHandler.class);

  /**
   * 处理获取服务器时间戳的请求。
   * 此方法已在 HttpServerVerticle 中被包裹在 ctx.runOnVirtualThread() 中运行。
   *
   * @param ctx RoutingContext
   */
  public void handleGetServerTimestamp(RoutingContext ctx) {
    try {
      // 获取当前时间戳
      JsonObject jsonObject = new JsonObject();
      jsonObject.put("time", System.currentTimeMillis());
      logger.debug("Server timestamp requested: {}", jsonObject);
      ResponseUtil.sendSuccess(ctx, jsonObject);

    } catch (Exception e) {
      logger.error("Error processing getServerTimestamp", e);
      ResponseUtil.sendError(ctx, ResponseCode.INTERNAL_SERVER_ERROR, "An internal error occurred.");
    }
  }
}
