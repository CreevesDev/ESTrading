package org.enchantedskies.estrading;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;

public class TradeSession {
    private final Trader trader1;
    private final Trader trader2;

    public TradeSession(Player trader1, Player trader2) {
        this.trader1 = new Trader(trader1, trader2);
        this.trader2 = new Trader(trader2, trader1);
    }

    public Trader getTrader1() {
        return trader1;
    }

    public Trader getTrader2() {
        return trader2;
    }

    public void updateStatus() {
        trader1.setTraderStatus(trader2.getStatus());
        trader2.setTraderStatus(trader1.getStatus());
        if (trader1.getStatus().equals("accepted") && trader2.getStatus().equals("accepted")) {
            ESTrading.tradeManager.completeTrade(trader1.getPlayerUUID());
        }
    }

    public void completeTrade() {
        Player player1 = Bukkit.getPlayer(trader1.getPlayerUUID());
        Player player2 = Bukkit.getPlayer(trader2.getPlayerUUID());
        Collection<ItemStack> itemSet1 = trader1.sortAndGetOfferedItems();
        Collection<ItemStack> itemSet2 = trader2.sortAndGetOfferedItems();
        giveItemsToPlayer(itemSet2, player1);
        giveItemsToPlayer(itemSet1, player2);
        trader1.completeTrade();
        trader2.completeTrade();
        ESTrading.logger.logTradeCompleted(player1, itemSet2);
        ESTrading.logger.logTradeCompleted(player2, itemSet1);
    }

    public void giveItemsToPlayer(Collection<ItemStack> itemSet, Player player) {
        Inventory playerInv = player.getInventory();
        for (ItemStack item : itemSet) {
            HashMap<Integer, ItemStack> itemMap = playerInv.addItem(item);
            for (ItemStack droppedItem : itemMap.values()) {
                player.getWorld().dropItem(player.getLocation(), droppedItem);
            }
        }
    }

    public void cancelTrade() {
        Player player1 = Bukkit.getPlayer(trader1.getPlayerUUID());
        Player player2 = Bukkit.getPlayer(trader2.getPlayerUUID());
        Collection<ItemStack> itemSet1 = trader1.sortAndGetOfferedItems();
        Collection<ItemStack> itemSet2 = trader2.sortAndGetOfferedItems();
        giveItemsToPlayer(itemSet1, player1);
        giveItemsToPlayer(itemSet2, player2);
        trader1.cancelTrade();
        trader2.cancelTrade();
        ESTrading.logger.logTradeCancelled(player1, itemSet1);
        ESTrading.logger.logTradeCancelled(player2, itemSet2);
    }
}
