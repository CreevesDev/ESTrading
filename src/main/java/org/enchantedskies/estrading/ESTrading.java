package org.enchantedskies.estrading;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class ESTrading extends JavaPlugin {
    private static ESTrading plugin;
    public static TradeManager tradeManager;
    public static Logger logger;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        tradeManager = new TradeManager();
        try {
            logger = new Logger();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getServer().getPluginManager().registerEvents(new TradeEvents(), this);
        getCommand("trade").setExecutor(new TradeCmd());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ESTrading getInstance() {
        return plugin;
    }
}
