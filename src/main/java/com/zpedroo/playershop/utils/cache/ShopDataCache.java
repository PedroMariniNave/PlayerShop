package com.zpedroo.playershop.utils.cache;

import com.zpedroo.playershop.managers.ShopManager;
import com.zpedroo.playershop.shop.Shop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ShopDataCache {

    /*
     * All shops
     *
     * Key = Serialized location
     * Value = Shop
     */
    private HashMap<String, Shop> shops;

    /*
     * All deleted shops
     *
     * Contains all serialized locations
     */
    private Set<String> deletedShops;

    /*
     * New constructor
     */
    public ShopDataCache() {
        this.shops = new HashMap<>(5120);
        this.deletedShops = new HashSet<>(2560);
    }

    /*
     * Returns all shops Map
     */
    public HashMap<String, Shop> getShops() {
        return shops;
    }

    /*
     * Returns all deleted shops Set
     */
    public Set<String> getDeletedShops() {
        return deletedShops;
    }

    /*
     * Set the new list of shops
     * This is useful to load
     * all shops from database
     */
    public void setShops(HashMap<String, Shop> shops) {
        this.shops = shops;
    }

    /*
     * Adds a new shop to cache
     */
    public void addShop(Shop shop) {
        getShops().put(ShopManager.getInstance().serializeLocation(shop.getLocation()), shop);
    }
}