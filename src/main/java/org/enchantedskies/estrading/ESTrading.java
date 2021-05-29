package org.enchantedskies.estrading;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class ESTrading extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("trade").setExecutor(new TradeCmd());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerEvents(Listener[] listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }
}
