package com.mcstaralliance.coderedeem.command;

import com.mcstaralliance.coderedeem.CodeRedeem;
import com.mcstaralliance.coderedeem.database.DataStorage;
import com.mcstaralliance.coderedeem.util.StringConst;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class RedeemCommand implements CommandExecutor {

    private static final CodeRedeem plugin = CodeRedeem.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + StringConst.INVALID_ARGUMENTS);
            player.sendMessage(ChatColor.RED + StringConst.REDEEM_COMMAND_HELP);

            return false;
        }
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
        commands.forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
        saveUsedPlayer(code, player);
        player.sendMessage(ChatColor.GREEN + StringConst.USE_SUCCESSFULLY);
    }

    public void saveUsedPlayer(String code, Player player) {
        DataStorage storage = plugin.getDataStorage();
        storage.savePlayerUsage(code, player.getName());
    }

    public boolean isValidCode(String code) {
        DataStorage storage = plugin.getDataStorage();
        return storage.isValidCode(code);
    }

    public List<String> getCommands(String code) {
        DataStorage storage = plugin.getDataStorage();
        return storage.getCommands(code);
    }

    public boolean isExpirationEnabled(String code) {
        long expireAt = getTimestamp(code);
        return expireAt != 0;
    }

    public List<String> getUsedPlayers(String code) {
        DataStorage storage = plugin.getDataStorage();
        return storage.getUsedPlayers(code);
    }

    public boolean isUsedBefore(String code, Player player) {
        DataStorage storage = plugin.getDataStorage();
        return storage.hasPlayerUsed(code, player.getName());
    }

    public boolean isExpiredCode(String code) {
        if (!isExpirationEnabled(code)) {
            return false;
        }
        return System.currentTimeMillis() >= getTimestamp(code);
    }

    public long getTimestamp(String code) {
        DataStorage storage = plugin.getDataStorage();
        return storage.getExpireTime(code);
    }
}