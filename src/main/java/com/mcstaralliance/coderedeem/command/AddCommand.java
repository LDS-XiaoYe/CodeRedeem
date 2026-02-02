package com.mcstaralliance.coderedeem.command;

import com.mcstaralliance.coderedeem.CodeRedeem;
import com.mcstaralliance.coderedeem.database.DataStorage;
import com.mcstaralliance.coderedeem.util.StringConst;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
        
        if (saveCode(code, timestamp, sortCommands(commands))) {
            sender.sendMessage(ChatColor.GREEN + "兑换码创建成功！");
        } else {
            sender.sendMessage(ChatColor.RED + "兑换码创建失败，请检查存储配置。");
        }
        
        return true;
    }

    public List<String> sortCommands(String command) {
        return Arrays.stream(command.split(";"))
                .map(s -> s.replaceAll("_", " "))
                .collect(Collectors.toList());
    }
    
    public boolean saveCode(String code, long timestamp, List<String> commands) {
        DataStorage storage = plugin.getDataStorage();
        return storage.saveCode(code, timestamp, commands);
    }
}
