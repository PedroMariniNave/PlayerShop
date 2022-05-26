package com.zpedroo.playershop.utils.menu;

import com.zpedroo.multieconomy.api.CurrencyAPI;
import com.zpedroo.multieconomy.objects.Currency;
import com.zpedroo.playershop.enums.ShopAction;
import com.zpedroo.playershop.enums.ShopType;
import com.zpedroo.playershop.listeners.PlayerChatListener;
import com.zpedroo.playershop.objects.Shop;
import com.zpedroo.playershop.objects.ShopCreator;
import com.zpedroo.playershop.utils.FileUtils;
import com.zpedroo.playershop.utils.builder.InventoryBuilder;
import com.zpedroo.playershop.utils.builder.InventoryUtils;
import com.zpedroo.playershop.utils.builder.ItemBuilder;
import com.zpedroo.playershop.utils.config.Messages;
import com.zpedroo.playershop.utils.formatter.NumberFormatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;

public class Menus extends InventoryUtils {

    private static Menus instance;
    public static Menus getInstance() { return instance; }

    public Menus() {
        instance = this;
    }

    public void openDisplayMenu(Player player, Shop shop) {
        FileUtils.Files file = FileUtils.Files.DISPLAY;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str).build();
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            inventory.addItem(item, slot, () -> {
                shop.setDisplay(item.getType());
                inventory.close(player);
            }, ActionType.ALL_CLICKS);
        }

        inventory.open(player);
    }

    public void openChooseMenu(Player player, Shop shop) {
        FileUtils.Files file = FileUtils.Files.CHOOSE;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str).build();
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            inventory.addItem(item, slot, () -> {
                switch (action.toUpperCase()) {
                    case "SELECT_BUY":
                        openShopMenu(player, shop, shop.getDefaultAmount(), ShopAction.BUY);
                        break;
                    case "SELECT_SELL":
                        openShopMenu(player, shop, shop.getDefaultAmount(), ShopAction.SELL);
                        break;
                }
            }, ActionType.ALL_CLICKS);
        }

        inventory.open(player);
    }

    public void openShopMenu(Player player, Shop shop, Integer amount, ShopAction shopAction) {
        FileUtils.Files file = FileUtils.Files.SHOP;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            if (StringUtils.equals(str, "shop-item")) {
                ItemStack item = shop.getItem().clone();
                if (item.getType().equals(Material.AIR)) return;

                item.setAmount(1);

                inventory.addItem(item, slot);
                continue;
            }

            BigInteger price = null;

            switch (shopAction) {
                case BUY:
                    price = shop.getBuyPrice();
                    break;
                case SELL:
                    price = shop.getSellPrice();
                    break;
            }

            BigInteger finalPrice = price.multiply(BigInteger.valueOf(amount));
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str, new String[]{
                    "{amount}",
                    "{final_price}"
            }, new String[]{
                    NumberFormatter.getInstance().formatDecimal(amount.doubleValue()),
                    StringUtils.replaceEach(shop.getCurrency().getAmountDisplay(), new String[]{ "{amount}" }, new String[]{ NumberFormatter.getInstance().format(finalPrice) }),
            }).build();

            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            inventory.addItem(item, slot, () -> {
                switch (action.toUpperCase()) {
                    case "CONFIRM":
                        switch (shopAction) {
                            case BUY:
                                shop.buy(player, amount);
                                break;
                            case SELL:
                                shop.sell(player, amount);
                                break;
                        }
                        break;
                    case "SELECT_AMOUNT":
                        for (int i = 0; i < 25; ++i) {
                            player.sendMessage("");
                        }

                        for (String msg : Messages.EDIT_AMOUNT) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChatListener.PlayerChat(PlayerChatListener.PlayerChat.PlayerChatAction.SELECT_AMOUNT, shop, shopAction));
                        inventory.close(player);
                        break;
                    case "CANCEL":
                        inventory.close(player);
                        break;
                }
            }, ActionType.ALL_CLICKS);
        }

        inventory.open(player);
    }

    public void openEditMenu(Player player, Shop shop) {
        FileUtils.Files file = FileUtils.Files.EDIT_SHOP;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            if (StringUtils.equals(str, "shop-item")) {
                ItemStack item = shop.getItem().clone();
                if (item.getType().equals(Material.AIR)) return;

                item.setAmount(1);

                inventory.addItem(item, slot);
                continue;
            }

            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str, new String[]{
                    "{amount}",
                    "{buy_price}",
                    "{sell_price}",
                    "{type}",
                    "{currency}"
            }, new String[]{
                    NumberFormatter.getInstance().formatDecimal(shop.getDefaultAmount().doubleValue()),
                    StringUtils.replaceEach(shop.getCurrency().getAmountDisplay(), new String[]{ "{amount}" }, new String[]{ NumberFormatter.getInstance().format(shop.getBuyPrice()) }),
                    StringUtils.replaceEach(shop.getCurrency().getAmountDisplay(), new String[]{ "{amount}" }, new String[]{ NumberFormatter.getInstance().format(shop.getSellPrice()) }),
                    shop.getType().getTranslation(),
                    shop.getCurrency().getDisplay()
            }).build();
            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            inventory.addItem(item, slot, () -> {
                switch (action.toUpperCase()) {
                    case "EDIT_DISPLAY":
                        openDisplayMenu(player, shop);
                        break;
                    case "EDIT_TYPE":
                        openEditTypeMenu(player, shop);
                        break;
                    case "EDIT_CURRENCY":
                        openEditCurrencyMenu(player, shop);
                        break;
                    case "EDIT_AMOUNT":
                        for (int i = 0; i < 25; ++i) {
                            player.sendMessage("");
                        }

                        for (String msg : Messages.EDIT_AMOUNT) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChatListener.PlayerChat(PlayerChatListener.PlayerChat.PlayerChatAction.EDIT_AMOUNT, shop));
                        inventory.close(player);
                        break;
                    case "BUY_PRICE":
                        for (int i = 0; i < 25; ++i) {
                            player.sendMessage("");
                        }

                        for (String msg : Messages.BUY_PRICE) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChatListener.PlayerChat(PlayerChatListener.PlayerChat.PlayerChatAction.EDIT_BUY_PRICE, shop));
                        inventory.close(player);
                        break;
                    case "SELL_PRICE":
                        for (int i = 0; i < 25; ++i) {
                            player.sendMessage("");
                        }

                        for (String msg : Messages.SELL_PRICE) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChatListener.PlayerChat(PlayerChatListener.PlayerChat.PlayerChatAction.EDIT_SELL_PRICE, shop));
                        inventory.close(player);
                        break;
                    case "OPEN_CHEST":
                        Inventory chestInventory = shop.getChestInventory();
                        player.openInventory(chestInventory);
                        getCloseInventories().put(chestInventory, shop);
                        break;
                    case "DELETE":
                        inventory.close(player);
                        shop.delete();
                        break;
                }
            }, ActionType.ALL_CLICKS);
        }

        inventory.open(player);
    }

    public void openEditTypeMenu(Player player, Shop shop) {
        FileUtils.Files file = FileUtils.Files.EDIT_TYPE;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str).build();
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            inventory.addItem(item, slot, () -> {
                switch (action.toUpperCase()) {
                    case "SELECT_BUY":
                        shop.setType(ShopType.BUY);
                        openEditMenu(player, shop);
                        break;
                    case "SELECT_SELL":
                        shop.setType(ShopType.SELL);
                        openEditMenu(player, shop);
                        break;
                    case "SELECT_BOTH":
                        shop.setType(ShopType.BOTH);
                        openEditMenu(player, shop);
                        break;
                }
            }, ActionType.ALL_CLICKS);
        }

        inventory.open(player);
    }

    public void openEditCurrencyMenu(Player player, Shop shop) {
        FileUtils.Files file = FileUtils.Files.EDIT_CURRENCY;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str).build();
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            String currencyName = FileUtils.get().getString(file, "Inventory.items." + str + ".currency");
            Currency currency = CurrencyAPI.getCurrency(currencyName);
            inventory.addItem(item, slot, () -> {
                if (currency == null) return;

                shop.setCurrency(currency);
                openEditMenu(player, shop);
            }, ActionType.ALL_CLICKS);
        }

        inventory.open(player);
    }

    public void openSelectCurrencyMenu(Player player, ShopCreator creator) {
        FileUtils.Files file = FileUtils.Files.SELECT_CURRENCY;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str).build();
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            String currencyName = FileUtils.get().getString(file, "Inventory.items." + str + ".currency");
            Currency currency = CurrencyAPI.getCurrency(currencyName);
            inventory.addItem(item, slot, () -> {
                if (currency == null) return;

                creator.setCurrency(currency);

                for (int i = 0; i < 25; ++i) {
                    player.sendMessage("");
                }

                inventory.close(player);

                switch (creator.getType()) {
                    case BUY:
                        for (String msg : Messages.BUY_PRICE) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChatListener.PlayerChat(PlayerChatListener.PlayerChat.PlayerChatAction.EDIT_BUY_PRICE, null, creator));
                        break;
                    case SELL:
                        for (String msg : Messages.SELL_PRICE) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChatListener.PlayerChat(PlayerChatListener.PlayerChat.PlayerChatAction.EDIT_BUY_PRICE, null, creator));
                        break;
                    case BOTH:
                        for (String msg : Messages.BUY_PRICE) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChatListener.PlayerChat(PlayerChatListener.PlayerChat.PlayerChatAction.EDIT_BUY_PRICE, PlayerChatListener.PlayerChat.PlayerChatAction.EDIT_SELL_PRICE, creator));
                        break;
                }
            }, ActionType.ALL_CLICKS);
        }

        inventory.open(player);
    }

    public void openCreatorMenu(Player player, ShopCreator creator) {
        FileUtils.Files file = FileUtils.Files.CREATE_SHOP;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        InventoryBuilder inventory = new InventoryBuilder(title, size);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str).build();
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            inventory.addItem(item, slot, () -> {
                switch (action.toUpperCase()) {
                    case "SELECT_BUY":
                        creator.setType(ShopType.BUY);
                        openSelectCurrencyMenu(player, creator);
                        break;
                    case "SELECT_SELL":
                        creator.setType(ShopType.SELL);
                        openSelectCurrencyMenu(player, creator);
                        break;
                    case "SELECT_BOTH":
                        creator.setType(ShopType.BOTH);
                        openSelectCurrencyMenu(player, creator);
                        break;
                }
            }, ActionType.ALL_CLICKS);
        }

        inventory.open(player);
    }
}