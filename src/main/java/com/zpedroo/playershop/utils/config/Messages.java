package com.zpedroo.playershop.utils.config;

import com.zpedroo.playershop.FileUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Messages {

    /*
     * Returns the message when
     * player hasn't slots
     * to buy all items
     */
    public static final String NEED_SPACE_PLAYER = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG, "Messages.need-space-player"));

    /*
     * Returns the message when
     * shop hasn't slots
     * to buy all items
     */
    public static final String NEED_SPACE_SHOP = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.need-space-shop"));

    /*
     * Returns the message when
     * player hasn't permission
     * to edit a shop
     */
    public static final String WITHOUT_PERMISSION = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.without-permission"));

    /*
     * Returns the message when
     * shop has a invalid price
     */
    public static final String INVALID_PRICE = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.invalid-price"));

    /*
     * Returns the message when
     * player successful buy items
     */
    public static final String BUY_SUCESSFUL = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.buy-successful"));

    /*
     * Returns the message when
     * player successful sell items
     */
    public static final String SELL_SUCESSFUL = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.sell-successful"));

    /*
     * Returns the message when player
     * hasn't sufficient items
     */
    public static final String INSUFFICIENT_ITEMS_PLAYER = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.insufficient-items-player"));

    /*
     * Returns the message when shop
     * hasn't sufficient items
     */
    public static final String INSUFFICIENT_ITEMS_SHOP = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.insufficient-items-shop"));

    /*
     * Returns the message when player
     * hasn't sufficient money to pay
     */
    public static final String INSUFFICIENT_MONEY_PLAYER = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.insufficient-money-player"));

    /*
     * Returns the message when owner
     * hasn't sufficient money to pay
     */
    public static final String INSUFFICIENT_MONEY_OWNER = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.insufficient-money-owner"));

    /*
     * Returns the message when
     * player aren't looking a block
     */
    public static final String NEED_LOOK = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.need-look"));

    /*
     * Returns the message when
     * player aren't holding a item
     */
    public static final String NEED_ITEM = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.need-item"));

    /*
     * Returns the message when already
     * exists a shop on location
     */
    public static final String HAS_SHOP = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.has-shop"));

    /*
     * Returns the message when
     * player types a invalid value
     */
    public static final String INVALID_VALUE = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.invalid-value"));

    /*
     * Returns the message when
     * player creates a new shop
     */
    public static final String SHOP_CREATED = getColored(FileUtils.get().getString(FileUtils.Files.CONFIG,"Messages.shop-created"));

    /*
     * Returns the messages when
     * player want to edit the shop
     * buy price
     */
    public static final List<String> BUY_PRICE = getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG,"Messages.buy-price"));

    /*
     * Returns the messages when
     * player want to edit the shop
     * sell price
     */
    public static final List<String> SELL_PRICE = getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG,"Messages.sell-price"));

    /*
     * Returns the messages when
     * player want to edit amount
     */
    public static final List<String> EDIT_AMOUNT = getColored(FileUtils.get().getStringList(FileUtils.Files.CONFIG,"Messages.edit-amount"));

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