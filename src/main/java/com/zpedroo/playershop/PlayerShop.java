package com.zpedroo.playershop;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.zpedroo.playershop.commands.CreateShopCmd;
import com.zpedroo.playershop.hooks.ProtocolLibHook;
import com.zpedroo.playershop.listeners.PlayerChatListener;
import com.zpedroo.playershop.listeners.ShopListeners;
import com.zpedroo.playershop.managers.ShopManager;
import com.zpedroo.playershop.mysql.DBConnection;
import com.zpedroo.playershop.utils.FileUtils;
import com.zpedroo.playershop.utils.formatter.NumberFormatter;
import com.zpedroo.playershop.utils.menu.Menus;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class PlayerShop extends JavaPlugin {

    private static PlayerShop instance;
    public static PlayerShop get() { return instance; }

    public void onEnable() {
        instance = this;
        new FileUtils(this);

        if (!isMySQLEnabled(getConfig())) {
            getLogger().log(Level.SEVERE, "MySQL are disabled! You need to enable it.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new DBConnection(getConfig());
        new NumberFormatter(getConfig());
        new Menus();

        ProtocolLibrary.getProtocolManager().addPacketListener(new ProtocolLibHook(this, PacketType.Play.Client.LOOK));

        registerCommands();
        registerListeners();
    }

    public void onDisable() {
        if (!isMySQLEnabled(getConfig())) return;

        try {
            ShopManager.getInstance().saveAll();
            ShopManager.getInstance().clearAll();
            DBConnection.getInstance().closeConnection();
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "An error occurred while trying to save data!");
            ex.printStackTrace();
        }
    }

    private void registerCommands() {
        getCommand("createshop").setExecutor(new CreateShopCmd());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
        getServer().getPluginManager().registerEvents(new ShopListeners(), this);
    }

    private Boolean isMySQLEnabled(FileConfiguration file) {
        if (!file.contains("MySQL.enabled")) return false;

        return file.getBoolean("MySQL.enabled");
    }
}