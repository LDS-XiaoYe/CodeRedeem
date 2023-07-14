package com.mcstaralliance.coderedeem;

import com.mcstaralliance.coderedeem.command.AddCommand;
import com.mcstaralliance.coderedeem.command.RedeemCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CodeRedeem extends JavaPlugin {

    private static CodeRedeem instance;


    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginCommand("redeem").setExecutor(new RedeemCommand());
        Bukkit.getPluginCommand("createredeem").setExecutor(new AddCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static CodeRedeem getInstance() {
        return instance;
    }
}
