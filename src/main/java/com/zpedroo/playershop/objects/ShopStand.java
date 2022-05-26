package com.zpedroo.playershop.objects;

import com.zpedroo.playershop.PlayerShop;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class ShopStand {

    private UUID armorStandId;
    private final Shop shop;

    public ShopStand(Shop shop) {
        this.shop = shop;
        PlayerShop.get().getServer().getScheduler().runTaskLater(PlayerShop.get(), this::create, 0L);
    }

    public void create() {
        ArmorStand armorStand = getArmorStand();
        if (armorStand != null && !armorStand.isDead()) return;

        // create inside the block (prevent visual bugs)
        armorStand = (ArmorStand) shop.getLocation().getWorld().spawnEntity(shop.getLocation().clone().add(0.5D, -0.290D, 0.5D), EntityType.ARMOR_STAND);
        NBTEditor.set(armorStand, true, "Invulnerable");
        NBTEditor.set(armorStand, 1, "DisabledSlots");
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setNoDamageTicks(Integer.MAX_VALUE);
        armorStand.getEquipment().setHelmet(new ItemStack(shop.getDisplay()));
        armorStand.setCustomName("Shop Stand");
        armorStand.setCustomNameVisible(false);
        this.armorStandId = armorStand.getUniqueId();
    }

    public void setDisplay(Material display) {
        ArmorStand armorStand = getArmorStand();
        if (armorStand == null || armorStand.isDead()) return;

        armorStand.getEquipment().setHelmet(new ItemStack(display));
    }

    public void remove() {
        shop.getLocation().getChunk().load();

        ArmorStand armorStand = getArmorStand();
        if (armorStand == null || armorStand.isDead()) return;

        armorStand.remove();
        armorStand.setVisible(true);
        this.armorStandId = null;
    }

    private ArmorStand getArmorStand() {
        if (armorStandId == null) return null;

        Optional<Entity> armorStand = shop.getLocation().getWorld().getNearbyEntities(shop.getLocation(), 2, 2, 2).stream().filter(
                entity -> entity.getUniqueId().equals(armorStandId)
                        || entity.getCustomName().equals("Shop Stand")
        ).findFirst();
        
        return (ArmorStand) armorStand.orElse(null);
    }
}