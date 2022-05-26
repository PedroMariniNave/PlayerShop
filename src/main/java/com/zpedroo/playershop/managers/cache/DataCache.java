package com.zpedroo.playershop.managers.cache;

import com.zpedroo.playershop.objects.Shop;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataCache {

    private Map<Location, Shop> shops;
    private final Set<Location> deletedShops;

    public DataCache() {
        this.deletedShops = new HashSet<>(16);
    }

    public Map<Location, Shop> getShops() {
        return shops;
    }

    public Set<Location> getDeletedShops() {
        return deletedShops;
    }

    public void setShops(HashMap<Location, Shop> shops) {
        this.shops = shops;
    }

    public void addShop(Shop shop) {
        shops.put(shop.getLocation(), shop);
    }
}