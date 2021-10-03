package com.zpedroo.playershop.objects;

import com.zpedroo.playershop.PlayerShop;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ShopStand {

    private ArmorStand armorStand;
    private Shop shop;

    public ShopStand(Shop shop) {
        this.shop = shop;
        PlayerShop.get().getServer().getScheduler().runTaskLater(PlayerShop.get(), this::create, 0L);
    }

    private void create() {
        // create inside the block (prevent visual bugs)
        armorStand = (ArmorStand) shop.getLocation().getWorld().spawnEntity(shop.getLocation().clone().add(0.5D, -0.290D, 0.5D), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setNoDamageTicks(Integer.MAX_VALUE);
        armorStand.getEquipment().setHelmet(new ItemStack(shop.getDisplay()));
        armorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.setCustomName("Shop Stand");
        armorStand.setCustomNameVisible(false);
    }

    public void setDisplay(Material display) {
        this.armorStand.getEquipment().setHelmet(new ItemStack(display));
    }

    public void destroy() {
        if (armorStand == null) return;

        armorStand.remove();
        armorStand = null;
    }
}