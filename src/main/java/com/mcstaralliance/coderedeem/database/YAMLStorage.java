package com.mcstaralliance.coderedeem.database;

import com.mcstaralliance.coderedeem.CodeRedeem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * YAML配置文件存储实现
 */
public class YAMLStorage implements DataStorage {
    
    private final CodeRedeem plugin;
    private File dataFile;
    private FileConfiguration dataConfig;
    
    public YAMLStorage(CodeRedeem plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean initialize() {
        plugin.saveDefaultConfig();
        
        // 创建data.yml文件
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
                plugin.getLogger().info("已创建 data.yml 文件");
            } catch (IOException e) {
                plugin.getLogger().severe("无法创建 data.yml 文件: " + e.getMessage());
                return false;
            }
        }
        
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        plugin.getLogger().info("使用YAML配置文件存储模式（数据存储在 data.yml）");
        return true;
    }
    
    @Override
    public void close() {
        // YAML模式不需要关闭连接
        saveData();
    }
    
    private void saveData() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("保存 data.yml 文件时出错: " + e.getMessage());
        }
    }
    
    @Override
    public boolean saveCode(String code, long expireAt, List<String> commands) {
        try {
            dataConfig.set(code + ".expire_at", expireAt);
            dataConfig.set(code + ".commands", commands);
            saveData();
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("保存兑换码到配置文件时出错: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean isValidCode(String code) {
        Set<String> codes = dataConfig.getKeys(false);
        return codes.contains(code);
    }
    
    @Override
    public List<String> getCommands(String code) {
        return dataConfig.getStringList(code + ".commands");
    }
    
    @Override
    public long getExpireTime(String code) {
        return dataConfig.getLong(code + ".expire_at", 0);
    }
    
    @Override
    public boolean hasPlayerUsed(String code, String playerName) {
        List<String> usedPlayers = getUsedPlayers(code);
        return usedPlayers.contains(playerName);
    }
    
    @Override
    public boolean savePlayerUsage(String code, String playerName) {
        try {
            List<String> players = dataConfig.getStringList(code + ".used_by");
            if (!players.contains(playerName)) {
                players.add(playerName);
                dataConfig.set(code + ".used_by", players);
                saveData();
            }
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("保存玩家使用记录时出错: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public List<String> getUsedPlayers(String code) {
        List<String> players = dataConfig.getStringList(code + ".used_by");
        return players != null ? players : new ArrayList<>();
    }
    
    @Override
    public String getStorageType() {
        return "YAML";
    }
}
