package com.zpedroo.playershop.utils.menu;

import com.zpedroo.playershop.FileUtils;
import com.zpedroo.playershop.enums.ShopAction;
import com.zpedroo.playershop.enums.ShopType;
import com.zpedroo.playershop.listeners.PlayerChatListener;
import com.zpedroo.playershop.shop.Shop;
import com.zpedroo.playershop.objects.ShopCreator;
import com.zpedroo.playershop.utils.builder.ItemBuilder;
import com.zpedroo.playershop.utils.chat.PlayerChat;
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
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str);
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            getInventoryUtils().addAction(inventory, slot, () -> {
                shop.setDisplay(item.getType());
                player.closeInventory();
            }, InventoryUtils.ActionClick.ALL);

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
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str);
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            if (!StringUtils.equals(action, "NULL")) {
                switch (action) {
                    case "SELECT_BUY" -> getInventoryUtils().addAction(inventory, slot, () -> openShopMenu(player, shop, shop.getAmount(), ShopAction.BUY), InventoryUtils.ActionClick.ALL);
                    case "SELECT_SELL" -> getInventoryUtils().addAction(inventory, slot, () -> openShopMenu(player, shop, shop.getAmount(), ShopAction.SELL), InventoryUtils.ActionClick.ALL);
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
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            if (StringUtils.equals(str, "shop-item")) {
                ItemStack item = shop.getItem().clone();

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

            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str, new String[]{
                    "{amount}",
                    "{final_price}"
            }, new String[]{
                    NumberFormatter.getInstance().formatDecimal(amount.doubleValue()),
                    NumberFormatter.getInstance().format(finalPrice)
            });

            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            if (!StringUtils.equals(action, "NULL")) {
                switch (action) {
                    case "CONFIRM" -> getInventoryUtils().addAction(inventory, slot, () -> {
                        switch (shopAction) {
                            case BUY -> shop.buy(player, amount);
                            case SELL -> shop.sell(player, amount);
                        }
                    }, InventoryUtils.ActionClick.ALL);
                    case "SELECT_AMOUNT" -> getInventoryUtils().addAction(inventory, slot, () -> {
                        for (int i = 0; i < 25; ++i) {
                            player.sendMessage("");
                        }

                        for (String msg : Messages.EDIT_AMOUNT) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChat(PlayerChat.PlayerChatAction.SELECT_AMOUNT, shop, shopAction));
                        player.closeInventory();
                    }, InventoryUtils.ActionClick.ALL);
                    case "CANCEL" -> getInventoryUtils().addAction(inventory, slot, player::closeInventory, InventoryUtils.ActionClick.ALL);
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

            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            if (StringUtils.equals(str, "shop-item")) {
                ItemStack item = shop.getItem().clone();

                if (item.getType().equals(Material.AIR)) return;

                item.setAmount(1);

                inventory.setItem(slot, item);
                continue;
            }

            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str);
            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            if (!StringUtils.equals(action, "NULL")) {
                switch (action) {
                    case "EDIT_DISPLAY" -> getInventoryUtils().addAction(inventory, slot, () -> openDisplayMenu(player, shop), InventoryUtils.ActionClick.ALL);
                    case "EDIT_TYPE" -> getInventoryUtils().addAction(inventory, slot, () -> openEditTypeMenu(player, shop), InventoryUtils.ActionClick.ALL);
                    case "EDIT_AMOUNT" -> getInventoryUtils().addAction(inventory, slot, () -> {
                        for (int i = 0; i < 25; ++i) {
                            player.sendMessage("");
                        }

                        for (String msg : Messages.EDIT_AMOUNT) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChat(PlayerChat.PlayerChatAction.EDIT_AMOUNT, shop));
                        player.closeInventory();
                    },InventoryUtils.ActionClick.ALL);
                    case "BUY_PRICE" -> getInventoryUtils().addAction(inventory, slot, () -> {
                        for (int i = 0; i < 25; ++i) {
                            player.sendMessage("");
                        }

                        for (String msg : Messages.BUY_PRICE) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChat(PlayerChat.PlayerChatAction.EDIT_BUY_PRICE, shop));
                        player.closeInventory();
                    }, InventoryUtils.ActionClick.ALL);
                    case "SELL_PRICE" -> getInventoryUtils().addAction(inventory, slot, () -> {
                        for (int i = 0; i < 25; ++i) {
                            player.sendMessage("");
                        }

                        for (String msg : Messages.SELL_PRICE) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChat(PlayerChat.PlayerChatAction.EDIT_SELL_PRICE, shop));
                        player.closeInventory();
                    }, InventoryUtils.ActionClick.ALL);
                    case "OPEN_CHEST" -> getInventoryUtils().addAction(inventory, slot, () -> {
                        Inventory chest = shop.getChest();
                        player.openInventory(chest);
                        getInventoryUtils().getCloseMenus().put(chest, shop);
                    }, InventoryUtils.ActionClick.ALL);
                    case "DELETE" -> getInventoryUtils().addAction(inventory, slot, () -> {
                        player.closeInventory();
                        shop.delete();
                    }, InventoryUtils.ActionClick.ALL);
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
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str);
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            if (!StringUtils.equals(action, "NULL")) {
                switch (action) {
                    case "SELECT_BUY" -> getInventoryUtils().addAction(inventory, slot, () -> {
                        shop.setType(ShopType.BUY);
                        openEditMenu(player, shop);
                    }, InventoryUtils.ActionClick.ALL);
                    case "SELECT_SELL" -> getInventoryUtils().addAction(inventory, slot, () -> {
                        shop.setType(ShopType.SELL);
                        openEditMenu(player, shop);
                    }, InventoryUtils.ActionClick.ALL);
                    case "SELECT_BOTH" -> getInventoryUtils().addAction(inventory, slot, () -> {
                        shop.setType(ShopType.BOTH);
                        openEditMenu(player, shop);
                    }, InventoryUtils.ActionClick.ALL);
                }
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
            ItemStack item = ItemBuilder.build(FileUtils.get().getFile(file).get(), "Inventory.items." + str);
            int slot = FileUtils.get().getInt(file, "Inventory.items." + str + ".slot");

            String action = FileUtils.get().getString(file, "Inventory.items." + str + ".action");

            if (!StringUtils.equals(action, "NULL")) {
                switch (action) {
                    case "SELECT_BUY" -> getInventoryUtils().addAction(inventory, slot, () -> {
                        for (int i = 0; i < 25; ++i) {
                            player.sendMessage("");
                        }

                        for (String msg : Messages.BUY_PRICE) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        creator.setType(ShopType.BUY);

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChat(PlayerChat.PlayerChatAction.EDIT_BUY_PRICE, null, creator));
                        player.closeInventory();
                    }, InventoryUtils.ActionClick.ALL);
                    case "SELECT_SELL" -> getInventoryUtils().addAction(inventory, slot, () -> {
                        for (int i = 0; i < 25; ++i) {
                            player.sendMessage("");
                        }

                        for (String msg : Messages.SELL_PRICE) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        creator.setType(ShopType.SELL);

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChat(PlayerChat.PlayerChatAction.EDIT_SELL_PRICE, null, creator));
                        player.closeInventory();
                    }, InventoryUtils.ActionClick.ALL);
                    case "SELECT_BOTH" -> getInventoryUtils().addAction(inventory, slot, () -> {
                        for (int i = 0; i < 25; ++i) {
                            player.sendMessage("");
                        }

                        for (String msg : Messages.BUY_PRICE) {
                            if (msg == null) break;

                            player.sendMessage(msg);
                        }

                        creator.setType(ShopType.BOTH);

                        PlayerChatListener.getPlayerChat().put(player, new PlayerChat(PlayerChat.PlayerChatAction.EDIT_BUY_PRICE, PlayerChat.PlayerChatAction.EDIT_SELL_PRICE, creator));
                        player.closeInventory();
                    }, InventoryUtils.ActionClick.ALL);
                }
            }

            inventory.setItem(slot, item);
        }

        player.openInventory(inventory);
    }

    private InventoryUtils getInventoryUtils() {
        return inventoryUtils;
    }
}