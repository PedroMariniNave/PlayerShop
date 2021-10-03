package com.zpedroo.playershop.utils.cache;

import com.zpedroo.playershop.objects.Shop;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DataCache {

    private HashMap<Location, Shop> shops;
    private Set<Location> deletedShops;

    public DataCache() {
        this.shops = new HashMap<>(5120);
        this.deletedShops = new HashSet<>(2560);
    }

    public HashMap<Location, Shop> getShops() {
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