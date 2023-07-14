package com.mcstaralliance.coderedeem.command;

import com.mcstaralliance.coderedeem.CodeRedeem;
import com.mcstaralliance.coderedeem.util.StringConst;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RedeemCommand implements CommandExecutor {

    private static final CodeRedeem plugin = CodeRedeem.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        String input = args[0];
        if (!isValidCode(input)) {
            player.sendMessage(ChatColor.RED + StringConst.INVALID_CODE);
            return false;
        }
        if (isUsedBefore(input, player)) {
            player.sendMessage(ChatColor.RED + StringConst.CODE_HAS_USED);
            return false;
        }
        if (isExpiredCode(input)) {
            player.sendMessage(ChatColor.RED + StringConst.CODE_EXPIRED);
            return false;
        }
        reward(input, player);
        return true;
    }

    public void reward(String code, Player player) {
        List<String> commands = getCommands(code).stream()
                .map(s -> s.replaceAll("%player%", player.getName()))
                .collect(Collectors.toList());
        for (String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
        saveUsedPlayer(code, player);
        player.sendMessage(ChatColor.GREEN + StringConst.USE_SUCCESSFULLY);
    }

    public void saveUsedPlayer(String code, Player player) {
        FileConfiguration config = plugin.getConfig();
        List<String> players = config.getStringList(code + ".used_by");
        players.add(player.getName());
        config.set(code + ".used_by", players);
        plugin.saveConfig();
    }

    public boolean isValidCode(String arg) {
        FileConfiguration config = plugin.getConfig();
        Set<String> codes = config.getKeys(false);
        return codes.contains(arg);
    }

    public List<String> getCommands(String code) {
        FileConfiguration config = plugin.getConfig();
        return config.getStringList(code + ".commands");
    }

    public boolean isExpirationEnabled(String code) {
        FileConfiguration config = plugin.getConfig();
        long expireAt = config.getLong(code + ".expire_at");
        return expireAt != 0;
    }

    public List<String> getUsedPlayers(String code) {
        FileConfiguration config = plugin.getConfig();
        return config.getStringList(code + ".used_by");
    }

    public boolean isUsedBefore(String code, Player player) {
        return getUsedPlayers(code).contains(player.getName());
    }

    public boolean isExpiredCode(String code) {
        if (!isExpirationEnabled(code)) {
            return false;
        }
        return System.currentTimeMillis() >= getTimestamp(code);
    }

    public long getTimestamp(String code) {
        FileConfiguration config = plugin.getConfig();
        return config.getLong(code + ".expire_at");
    }
}