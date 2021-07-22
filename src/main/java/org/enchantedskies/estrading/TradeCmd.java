package org.enchantedskies.estrading;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class TradeCmd implements CommandExecutor {
    private final ESTrading plugin = ESTrading.getInstance();
    private final HashMap<UUID, UUID> playerToReceiver = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Console cannot run this command!");
            return true;
        }
        UUID playerUUID = player.getUniqueId();
        if (args.length < 1) {
            player.sendMessage("§cIncorrect usage: Try /trade <player_name>.");
            return true;
        }
        Player tradeReceiver = Bukkit.getPlayer(args[0]);
        if (tradeReceiver == null) {
            player.sendMessage("§cIt looks like that player does not exist! Try /trade <player_name>.");
            return true;
        } if (player == tradeReceiver) {
            player.sendMessage("§cYou can't trade with yourself!");
            return true;
        }
        if (args.length > 1) {
            if (args[1].equals("accept")) {
                if (!playerToReceiver.containsKey(tradeReceiver.getUniqueId())) {
                    player.sendMessage("§cYou do not have a trade request from this player!");
                    return true;
                }
            } else if (args[1].equals("deny")) {
                if (!playerToReceiver.containsKey(tradeReceiver.getUniqueId())) {
                    player.sendMessage("§cYou do not have a trade request from this player!");
                    return true;
                }
                playerToReceiver.remove(tradeReceiver.getUniqueId());
                player.sendMessage("§cTrade request denied.");
                tradeReceiver.sendMessage("§e" + player.getName() + " §7denied your trade request.");
                return true;
            } else {
                player.sendMessage("§cIncorrect usage: Try /trade <player_name>.");
                return true;
            }
        }
        if (ESTrading.tradeManager.getTradingPlayers().contains(tradeReceiver.getUniqueId())) {
            player.sendMessage("§cThat players is currently in a trade!");
            return true;
        } else if (playerToReceiver.containsKey(playerUUID)) {
            player.sendMessage("§cYou already have a pending trade request!");
            return true;
        } else if (playerToReceiver.containsKey(tradeReceiver.getUniqueId())) {
            playerToReceiver.remove(tradeReceiver.getUniqueId());
            ESTrading.tradeManager.startTrade(player, tradeReceiver);
            return true;
        }
        player.sendMessage("§7You have sent a trade request to §e" + tradeReceiver.getName());
        playerToReceiver.put(playerUUID, tradeReceiver.getUniqueId());
        new BukkitRunnable() {
            public void run() {
                if (!playerToReceiver.containsKey(playerUUID)) return;
                playerToReceiver.remove(playerUUID);
                player.sendMessage("§cThe trade was not accepted.");
            }
        }.runTaskLater(plugin, 1200);
        if (isBedrockPlayer(tradeReceiver)) {
            tradeReceiver.sendMessage("§7You've received a trade request from §e" + player.getName() + " §7type §e/trade " + player.getName() + " accept §7to accept or §e/trade " + player.getName() + " deny §7to deny.");
        } else {
            BaseComponent message = new TextComponent("§7You've received a trade request from §e" + player.getName() + " ");
            TextComponent acceptText = new TextComponent("§a§l[ACCEPT]");
            acceptText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trade " + player.getName()));
            acceptText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to accept trade request")));
            message.addExtra(acceptText);
            message.addExtra(new TextComponent(" "));
            TextComponent denyText = new TextComponent("§c§l[DENY]");
            denyText.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trade " + player.getName() + " deny"));
            denyText.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Click to accept trade request")));
            message.addExtra(denyText);
            tradeReceiver.spigot().sendMessage(message);
        }
        return true;
    }

    public boolean isBedrockPlayer(Player player) {
        return player.getName().startsWith(".");
    }
}
