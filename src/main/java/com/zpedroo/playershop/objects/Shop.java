package com.zpedroo.playershop.objects;

import com.zpedroo.multieconomy.api.CurrencyAPI;
import com.zpedroo.multieconomy.objects.Currency;
import com.zpedroo.playershop.enums.ShopType;
import com.zpedroo.playershop.managers.InventoryManager;
import com.zpedroo.playershop.managers.ShopManager;
import com.zpedroo.playershop.utils.config.Messages;
import com.zpedroo.playershop.utils.formatter.NumberFormatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.UUID;

public class Shop {

    private final Location location;
    private final UUID ownerUUID;
    private final ItemStack item;
    private Currency currency;
    private BigInteger buyPrice;
    private BigInteger sellPrice;
    private Integer defaultAmount;
    private ShopType type;
    private Inventory chestInventory;
    private Material display;
    private final ShopHologram hologram;
    private final ShopStand armorStand;
    private Boolean update;

    public Shop(Location location, UUID ownerUUID, ItemStack item, Currency currency, BigInteger buyPrice, BigInteger sellPrice, Integer defaultAmount, ShopType type, Inventory chestInventory, Material display) {
        this.location = location;
        this.ownerUUID = ownerUUID;
        this.item = item;
        this.currency = currency;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.defaultAmount = defaultAmount;
        this.type = type;
        this.chestInventory = chestInventory;
        this.display = display;
        this.hologram = new ShopHologram(this);
        this.armorStand = new ShopStand(this);
        this.update = false;
    }

    public Location getLocation() {
        return location;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public ItemStack getItem() {
        return item;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigInteger getBuyPrice() {
        return buyPrice;
    }

    public BigInteger getSellPrice() {
        return sellPrice;
    }

    public Integer getDefaultAmount() {
        return defaultAmount;
    }

    public ShopType getType() {
        return type;
    }

    public Inventory getChestInventory() {
        return chestInventory;
    }

    public Material getDisplay() {
        return display;
    }

    public ShopHologram getHologram() {
        return hologram;
    }

    public ShopStand getArmorStand() {
        return armorStand;
    }

    public Boolean isQueueUpdate() {
        return update;
    }

    public String replace(String text) {
        if (text == null || text.isEmpty()) return "";

        return StringUtils.replaceEach(text, new String[] {
                "{player}",
                "{buy_price}",
                "{sell_price}",
                "{amount}",
                "{item}"
        }, new String[] {
                Bukkit.getOfflinePlayer(ownerUUID).getName(),
                StringUtils.replaceEach(currency.getAmountDisplay(), new String[]{ "{amount}" }, new String[]{ NumberFormatter.getInstance().format(buyPrice) }),
                StringUtils.replaceEach(currency.getAmountDisplay(), new String[]{ "{amount}" }, new String[]{ NumberFormatter.getInstance().format(sellPrice) }),
                NumberFormatter.getInstance().formatDecimal(defaultAmount.doubleValue()),
                (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : item.getType().toString()
        });
    }

    public void setChestInventory(Inventory chestInventory) {
        this.chestInventory = chestInventory;
        this.update = true;
        // getHolographic().update(this);
    }

    public void setDisplay(Material display) {
        this.display = display;
        this.armorStand.setDisplay(display);
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
        this.update = true;
        this.hologram.updateHologramAndItem();
    }

    public void setBuyPrice(BigInteger price) {
        this.buyPrice = price;
        this.update = true;
        this.hologram.updateHologramAndItem();
    }

    public void setSellPrice(BigInteger price) {
        this.sellPrice = price;
        this.update = true;
        this.hologram.updateHologramAndItem();
    }

    public void setDefaultAmount(Integer amount) {
        this.defaultAmount = amount;
        this.update = true;
        this.hologram.updateHologramAndItem();
    }

    public void setType(ShopType type) {
        this.type = type;
        this.update = true;
        this.hologram.updateLines();
        this.hologram.updateHologramAndItem();
    }

    public void delete() {
        ShopManager.getInstance().getCache().getDeletedShops().add(location);
        ShopManager.getInstance().getCache().getShops().remove(location);

        hologram.removeHologramAndItem();
        armorStand.remove();

        for (ItemStack item : chestInventory.getContents()) {
            if (item == null || item.getType().equals(Material.AIR)) continue;

            location.getWorld().dropItemNaturally(location, item);
        }
    }

    public void buy(Player player, int amount) {
        if (amount <= 0) amount = defaultAmount;

        if (InventoryManager.getFreeSpace(player.getInventory(), item) < amount) {
            player.sendMessage(Messages.NEED_SPACE_PLAYER);
            player.closeInventory();
            return;
        }

        if (buyPrice == null || buyPrice.signum() <= 0) {
            player.sendMessage(Messages.INVALID_PRICE);
            player.closeInventory();
            return;
        }

        if (!InventoryManager.containsItem(item, chestInventory, amount)) {
            player.sendMessage(Messages.INSUFFICIENT_ITEMS_SHOP);
            player.closeInventory();
            return;
        }

        final BigInteger finalPrice = buyPrice.multiply(BigInteger.valueOf(amount));
        BigInteger currencyAmount = CurrencyAPI.getCurrencyAmount(player, currency);

        if (currencyAmount.compareTo(finalPrice) < 0) {
            player.sendMessage(StringUtils.replaceEach(Messages.INSUFFICIENT_CURRENCY_PLAYER, new String[]{
                    "{has}",
                    "{need}"
            }, new String[]{
                    NumberFormatter.getInstance().format(currencyAmount),
                    NumberFormatter.getInstance().format(finalPrice)
            }));
            player.closeInventory();
            return;
        }

        ItemStack item = this.item.clone();
        item.setAmount(1);

        for (int i = 0; i < amount; ++i) {
            chestInventory.removeItem(item);
            player.getInventory().addItem(item);
        }

        CurrencyAPI.removeCurrencyAmount(player, currency, finalPrice);
        CurrencyAPI.addCurrencyAmount(Bukkit.getOfflinePlayer(ownerUUID), currency, finalPrice);

        player.sendMessage(Messages.BUY_SUCESSFUL);
        player.closeInventory();
    }

    public void sell(Player player, int amount) {
        if (amount <= 0) amount = defaultAmount;

        if (!InventoryManager.containsItem(item, player.getInventory(), amount)) {
            player.sendMessage(Messages.INSUFFICIENT_ITEMS_PLAYER);
            player.closeInventory();
            return;
        }

        if (sellPrice == null || sellPrice.signum() <= 0) {
            player.sendMessage(Messages.INVALID_PRICE);
            player.closeInventory();
            return;
        }

        if (InventoryManager.getFreeSpace(chestInventory, item) < amount) {
            player.sendMessage(Messages.NEED_SPACE_SHOP);
            player.closeInventory();
            return;
        }

        OfflinePlayer owner = Bukkit.getOfflinePlayer(ownerUUID);
        final BigInteger finalPrice = sellPrice.multiply(BigInteger.valueOf(amount));
        BigInteger currencyAmount = CurrencyAPI.getCurrencyAmount(owner, currency);

        if (currencyAmount.compareTo(finalPrice) < 0) {
            player.sendMessage(StringUtils.replaceEach(Messages.INSUFFICIENT_CURRENCY_OWNER, new String[]{
                    "{has}",
                    "{need}"
            }, new String[]{
                    NumberFormatter.getInstance().format(currencyAmount),
                    NumberFormatter.getInstance().format(finalPrice)
            }));
            player.closeInventory();
            return;
        }

        ItemStack item = this.item.clone();
        item.setAmount(1);

        // fix stack
        for (int i = 0; i < amount; ++i) {
            player.getInventory().removeItem(item);
            chestInventory.addItem(item);
        }

        CurrencyAPI.removeCurrencyAmount(owner, currency, finalPrice);
        CurrencyAPI.addCurrencyAmount(player, currency, finalPrice);

        player.sendMessage(Messages.SELL_SUCESSFUL);
        player.closeInventory();
    }

    public void setQueueUpdate(Boolean status) {
        this.update = status;
    }
}