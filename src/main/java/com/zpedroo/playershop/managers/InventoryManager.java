package com.zpedroo.playershop.managers;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryManager {

    public static boolean containsItem(ItemStack item, Inventory inventory, int amount) {
        int found = 0;

        for (ItemStack items : inventory.getContents()) {
            if (items == null || item.getType().equals(Material.AIR)) continue;
            if (items.isSimilar(item)) {
                found += items.getAmount();
            }
        }

        return found >= amount;
    }

    public static int getFreeSpace(Inventory inventory, ItemStack item) {
        int free = 0;

        for (ItemStack items : inventory.getContents()) {
            if (items == null || items.getType().equals(Material.AIR)) {
                free += item.getMaxStackSize();
                continue;
            }

            if (!items.isSimilar(item)) continue;

            free += item.getMaxStackSize() - items.getAmount();
        }

        return free;
    }
}
