package com.game.core.redis;

/**
 * Redis Key 管理
 * 统一管理Redis的Key，避免硬编码
 */
public class RedisKey {
    private static final String PLAYER_INFO_PREFIX = "player:info:";

    /**
     * 获取玩家信息Key
     * @param playerId 玩家ID
     * @return Redis Key
     */
    public static String playerInfoKey(String playerId) {
        return PLAYER_INFO_PREFIX + playerId;
    }
} 