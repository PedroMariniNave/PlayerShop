package com.zpedroo.playershop.objects;

import com.zpedroo.playershop.enums.ShopType;
import com.zpedroo.playershop.shop.Shop;
import com.zpedroo.playershop.utils.config.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.UUID;

public class ShopCreator {

    private UUID ownerUUID;
    private Location location;
    private ItemStack item;
    private BigInteger buyPrice;
    private BigInteger sellPrice;
    private Integer amount;
    private ShopType type;

    public ShopCreator(Location location, UUID ownerUUID, ItemStack item, BigInteger buyPrice, BigInteger sellPrice, Integer amount, ShopType type) {
        this.location = location;
        this.ownerUUID = ownerUUID;
        this.item = item;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.amount = amount;
        this.type = type;
    }

    private UUID getOwnerUUID() {
        return ownerUUID;
    }

    private Location getLocation() {
        return location;
    }

    private ItemStack getItem() {
        return item;
    }

    private BigInteger getBuyPrice() {
        return buyPrice;
    }

    private BigInteger getSellPrice() {
        return sellPrice;
    }

    private Integer getAmount() {
        return amount;
    }

    private ShopType getType() {
        return type;
    }

    public Shop create() {
        return new Shop(getLocation(), getOwnerUUID(), getItem(), getBuyPrice(), getSellPrice(), getAmount(), getType(), Bukkit.createInventory(null, 9*6, Settings.CHEST_INVENTORY_NAME), Material.GLASS);
    }

    public void setBuyPrice(BigInteger value) {
        this.buyPrice = value;
    }

    public void setSellPrice(BigInteger value) {
        this.sellPrice = value;
    }

    public void setType(ShopType type) {
        this.type = type;
    }
}