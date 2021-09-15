package com.zpedroo.playershop.shop;

import com.zpedroo.playershop.enums.ShopType;
import com.zpedroo.playershop.hooks.Vault;
import com.zpedroo.playershop.managers.InventoryManager;
import com.zpedroo.playershop.managers.ShopManager;
import com.zpedroo.playershop.utils.config.Messages;
import com.zpedroo.playershop.utils.formatter.NumberFormatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.UUID;

public class Shop {

    private Location location;
    private UUID ownerUUID;
    private ItemStack item;
    private BigInteger buyPrice;
    private BigInteger sellPrice;
    private Integer amount;
    private ShopType type;
    private Inventory chest;
    private Material display;
    private ShopHologram hologram;
    private ShopStand armorStand;
    private Boolean update;

    public Shop(Location location, UUID ownerUUID, ItemStack item, BigInteger buyPrice, BigInteger sellPrice, Integer amount, ShopType type, Inventory chest, Material display) {
        this.location = location;
        this.ownerUUID = ownerUUID;
        this.item = item;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.amount = amount;
        this.type = type;
        this.chest = chest;
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

    public BigInteger getBuyPrice() {
        return buyPrice;
    }

    public BigInteger getSellPrice() {
        return sellPrice;
    }

    public Integer getAmount() {
        return amount;
    }

    public ShopType getType() {
        return type;
    }

    public Inventory getChest() {
        return chest;
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
                Bukkit.getOfflinePlayer(getOwnerUUID()).getName(),
                NumberFormatter.getInstance().format(getBuyPrice()),
                NumberFormatter.getInstance().format(getSellPrice()),
                NumberFormatter.getInstance().formatDecimal((double) getAmount()),
                (getItem().hasItemMeta() && getItem().getItemMeta().hasDisplayName()) ? getItem().getItemMeta().getDisplayName() : getItem().getType().toString()
        });
    }

    public void setChest(Inventory chest) {
        this.chest = chest;
        this.update = true;
        // getHolographic().update(this);
    }

    public void setDisplay(Material display) {
        this.display = display;
        getArmorStand().setDisplay(getDisplay());
    }

    public void setBuyPrice(BigInteger value) {
        this.buyPrice = value;
        this.update = true;
        getHologram().update(this);
    }

    public void setSellPrice(BigInteger value) {
        this.sellPrice = value;
        this.update = true;
        getHologram().update(this);
    }

    public void setAmount(Integer value) {
        this.amount = value;
        this.update = true;
        getHologram().update(this);
    }

    public void setType(ShopType type) {
        this.type = type;
        this.update = true;
        getHologram().updateLines(this);
        getHologram().update(this);
    }

    public void delete() {
        String location = ShopManager.getInstance().serializeLocation(getLocation());

        ShopManager.getInstance().getDataCache().getDeletedShops().add(location);
        ShopManager.getInstance().getDataCache().getShops().remove(location);

        getHologram().destroy();
        getArmorStand().destroy();
    }

    public void buy(Player player, int amount) {
        if (amount <= 0) amount = getAmount();

        int needSlots;

        if (getItem().getType().getMaxStackSize() == 1) {
            needSlots = getAmount();
        } else {
            needSlots = getAmount() >= 64 ? getAmount() / getItem().getMaxStackSize() : 1;
        }

        if (!InventoryManager.hasSpace(player.getInventory(), needSlots)) {
            player.sendMessage(Messages.NEED_SPACE_PLAYER);
            player.closeInventory();
            return;
        }

        if (getBuyPrice() == null || getBuyPrice().signum() <= 0) {
            player.sendMessage(Messages.INVALID_PRICE);
            player.closeInventory();
            return;
        }

        if (!InventoryManager.containsItem(getItem(), getChest(), amount)) {
            player.sendMessage(Messages.INSUFFICIENT_ITEMS_SHOP);
            player.closeInventory();
            return;
        }

        final BigInteger finalPrice = getBuyPrice().multiply(BigInteger.valueOf(amount));

        if (Vault.getMoney(player) < finalPrice.doubleValue()) {
            player.sendMessage(Messages.INSUFFICIENT_MONEY_PLAYER);
            player.closeInventory();
            return;
        }

        ItemStack item = getItem().clone();
        item.setAmount(1);

        for (int i = 0; i < amount; ++i) {
            getChest().removeItem(item);
            player.getInventory().addItem(item);
        }

        Vault.removeMoney(player, finalPrice.doubleValue());
        Vault.addMoney(player, finalPrice.doubleValue());

        player.sendMessage(Messages.BUY_SUCESSFUL);
        player.closeInventory();
    }

    public void sell(Player player, int amount) {
        if (amount <= 0) amount = getAmount();

        if (!InventoryManager.containsItem(getItem(), player.getInventory(), amount)) {
            player.sendMessage(Messages.INSUFFICIENT_ITEMS_PLAYER);
            player.closeInventory();
            return;
        }

        if (getSellPrice() == null || getSellPrice().signum() <= 0) {
            player.sendMessage(Messages.INVALID_PRICE);
            player.closeInventory();
            return;
        }

        int needSlots;

        if (getItem().getType().getMaxStackSize() == 1) {
            needSlots = amount;
        } else {
            needSlots = amount >= 64 ? amount / getItem().getType().getMaxStackSize() : 1;
        }

        if (!InventoryManager.hasSpace(getChest(), needSlots)) {
            player.sendMessage(Messages.NEED_SPACE_SHOP);
            player.closeInventory();
            return;
        }

        final BigInteger finalPrice = getBuyPrice().multiply(BigInteger.valueOf(amount));

        if (Vault.getMoney(Bukkit.getOfflinePlayer(getOwnerUUID())) < finalPrice.doubleValue()) {
            player.sendMessage(Messages.INSUFFICIENT_MONEY_OWNER);
            player.closeInventory();
            return;
        }

        ItemStack item = getItem().clone();
        item.setAmount(1);

        // fix stack
        for (int i = 0; i < amount; ++i) {
            player.getInventory().removeItem(item);
            getChest().addItem(item);
        }

        Vault.removeMoney(Bukkit.getOfflinePlayer(getOwnerUUID()), finalPrice.doubleValue());
        Vault.addMoney(player, finalPrice.doubleValue());

        player.sendMessage(Messages.SELL_SUCESSFUL);
        player.closeInventory();
    }

    public void setQueueUpdate(Boolean status) {
        this.update = status;
    }
}