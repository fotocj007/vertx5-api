package com.game.server.manager;

import com.game.server.dao.PlayerDAO;
import com.game.server.entity.Player;
import io.vertx.core.Future;

import java.util.Optional;

/**
 * 玩家业务逻辑层
 * 处理具体的业务逻辑，调用DAO层
 */
public class PlayerManager {
    private final PlayerDAO playerDAO = new PlayerDAO();

    /**
     * 获取玩家信息的业务逻辑（异步）。
     * @param playerId 玩家ID
     * @return Future<Optional<Player>>
     */
    public Future<Optional<Player>> getPlayer(String playerId) {
        // 这里可以添加更复杂的业务逻辑，比如缓存、数据聚合等
        return playerDAO.getPlayerById(playerId);
    }

    /**
     * 保存玩家信息（异步）
     * @param player 玩家信息
     */
    public Future<Void> savePlayer(Player player) {
        return playerDAO.savePlayer(player);
    }

    /**
     * 删除玩家信息（异步）
     * @param playerId 玩家ID
     */
    public Future<Void> deletePlayer(String playerId) {
        return playerDAO.deletePlayer(playerId);
    }
}
