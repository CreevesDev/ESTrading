package org.enchantedskies.estrading;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class TradeManager {
    private final HashMap<UUID, TradeSession> tradingPlayers = new HashMap<>();

    public void startTrade(Player trader1, Player trader2) {
        TradeSession tradeSession = new TradeSession(trader1, trader2);
        tradingPlayers.put(trader1.getUniqueId(), tradeSession);
        tradingPlayers.put(trader2.getUniqueId(), tradeSession);
    }

    public void completeTrade(UUID traderUUID) {
        TradeSession tradeSession = tradingPlayers.get(traderUUID);
        tradeSession.completeTrade();
        tradingPlayers.remove(tradeSession.getTrader1().getPlayerUUID());
        tradingPlayers.remove(tradeSession.getTrader2().getPlayerUUID());
    }

    public void endTrade(UUID traderUUID) {
        TradeSession tradeSession = tradingPlayers.get(traderUUID);
        tradeSession.cancelTrade();
        tradingPlayers.remove(tradeSession.getTrader1().getPlayerUUID());
        tradingPlayers.remove(tradeSession.getTrader2().getPlayerUUID());
    }

    public TradeSession getTradeSession(UUID playerUUID) {
        return tradingPlayers.get(playerUUID);
    }

    public Trader getTrader(UUID playerUUID) {
        TradeSession tradeSession = tradingPlayers.get(playerUUID);
        Trader trader1 = tradeSession.getTrader1();
        Trader trader2 = tradeSession.getTrader2();
        if (trader1.getPlayerUUID() == playerUUID) return trader1;
        else if (trader2.getPlayerUUID() == playerUUID) return trader2;
        else return null;
    }

    public Trader getTradingPartner(UUID playerUUID) {
        TradeSession tradeSession = tradingPlayers.get(playerUUID);
        Trader trader1 = tradeSession.getTrader1();
        Trader trader2 = tradeSession.getTrader2();
        if (trader1.getPlayerUUID() == playerUUID) return trader2;
        else if (trader2.getPlayerUUID() == playerUUID) return trader1;
        else return null;
    }

    public Set<UUID> getTradingPlayers() {
        return tradingPlayers.keySet();
    }
}
