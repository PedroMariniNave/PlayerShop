package com.zpedroo.playershop.utils.config;

import com.zpedroo.playershop.FileUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Settings {

    /*
     * Returns the hologram when
     * shop only buy items
     */
    public static final String[] BUY_HOLOGRAM = getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.buy-hologram")).toArray(new String[256]);

    /*
     * Returns the hologram when
     * shop only sell items
     */
    public static final String[] SELL_HOLOGRAM = getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.sell-hologram")).toArray(new String[256]);

    /*
     * Returns the hologram when
     * shop buy and sell items
     */
    public static final String[] BOTH_HOLOGRAM = getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.both-hologram")).toArray(new String[256]);

    /*
     * Returns the name of inventory
     * when player opens the shop chest
     */
    public static final String CHEST_INVENTORY_NAME = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.chest-inventory-name"));

    /*
     * Translate all String colors
     */
    private static String getColored(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    /*
     * Translate all Strings colors
     */
    private static List<String> getColored(List<String> list) {
        List<String> colored = new ArrayList<>();
        for (String str : list) {
            colored.add(getColored(str));
        }

        return colored;
    }
}