package com.game.server.dao;

import com.game.core.redis.RedisKey;
import com.game.core.redis.RedisManager;
import com.game.server.entity.Player;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

/**
 * 玩家数据访问层
 * 封装对Redis的具体读写操作（全异步）
 */
public class PlayerDAO {
    private static final Logger logger = LogManager.getLogger(PlayerDAO.class);

    /**
     * 从Redis获取玩家信息（异步）。
     * @param playerId 玩家ID
     * @return Future<Optional<Player>>
     */
    public Future<Optional<Player>> getPlayerById(String playerId) {
        String key = RedisKey.playerInfoKey(playerId);
        return RedisManager.getApi()
                .get(key)
                .map(this::mapResponseToPlayer);
    }

    private Optional<Player> mapResponseToPlayer(Response response) {
        if (response == null) {
            return Optional.empty();
        }
        return Optional.of(new JsonObject(response.toString()).mapTo(Player.class));
    }

    /**
     * 保存玩家信息到Redis（异步）。
     * @param player 玩家信息
     * @return Future<Void>
     */
    public Future<Void> savePlayer(Player player) {
        String key = RedisKey.playerInfoKey(player.id());
        JsonObject playerJson = JsonObject.mapFrom(player);
        Future<Void> result = RedisManager.getApi()
                .set(List.of(key, playerJson.encode()))
                .mapEmpty();
        result.onSuccess(v -> logger.info("Player {} saved to Redis", player.id()));
        return result;
    }

    /**
     * 从Redis删除玩家信息（异步）。
     * @param playerId 玩家ID
     * @return Future<Void>
     */
    public Future<Void> deletePlayer(String playerId) {
        String key = RedisKey.playerInfoKey(playerId);
        Future<Void> result = RedisManager.getApi()
                .del(List.of(key))
                .mapEmpty();
        result.onSuccess(v -> logger.info("Player {} deleted from Redis", playerId));
        return result;
    }
}
