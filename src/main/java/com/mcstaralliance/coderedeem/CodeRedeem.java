package com.mcstaralliance.coderedeem;

import com.mcstaralliance.coderedeem.command.AddCommand;
import com.mcstaralliance.coderedeem.command.AddCommandTabCompleter;
import com.mcstaralliance.coderedeem.command.RedeemCommand;
import com.mcstaralliance.coderedeem.command.RedeemTabCompleter;
import com.mcstaralliance.coderedeem.database.DataStorage;
import com.mcstaralliance.coderedeem.database.MySQLManager;
import com.mcstaralliance.coderedeem.database.YAMLStorage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CodeRedeem extends JavaPlugin {

    private static CodeRedeem instance;
    private DataStorage dataStorage;


    @Override
    public void onEnable() {
        instance = this;
        
        // 保存默认配置
        saveDefaultConfig();
        
        // 根据配置选择存储方式
        boolean enableMySQL = getConfig().getBoolean("enable-mysql", false);
        
        if (enableMySQL) {
            getLogger().info("正在启用MySQL存储模式...");
            dataStorage = new MySQLManager(this);
        } else {
            getLogger().info("正在启用YAML配置文件存储模式...");
            dataStorage = new YAMLStorage(this);
        }
        
        // 初始化存储
        if (!dataStorage.initialize()) {
            getLogger().severe("无法初始化数据存储！插件将被禁用。");
            if (enableMySQL) {
                getLogger().severe("请检查config.yml中的MySQL配置，或将enable-mysql设置为false使用YAML模式。");
            }
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        
        getLogger().info("使用 " + dataStorage.getStorageType() + " 存储模式");
        
        // 注册命令redeem").setTabCompleter(new RedeemTabCompleter());
        Bukkit.getPluginCommand("createredeem").setExecutor(new AddCommand());
        Bukkit.getPluginCommand("createredeem").setTabCompleter(new AddCommandTabCompleter());
        Bukkit.getPluginCommand("redeem").setExecutor(new RedeemCommand());
        Bukkit.getPluginCommand("createredeem").setExecutor(new AddCommand());
        
        getLogger().info("CodeRedeem插件已启用！");
    }

    @Override
    public void onDisable() {
        // 关闭数据存储
        if (dataStorage != null) {
            dataStorage.close();
        }
        getLogger().info("CodeRedeem插件已禁用！");
    }

    public static CodeRedeem getInstance() {
        return instance;
    }
    
    public DataStorage getDataStorage() {
        return dataStorage;
    }
}
