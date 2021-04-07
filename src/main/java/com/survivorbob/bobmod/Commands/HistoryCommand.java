package com.survivorbob.bobmod.Commands;

import com.survivorbob.bobmod.GUIs.menus.historyMenu;
import com.survivorbob.bobmod.GUIs.menus.mainMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HistoryCommand implements CommandExecutor {
    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if(!player.hasPermission("bobmod.gui.mod"))
        {
            return false;
        }
        if(args.length == 0)
        {
            player.sendMessage("Please provide a player to moderate!");
            return true;
        }
        new historyMenu().getHistory(player, args[0]);
        return true;
    }
}
