package com.game.server.handler;

import com.game.core.code.ResponseCode;
import com.game.core.utils.ResponseUtil;
import com.game.server.entity.Player;
import com.game.server.manager.PlayerManager;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.router.RouterBuilder;
import io.vertx.openapi.validation.ValidatedRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 玩家路由处理器
 * 直接处理HTTP请求和响应，调用Manager层（异步）
 */
public class PlayerHandler {
  private static final Logger logger = LogManager.getLogger(PlayerHandler.class);
  private final PlayerManager playerManager = new PlayerManager();

  /**
   * 处理获取玩家信息的请求（异步）。
   * @param ctx RoutingContext
   */
  public void handleGetPlayerById(RoutingContext ctx) {
    String playerId = ctx.pathParam("playerId");

    playerManager.getPlayer(playerId)
      .onSuccess(playerOptional -> {
        if (playerOptional.isPresent()) {
          JsonObject jsonObject = new JsonObject().put("player", playerOptional.get());
          ResponseUtil.sendSuccess(ctx, jsonObject);
        } else {
          ResponseUtil.sendError(ctx, ResponseCode.NOT_FOUND, "Player not found");
        }
      })
      .onFailure(err -> {
        logger.error("Error processing getPlayerById for playerId: {}", playerId, err);
        ResponseUtil.sendError(ctx, ResponseCode.INTERNAL_SERVER_ERROR, "An internal error occurred.");
      });
  }

  /**
   * 处理保存玩家信息（异步）。
   */
  public void handleSavePlayer(RoutingContext ctx) {
    JsonObject body = ctx.get("jsonBody");
    if (null == body) {
      ResponseUtil.sendError(ctx, 400, "Invalid or missing JSON body");
      return;
    }

    String id = body.getString("id");
    String name = body.getString("name");
    Integer level = body.getInteger("level");
    if (id == null || name == null || level == null) {
      ResponseUtil.sendError(ctx, 400, "Missing required fields: id, name, level");
      return;
    }

    Player player = new Player(id, name, level);
    playerManager.savePlayer(player)
      .onSuccess(v -> ResponseUtil.sendSuccess(ctx, new JsonObject().put("saved", true)))
      .onFailure(err -> {
        logger.error("Failed to save player {}", id, err);
        ResponseUtil.sendError(ctx, ResponseCode.INTERNAL_SERVER_ERROR, "Failed to save player");
      });
  }
}
