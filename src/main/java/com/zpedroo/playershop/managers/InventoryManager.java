package com.zpedroo.playershop.managers;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryManager {

    public static boolean containsItem(ItemStack item, Inventory inventory, int amount) {
        int found = 0;

        for (ItemStack items : inventory.getContents()) {
            if (item.isSimilar(items)) {
                found += items.getAmount();
            }
        }

        return found >= amount;
    }

    public static boolean hasSpace(Inventory inventory, int slotsFree) {
        int free = 0;

        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType().equals(Material.AIR)) free += 1;
        }

        return slotsFree >= free;
    }
}
