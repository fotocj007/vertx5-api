package com.game.server.entity;

/**
 * 玩家实体类
 * 使用 Java 21 的 record 特性定义不可变的数据实体
 */
public record Player(String id, String name, int level) {
} 