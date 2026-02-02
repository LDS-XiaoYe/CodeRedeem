package com.mcstaralliance.coderedeem.database;

import java.util.List;

/**
 * 数据存储接口，支持MySQL和YAML两种存储方式
 */
public interface DataStorage {
    
    /**
     * 初始化存储
     * @return 是否初始化成功
     */
    boolean initialize();
    
    /**
     * 关闭存储连接
     */
    void close();
    
    /**
     * 保存兑换码
     * @param code 兑换码
     * @param expireAt 过期时间戳
     * @param commands 命令列表
     * @return 是否保存成功
     */
    boolean saveCode(String code, long expireAt, List<String> commands);
    
    /**
     * 检查兑换码是否有效
     * @param code 兑换码
     * @return 是否有效
     */
    boolean isValidCode(String code);
    
    /**
     * 获取兑换码的命令列表
     * @param code 兑换码
     * @return 命令列表
     */
    List<String> getCommands(String code);
    
    /**
     * 获取兑换码的过期时间
     * @param code 兑换码
     * @return 过期时间戳
     */
    long getExpireTime(String code);
    
    /**
     * 检查玩家是否使用过兑换码
     * @param code 兑换码
     * @param playerName 玩家名
     * @return 是否使用过
     */
    boolean hasPlayerUsed(String code, String playerName);
    
    /**
     * 保存玩家使用记录
     * @param code 兑换码
     * @param playerName 玩家名
     * @return 是否保存成功
     */
    boolean savePlayerUsage(String code, String playerName);
    
    /**
     * 获取使用过兑换码的玩家列表
     * @param code 兑换码
     * @return 玩家名列表
     */
    List<String> getUsedPlayers(String code);
    
    /**
     * 获取存储类型名称
     * @return 存储类型（MySQL或YAML）
     */
    String getStorageType();
}
