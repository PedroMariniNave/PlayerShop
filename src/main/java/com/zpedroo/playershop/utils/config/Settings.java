package com.zpedroo.playershop.utils.config;

import com.zpedroo.playershop.utils.FileUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Settings {

    public static final String BUY_TRANSLATION = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.type-translations.BUY"));

    public static final String SELL_TRANSLATION = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.type-translations.SELL"));

    public static final String BOTH_TRANSLATION = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.type-translations.BOTH"));

    public static final String[] BUY_HOLOGRAM = getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.buy-hologram")).toArray(new String[1]);

    public static final String[] SELL_HOLOGRAM = getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.sell-hologram")).toArray(new String[1]);

    public static final String[] BOTH_HOLOGRAM = getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG, "Settings.both-hologram")).toArray(new String[1]);

    public static final String CHEST_INVENTORY_NAME = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Settings.chest-inventory-name"));

    private static String getColored(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    private static List<String> getColored(List<String> list) {
        List<String> colored = new ArrayList<>(list.size());
        for (String str : list) {
            colored.add(getColored(str));
        }

        return colored;
    }
}