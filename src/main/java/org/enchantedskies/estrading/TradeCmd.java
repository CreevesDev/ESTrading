package org.enchantedskies.estrading;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TradeCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Console cannot run this command!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage("§cIncorrect usage: Try /trade <player_name>.");
            return true;
        }
        Player tradeReceiver = Bukkit.getPlayer(args[0]);
        if (tradeReceiver == null) {
            player.sendMessage("§cIt looks like that player does not exist! Try /trade <player_name>.");
            return true;
        }
        new TradeGUI(player, tradeReceiver);
        return true;
    }
}
