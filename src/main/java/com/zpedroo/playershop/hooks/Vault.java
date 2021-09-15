package com.zpedroo.playershop.hooks;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Vault {

    private static Economy economy;

    public Vault() {
        this.hook();
    }

    public void hook() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return;

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return;

        economy = rsp.getProvider();
    }

    public static double getMoney(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public static void removeMoney(OfflinePlayer player, double amount) {
        economy.withdrawPlayer(player.getName(), Bukkit.getWorlds().get(0).getName(), amount);
    }

    public static void addMoney(OfflinePlayer player, double amount) {
        economy.depositPlayer(player.getName(), Bukkit.getWorlds().get(0).getName(), amount);
    }
}