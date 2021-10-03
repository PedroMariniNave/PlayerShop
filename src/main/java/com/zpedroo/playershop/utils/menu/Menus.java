package com.zpedroo.playershop.utils.menu;

import com.zpedroo.multieconomy.api.CurrencyAPI;
import com.zpedroo.multieconomy.objects.Currency;
import com.zpedroo.playershop.FileUtils;
import com.zpedroo.playershop.enums.ShopAction;
import com.zpedroo.playershop.enums.ShopType;
import com.zpedroo.playershop.listeners.PlayerChatListener;
import com.zpedroo.playershop.objects.Shop;
import com.zpedroo.playershop.objects.ShopCreator;
import com.zpedroo.playershop.utils.builder.InventoryUtils;
import com.zpedroo.playershop.utils.builder.ItemBuilder;
import com.zpedroo.playershop.utils.config.Messages;
import com.zpedroo.playershop.utils.formatter.NumberFormatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;

public class Menus {

    private static Menus instance;
    public static Menus getInstance() { return instance; }

    private InventoryUtils inventoryUtils;

    public Menus() {
        instance = this;
        this.inventoryUtils = new InventoryUtils();
    }

    public void openDisplayMenu(Player player, Shop shop) {
        FileUtils.Files file = FileUtils.Files.DISPLAY;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        Inventory inventory = Bukkit.createInventory(null, size, title);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str).build();
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            inventoryUtils.addAction(inventory, item, () -> {
                shop.setDisplay(item.getType());
                player.closeInventory();
            }, InventoryUtils.ActionType.ALL_CLICKS);

            inventory.setItem(slot, item);
        }

        player.openInventory(inventory);
    }

    public void openChooseMenu(Player player, Shop shop) {
        FileUtils.Files file = FileUtils.Files.CHOOSE;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        Inventory inventory = Bukkit.createInventory(null, size, title);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str).build();
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            if (!StringUtils.equals(action, "NULL")) {
                switch (action) {
                    case "SELECT_BUY" -> inventoryUtils.addAction(inventory, item, () -> openShopMenu(player, shop, shop.getDefaultAmount(), ShopAction.BUY), InventoryUtils.ActionType.ALL_CLICKS);
                    case "SELECT_SELL" -> inventoryUtils.addAction(inventory, item, () -> openShopMenu(player, shop, shop.getDefaultAmount(), ShopAction.SELL), InventoryUtils.ActionType.ALL_CLICKS);
                }
            }

            inventory.setItem(slot, item);
        }

        player.openInventory(inventory);
    }

    public void openShopMenu(Player player, Shop shop, Integer amount, ShopAction shopAction) {
        FileUtils.Files file = FileUtils.Files.SHOP;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        Inventory inventory = Bukkit.createInventory(null, size, title);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = null;
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            if (StringUtils.equals(str, "shop-item")) {
                item = shop.getItem().clone();

                if (item.getType().equals(Material.AIR)) return;

                item.setAmount(1);

                inventory.setItem(slot, item);
                continue;
            }

            BigInteger price = null;

            switch (shopAction) {
                case BUY -> price = shop.getBuyPrice();
                case SELL -> price = shop.getSellPrice();
            }

            BigInteger finalPrice = price.multiply(BigInteger.valueOf(amount));

            item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str, new String[]{
                    "{amount}",
                    "{price}"
            }, new String[]{
                    NumberFormatter.getInstance().formatDecimal(amount.doubleValue()),
                    StringUtils.replaceEach(shop.getCurrency().getAmountDisplay(), new String[]{ "{amount}" }, new String[]{ NumberFormatter.getInstance().format(finalPrice) }),
            }).build();

            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            if (!StringUtils.equals(action, "NULL")) {
                switch (action) {
                    case "CONFIRM" -> inventoryUtils.addAction(inventory, item, () -> {
                        switch (shopAction) {
                            case BUY -> shop.buy(player, amount);
                            case SELL -> shop.sell(player, amount);
                        }
                    }, InventoryUtils.ActionType.ALL_CLICKS);

                    case "SELECT_AMOUNT" -> inventoryUtils.addAction(inventory, item, () -> {
                        for (int i = 0; i < 25; ++i) {
                            player.sendMessage("");
                        }

                        for (String msg : Messages.EDIT_AMOUNT) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChatListener.PlayerChat(PlayerChatListener.PlayerChat.PlayerChatAction.SELECT_AMOUNT, shop, shopAction));
                        player.closeInventory();
                    }, InventoryUtils.ActionType.ALL_CLICKS);

                    case "CANCEL" -> inventoryUtils.addAction(inventory, item, player::closeInventory, InventoryUtils.ActionType.ALL_CLICKS);
                }
            }

            inventory.setItem(slot, item);
        }

        player.openInventory(inventory);
    }

    public void openEditMenu(Player player, Shop shop) {
        FileUtils.Files file = FileUtils.Files.EDIT_SHOP;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        Inventory inventory = Bukkit.createInventory(null, size, title);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = null;
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            if (StringUtils.equals(str, "shop-item")) {
                item = shop.getItem().clone();

                if (item.getType().equals(Material.AIR)) return;

                item.setAmount(1);

                inventory.setItem(slot, item);
                continue;
            }

            item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str, new String[]{
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

            if (!StringUtils.equals(action, "NULL")) {
                switch (action) {
                    case "EDIT_DISPLAY" -> inventoryUtils.addAction(inventory, item, () -> openDisplayMenu(player, shop), InventoryUtils.ActionType.ALL_CLICKS);
                    case "EDIT_TYPE" -> inventoryUtils.addAction(inventory, item, () -> openEditTypeMenu(player, shop), InventoryUtils.ActionType.ALL_CLICKS);
                    case "EDIT_CURRENCY" -> inventoryUtils.addAction(inventory, item, () -> openEditCurrencyMenu(player, shop), InventoryUtils.ActionType.ALL_CLICKS);
                    case "EDIT_AMOUNT" -> inventoryUtils.addAction(inventory, item, () -> {
                        for (int i = 0; i < 25; ++i) {
                            player.sendMessage("");
                        }

                        for (String msg : Messages.EDIT_AMOUNT) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChatListener.PlayerChat(PlayerChatListener.PlayerChat.PlayerChatAction.EDIT_AMOUNT, shop));
                        player.closeInventory();
                    },InventoryUtils.ActionType.ALL_CLICKS);

                    case "BUY_PRICE" -> inventoryUtils.addAction(inventory, item, () -> {
                        for (int i = 0; i < 25; ++i) {
                            player.sendMessage("");
                        }

                        for (String msg : Messages.BUY_PRICE) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChatListener.PlayerChat(PlayerChatListener.PlayerChat.PlayerChatAction.EDIT_BUY_PRICE, shop));
                        player.closeInventory();
                    }, InventoryUtils.ActionType.ALL_CLICKS);

                    case "SELL_PRICE" -> inventoryUtils.addAction(inventory, item, () -> {
                        for (int i = 0; i < 25; ++i) {
                            player.sendMessage("");
                        }

                        for (String msg : Messages.SELL_PRICE) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChatListener.PlayerChat(PlayerChatListener.PlayerChat.PlayerChatAction.EDIT_SELL_PRICE, shop));
                        player.closeInventory();
                    }, InventoryUtils.ActionType.ALL_CLICKS);

                    case "OPEN_CHEST" -> inventoryUtils.addAction(inventory, item, () -> {
                        Inventory chest = shop.getChestInventory();
                        player.openInventory(chest);
                        inventoryUtils.getCloseInventories().put(chest, shop);
                    }, InventoryUtils.ActionType.ALL_CLICKS);

                    case "DELETE" -> inventoryUtils.addAction(inventory, item, () -> {
                        player.closeInventory();
                        shop.delete();
                    }, InventoryUtils.ActionType.ALL_CLICKS);
                }
            }

            inventory.setItem(slot, item);
        }

        player.openInventory(inventory);
    }

    public void openEditTypeMenu(Player player, Shop shop) {
        FileUtils.Files file = FileUtils.Files.EDIT_TYPE;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        Inventory inventory = Bukkit.createInventory(null, size, title);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str).build();
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            if (!StringUtils.equals(action, "NULL")) {
                switch (action) {
                    case "SELECT_BUY" -> inventoryUtils.addAction(inventory, item, () -> {
                        shop.setType(ShopType.BUY);
                        openEditMenu(player, shop);
                    }, InventoryUtils.ActionType.ALL_CLICKS);

                    case "SELECT_SELL" -> inventoryUtils.addAction(inventory, item, () -> {
                        shop.setType(ShopType.SELL);
                        openEditMenu(player, shop);
                    }, InventoryUtils.ActionType.ALL_CLICKS);

                    case "SELECT_BOTH" -> inventoryUtils.addAction(inventory, item, () -> {
                        shop.setType(ShopType.BOTH);
                        openEditMenu(player, shop);
                    }, InventoryUtils.ActionType.ALL_CLICKS);
                }
            }

            inventory.setItem(slot, item);
        }

        player.openInventory(inventory);
    }

    public void openEditCurrencyMenu(Player player, Shop shop) {
        FileUtils.Files file = FileUtils.Files.EDIT_CURRENCY;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        Inventory inventory = Bukkit.createInventory(null, size, title);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str).build();
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            String currecyName = FileUtils.get().getString(file, "Inventory.items." + str + ".currency");
            Currency currency = CurrencyAPI.getCurrency(currecyName);
            if (currency != null) {
                inventoryUtils.addAction(inventory, item, () -> {
                    shop.setCurrency(currency);
                    openEditMenu(player, shop);
                }, InventoryUtils.ActionType.ALL_CLICKS);
            }

            inventory.setItem(slot, item);
        }

        player.openInventory(inventory);
    }

    public void openSelectCurrencyMenu(Player player, ShopCreator creator) {
        FileUtils.Files file = FileUtils.Files.SELECT_CURRENCY;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        Inventory inventory = Bukkit.createInventory(null, size, title);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str).build();
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            String currecyName = FileUtils.get().getString(file, "Inventory.items." + str + ".currency");
            Currency currency = CurrencyAPI.getCurrency(currecyName);
            if (currency != null) {
                inventoryUtils.addAction(inventory, item, () -> {
                    creator.setCurrency(currency);

                    for (int i = 0; i < 25; ++i) {
                        player.sendMessage("");
                    }

                    player.closeInventory();

                    switch (creator.getType()) {
                        case BUY -> {
                            for (String msg : Messages.BUY_PRICE) {
                                if (msg == null) break;

                                player.sendMessage(msg);
                            }

                            PlayerChatListener.getPlayerChat().put(player, new PlayerChatListener.PlayerChat(PlayerChatListener.PlayerChat.PlayerChatAction.EDIT_BUY_PRICE, null, creator));
                        }

                        case SELL -> {
                            for (String msg : Messages.SELL_PRICE) {
                                if (msg == null) break;

                                player.sendMessage(msg);
                            }

                            PlayerChatListener.getPlayerChat().put(player, new PlayerChatListener.PlayerChat(PlayerChatListener.PlayerChat.PlayerChatAction.EDIT_BUY_PRICE, null, creator));
                        }

                        case BOTH -> {
                            for (String msg : Messages.BUY_PRICE) {
                                if (msg == null) break;

                                player.sendMessage(msg);
                            }

                            PlayerChatListener.getPlayerChat().put(player, new PlayerChatListener.PlayerChat(PlayerChatListener.PlayerChat.PlayerChatAction.EDIT_BUY_PRICE, PlayerChatListener.PlayerChat.PlayerChatAction.EDIT_SELL_PRICE, creator));
                        }
                    }
                }, InventoryUtils.ActionType.ALL_CLICKS);
            }

            inventory.setItem(slot, item);
        }

        player.openInventory(inventory);
    }

    public void openCreatorMenu(Player player, ShopCreator creator) {
        FileUtils.Files file = FileUtils.Files.CREATE_SHOP;

        String title = ChatColor.translateAlternateColorCodes('&', FileUtils.get().getString(file, "Inventory.title"));
        int size = FileUtils.get().getInt(file, "Inventory.size");

        Inventory inventory = Bukkit.createInventory(null, size, title);

        for (String str : FileUtils.get().getSection(file, "Inventory.items")) {
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str).build();
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            if (!StringUtils.equals(action, "NULL")) {
                switch (action) {
                    case "SELECT_BUY" -> inventoryUtils.addAction(inventory, item, () -> {
                        creator.setType(ShopType.BUY);
                        openSelectCurrencyMenu(player, creator);
                    }, InventoryUtils.ActionType.ALL_CLICKS);

                    case "SELECT_SELL" -> inventoryUtils.addAction(inventory, item, () -> {
                        creator.setType(ShopType.SELL);
                        openSelectCurrencyMenu(player, creator);
                    }, InventoryUtils.ActionType.ALL_CLICKS);

                    case "SELECT_BOTH" -> inventoryUtils.addAction(inventory, item, () -> {
                        creator.setType(ShopType.BOTH);
                        openSelectCurrencyMenu(player, creator);
                    }, InventoryUtils.ActionType.ALL_CLICKS);
                }
            }

            inventory.setItem(slot, item);
        }

        player.openInventory(inventory);
    }
}