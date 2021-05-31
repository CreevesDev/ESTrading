package org.enchantedskies.estrading;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

public class Logger {
    private final PrintWriter printWriter;

    public Logger() throws IOException {
        ESTrading plugin = ESTrading.getInstance();
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();
        File logFile = new File(plugin.getDataFolder(), "current.log");
        if(!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter fileWriter = new FileWriter(logFile, true);
        printWriter = new PrintWriter(fileWriter);
    }

    public void logItemAdd(Player player, ItemStack item) {
        String stackInfo = "[GUI] " + player.getName() + " added " + item.getType().name() + " x" + item.getAmount() + " to trade window";
        printWriter.println(stackInfo);
        printWriter.flush();
    }

    public void logItemRemove(Player player, ItemStack item) {
        String stackInfo = "[GUI] " + player.getName() + " removed " + item.getType().name() + " x" + item.getAmount() + " from trade window";
        printWriter.println(stackInfo);
        printWriter.flush();
    }

    public void logTradeCompleted(Player player, Collection<ItemStack> items) {
        String stackInfo = "[COMPLETE] Sent to " + player.getName() + ": ";
        if (items.size() == 0) stackInfo += "nothing, ";
        for (ItemStack item : items) {
            stackInfo += item.getType().name() + " x" + item.getAmount() + ", ";
        }
        stackInfo = stackInfo.substring(0, stackInfo.length() - 2);
        printWriter.println(stackInfo);
        printWriter.flush();
    }

    public void logTradeCancelled(Player player, Collection<ItemStack> items) {
        String stackInfo = "[CANCELLED] Returned to " + player.getName() + ": ";
        if (items.size() == 0) stackInfo += "nothing, ";
        for (ItemStack item : items) {
            stackInfo += item.getType().name() + " x" + item.getAmount() + ", ";
        }
        stackInfo = stackInfo.substring(0, stackInfo.length() - 2);
        printWriter.println(stackInfo);
        printWriter.flush();
    }
}
