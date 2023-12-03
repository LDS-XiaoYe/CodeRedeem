package com.mcstaralliance.coderedeem.command;

import com.mcstaralliance.coderedeem.CodeRedeem;
import com.mcstaralliance.coderedeem.util.StringConst;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AddCommand implements CommandExecutor {
    private static final CodeRedeem plugin = CodeRedeem.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + StringConst.PERMISSION_DENIED);
            return false;
        }

        if (args.length != 3) {
            sender.sendMessage(ChatColor.RED + StringConst.INVALID_ARGUMENTS);
            sender.sendMessage(ChatColor.RED + StringConst.ADD_COMMAND_HELP_1);
            sender.sendMessage(ChatColor.RED + StringConst.ADD_COMMAND_HELP_2);
            return false;
        }

        String code = args[0];
        long timestamp = Long.parseLong(args[1]);
        String commands = args[2];
        saveCode(code, timestamp, sortCommands(commands));
        return true;
    }

    public List<String> sortCommands(String command) {
        return Arrays.stream(command.split(";"))
                .map(s -> s.replaceAll("_", " "))
                .collect(Collectors.toList());
    }
    public void saveCode(String code, long timestamp, List<String> commands) {
        FileConfiguration config = plugin.getConfig();
        config.set(code + ".expire_at", timestamp);
        config.set(code + ".commands", commands);
        plugin.saveConfig();
    }
}
