package com.zpedroo.playershop.objects;

import com.zpedroo.multieconomy.objects.Currency;
import com.zpedroo.playershop.enums.ShopType;
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
    private Currency currency;
    private BigInteger buyPrice;
    private BigInteger sellPrice;
    private Integer amount;
    private ShopType type;

    public ShopCreator(Location location, UUID ownerUUID, ItemStack item, Currency currency, BigInteger buyPrice, BigInteger sellPrice, Integer amount, ShopType type) {
        this.location = location;
        this.ownerUUID = ownerUUID;
        this.item = item;
        this.currency = currency;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.amount = amount;
        this.type = type;
    }

    public ShopType getType() {
        return type;
    }

    public Shop create() {
        return new Shop(location, ownerUUID, item, currency, buyPrice, sellPrice, amount, type, Bukkit.createInventory(null, 9*6, Settings.CHEST_INVENTORY_NAME), Material.GLASS);
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setBuyPrice(BigInteger price) {
        this.buyPrice = price;
    }

    public void setSellPrice(BigInteger price) {
        this.sellPrice = price;
    }

    public void setType(ShopType type) {
        this.type = type;
    }
}