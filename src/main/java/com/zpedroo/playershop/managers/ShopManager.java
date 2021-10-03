package com.zpedroo.playershop.managers;

import com.zpedroo.playershop.PlayerShop;
import com.zpedroo.playershop.mysql.DBConnection;
import com.zpedroo.playershop.objects.Shop;
import com.zpedroo.playershop.utils.cache.DataCache;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;

public class ShopManager {

    private static ShopManager instance;
    public static ShopManager getInstance() { return instance; }

    private DataCache dataCache;

    public ShopManager() {
        instance = this;
        this.dataCache = new DataCache();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (!StringUtils.equals(entity.getName(), "Shop Stand") && !StringUtils.equals(entity.getName(), "Shop Item")) continue;

                        entity.remove();
                    }
                }

                loadShops();
            }
        }.runTaskLaterAsynchronously(PlayerShop.get(), 100L);
    }

    public Shop getShop(Location location) {
        return dataCache.getShops().get(location);
    }

    public void saveAll() {
        new HashSet<>(dataCache.getDeletedShops()).forEach(shop -> {
            DBConnection.getInstance().getDBManager().deleteShop(shop);
        });

        dataCache.getDeletedShops().clear();

        new HashSet<>(dataCache.getShops().values()).forEach(shop -> {
            if (shop == null) return;
            if (!shop.isQueueUpdate()) return;

            DBConnection.getInstance().getDBManager().saveShop(shop);
            shop.setQueueUpdate(false);
        });
    }

    public String serializeLocation(Location location) {
        if (location == null) return null;

        StringBuilder serialized = new StringBuilder(4);
        serialized.append(location.getWorld().getName());
        serialized.append("#" + location.getX());
        serialized.append("#" + location.getY());
        serialized.append("#" + location.getZ());

        return serialized.toString();
    }

    public Location deserializeLocation(String location) {
        if (location == null) return null;

        String[] locationSplit = location.split("#");
        double x = Double.parseDouble(locationSplit[1]);
        double y = Double.parseDouble(locationSplit[2]);
        double z = Double.parseDouble(locationSplit[3]);

        return new Location(Bukkit.getWorld(locationSplit[0]), x, y, z);
    }

    private void loadShops() {
        dataCache.setShops(DBConnection.getInstance().getDBManager().getShops());
    }

    public DataCache getCache() {
        return dataCache;
    }
}