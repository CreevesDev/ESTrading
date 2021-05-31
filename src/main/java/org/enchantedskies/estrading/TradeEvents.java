package org.enchantedskies.estrading;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TradeEvents implements Listener {
    private final ESTrading plugin = ESTrading.getInstance();
    private final ItemStack cancelItem;
    private final ItemStack unacceptedItem;
    private final ItemStack verifyItem;
    private final ItemStack acceptedItem;

    public TradeEvents() {

        cancelItem = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancelItem.getItemMeta();
        cancelMeta.setDisplayName("§c§lCANCEL");
        cancelItem.setItemMeta(cancelMeta);

        List<String> confirmLore = new ArrayList<>();
        confirmLore.add("§eClick to confirm");

        unacceptedItem = new ItemStack(Material.ORANGE_WOOL);
        ItemMeta unacceptedMeta = unacceptedItem.getItemMeta();
        unacceptedMeta.setDisplayName("§a§lACCEPT");
        unacceptedMeta.setLore(confirmLore);
        unacceptedItem.setItemMeta(unacceptedMeta);

        verifyItem = new ItemStack(Material.YELLOW_WOOL);
        ItemMeta verifyMeta = verifyItem.getItemMeta();
        verifyMeta.setDisplayName("§6§lAre you sure?");
        verifyMeta.setLore(confirmLore);
        verifyItem.setItemMeta(verifyMeta);

        acceptedItem = new ItemStack(Material.LIME_WOOL);
        ItemMeta acceptedMeta = acceptedItem.getItemMeta();
        acceptedMeta.setDisplayName("§a§lACCEPTED");
        acceptedItem.setItemMeta(acceptedMeta);
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        UUID playerUUID = player.getUniqueId();
        if (!ESTrading.tradeManager.getTradingPlayers().contains(playerUUID)) return;
        Inventory clickedInv = event.getClickedInventory();
        if (clickedInv == null) return;
        InventoryAction invAction = event.getAction();
        HashSet<InventoryAction> bannedActions = new HashSet<>();
        bannedActions.add(InventoryAction.CLONE_STACK);
        bannedActions.add(InventoryAction.COLLECT_TO_CURSOR);
        bannedActions.add(InventoryAction.DROP_ALL_CURSOR);
        bannedActions.add(InventoryAction.DROP_ALL_SLOT);
        bannedActions.add(InventoryAction.DROP_ONE_CURSOR);
        bannedActions.add(InventoryAction.DROP_ONE_SLOT);
        bannedActions.add(InventoryAction.HOTBAR_MOVE_AND_READD); //CHECK ME
        bannedActions.add(InventoryAction.HOTBAR_SWAP); //CHECK ME

        if (invAction == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            if (clickedInv.getType() != InventoryType.CHEST) {
                event.setCancelled(true);
                ItemStack currItem = event.getCurrentItem();
                if (currItem == null) return;
                int firstEmptySlot = -1;
                Inventory tradeInv = event.getWhoClicked().getOpenInventory().getTopInventory();
                for (int i = 0; i < 40; i++) {
                    if (i % 9 == 5) i += 4;
                    ItemStack thisItem = tradeInv.getItem(i);
                    if (thisItem == null || thisItem.getType() == Material.AIR) {
                        firstEmptySlot = i;
                        break;
                    }
                }
                if (firstEmptySlot >= 0) {
                    ESTrading.tradeManager.getTrader(playerUUID).addItem(currItem, firstEmptySlot);
                    ESTrading.tradeManager.getTradingPartner(playerUUID).addDisplayItem(currItem, firstEmptySlot);
                    clickedInv.setItem(event.getSlot(), new ItemStack(Material.AIR));
                    tradeInv.setItem(firstEmptySlot, currItem);
                }
            } else {
                ESTrading.tradeManager.getTrader(playerUUID).removeItem(event.getSlot());
                ESTrading.tradeManager.getTradingPartner(playerUUID).removeDisplayItem(event.getSlot());
            }
        }

        for (InventoryAction currAction : bannedActions) {
            if (invAction == currAction) {
                event.setCancelled(true);
                break;
            }
        }
        if (clickedInv.getType() == InventoryType.PLAYER) return;

        int invSlot = event.getSlot();
        // Limits usage of trading inventory to top left section
        if ((invSlot % 9) <= 3 && invSlot <= 39) {
            new BukkitRunnable() {
                public void run() {
                    ItemStack currItem = clickedInv.getItem(invSlot);
                    if (currItem == null || currItem.getType() == Material.AIR) {
                        ESTrading.tradeManager.getTrader(playerUUID).removeItem(invSlot);
                        ESTrading.tradeManager.getTradingPartner(playerUUID).removeDisplayItem(invSlot);
                    } else {
                        ESTrading.tradeManager.getTrader(playerUUID).addItem(currItem, invSlot);
                        ESTrading.tradeManager.getTradingPartner(playerUUID).addDisplayItem(currItem, invSlot);
                    }
                }
            }.runTaskLater(plugin, 1);
            return;
        }
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;
        if (clickedItem.isSimilar(cancelItem)) {
            ESTrading.tradeManager.endTrade(playerUUID);
        } else if (clickedItem.isSimilar(unacceptedItem) || clickedItem.isSimilar(verifyItem) || clickedItem.isSimilar(acceptedItem)) {
            ESTrading.tradeManager.getTrader(playerUUID).accept();
        }

    }

    @EventHandler
    public void onItemDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        UUID playerUUID = player.getUniqueId();
        if (!ESTrading.tradeManager.getTradingPlayers().contains(playerUUID)) return;
        Inventory clickedInv = event.getInventory();
        if (clickedInv.getType() == InventoryType.PLAYER) return;
        Set<Integer> invSlots = event.getInventorySlots();
        for (int currSlot: invSlots) {
            if ((currSlot % 9) <= 3 && currSlot <= 39) continue;
            event.setCancelled(true);
            return;
        }
        Map<Integer, ItemStack> addedItems = event.getNewItems();
        for (Integer slot : addedItems.keySet()) {
            ESTrading.tradeManager.getTrader(playerUUID).addItem(addedItems.get(slot), slot);
            ESTrading.tradeManager.getTradingPartner(playerUUID).addDisplayItem(addedItems.get(slot), slot);
        }
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        new BukkitRunnable() {
            public void run() {
                if (event.getPlayer().getOpenInventory().getType() != InventoryType.CHEST) {
                    UUID playerUUID = event.getPlayer().getUniqueId();
                    if (!ESTrading.tradeManager.getTradingPlayers().contains(playerUUID)) return;
                    ESTrading.tradeManager.endTrade(playerUUID);
                }
            }
        }.runTaskLater(plugin, 1);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getOpenInventory().getType() != InventoryType.CHEST) {
            UUID playerUUID = event.getEntity().getUniqueId();
            if (!ESTrading.tradeManager.getTradingPlayers().contains(playerUUID)) return;
            ESTrading.tradeManager.endTrade(playerUUID);
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        if (event.getPlayer().getOpenInventory().getType() != InventoryType.CHEST) {
            UUID playerUUID = event.getPlayer().getUniqueId();
            if (!ESTrading.tradeManager.getTradingPlayers().contains(playerUUID)) return;
            ESTrading.tradeManager.endTrade(playerUUID);
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!ESTrading.tradeManager.getTradingPlayers().contains(player.getUniqueId())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onArrowPickup(PlayerPickupArrowEvent event) {
        Player player = event.getPlayer();
        if (!ESTrading.tradeManager.getTradingPlayers().contains(player.getUniqueId())) return;
        event.setCancelled(true);
    }
}
