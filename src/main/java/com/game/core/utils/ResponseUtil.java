package com.game.core.utils;

import com.game.core.code.ResponseCode;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * 响应工具类
 * 封装统一的JSON响应格式
 */
public class ResponseUtil {

  /**
   * 发送成功响应
   *
   * @param ctx  路由上下文
   * @param data 响应数据
   */
  public static void sendSuccess(RoutingContext ctx, JsonObject data) {
    JsonObject responseBody = new JsonObject()
      .put("code", ResponseCode.OK)
      .put("message", "success")
      .put("data", null != data ? data : new JsonObject());

    sendJsonResponse(ctx, ResponseCode.OK, responseBody);
  }

  /**
   * 发送错误响应
   *
   * @param ctx        路由上下文
   * @param statusCode 状态码
   * @param message    错误消息
   */
  public static void sendError(RoutingContext ctx, int statusCode, String message) {
    JsonObject responseBody = new JsonObject()
      .put("code", statusCode)
      .put("message", message);

    sendJsonResponse(ctx, statusCode, responseBody);
  }

  /**
   * 发送JSON响应
   *
   * @param ctx        路由上下文
   * @param statusCode 状态码
   * @param body       响应体
   */
  private static void sendJsonResponse(RoutingContext ctx, int statusCode, JsonObject body) {
    HttpServerResponse response = ctx.response();
    if (!response.ended()) {
      response.setStatusCode(statusCode)
        .putHeader("Content-Type", "application/json; charset=utf-8")
        .end(body.encode());
    }
  }
}
