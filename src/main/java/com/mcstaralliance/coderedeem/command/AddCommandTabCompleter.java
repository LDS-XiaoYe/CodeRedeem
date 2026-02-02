package com.mcstaralliance.coderedeem.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class AddCommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.add("<兑换码>");
        } else if (args.length == 2) {
            completions.add("<过期时间戳>");
            completions.add("0");
        } else if (args.length == 3) {
            completions.add("<命令1;命令2;命令3>");
            completions.add("give_%player%_diamond_1");
        }
        
        return completions;
    }
}
