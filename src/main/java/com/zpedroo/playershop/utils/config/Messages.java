package com.zpedroo.playershop.utils.config;

import com.zpedroo.playershop.FileUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Messages {

    public static final String NEED_SPACE_PLAYER = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.need-space-player"));

    public static final String NEED_SPACE_SHOP = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.need-space-shop"));

    public static final String WITHOUT_PERMISSION = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.without-permission"));

    public static final String INVALID_PRICE = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.invalid-price"));

    public static final String BUY_SUCESSFUL = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.buy-successful"));

    public static final String SELL_SUCESSFUL = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.sell-successful"));

    public static final String INSUFFICIENT_ITEMS_PLAYER = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.insufficient-items-player"));

    public static final String INSUFFICIENT_ITEMS_SHOP = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.insufficient-items-shop"));

    public static final String INSUFFICIENT_CURRENCY_PLAYER = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.insufficient-currency-player"));

    public static final String INSUFFICIENT_CURRENCY_OWNER = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.insufficient-currency-owner"));

    public static final String NEED_LOOK = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.need-look"));

    public static final String NEED_ITEM = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.need-item"));

    public static final String HAS_SHOP = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.has-shop"));

    public static final String INVALID_VALUE = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.invalid-value"));

    public static final String SHOP_CREATED = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.shop-created"));

    public static final List<String> BUY_PRICE = getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG,"Messages.buy-price"));

    public static final List<String> SELL_PRICE = getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG,"Messages.sell-price"));

    public static final List<String> EDIT_AMOUNT = getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG,"Messages.edit-amount"));

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