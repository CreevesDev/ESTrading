package org.enchantedskies.estrading;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TradeGUI {
    private final Inventory inventory = Bukkit.createInventory(null, 54, "Trading Panel");

    public TradeGUI(Player trader1, Player trader2) {
        trader1.openInventory(inventory);
        trader2.openInventory(inventory);
    }
}
