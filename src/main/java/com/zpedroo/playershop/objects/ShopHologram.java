package com.zpedroo.playershop.objects;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.zpedroo.playershop.PlayerShop;
import com.zpedroo.playershop.enums.ShopType;
import com.zpedroo.playershop.utils.config.Settings;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Optional;
import java.util.UUID;

public class ShopHologram {

    private final Shop shop;

    private String[] hologramLines;
    private TextLine[] textLines;
    private UUID displayItemId;

    private Hologram hologram;
    private int viewers = 0;

    public ShopHologram(Shop shop) {
        this.shop = shop;
        this.updateLines();
        this.updateHologramAndItem();
    }

    public void updateHologramAndItem() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateHologram();
                spawnItem();
            }
        }.runTaskLater(PlayerShop.get(), 0L);
    }

    public void showHologramTo(Player player) {
        ++viewers;
        createHologram();
        spawnItem();

        hologram.getVisibilityManager().showTo(player);
    }

    public void hideHologramTo(Player player) {
        if (hologram == null) return;
        if (--viewers <= 0) {
            removeHologram();
            return;
        }

        hologram.getVisibilityManager().hideTo(player);
    }

    private void updateHologram() {
        if (hologram == null || hologram.isDeleted()) return;

        for (int i = 0; i < hologramLines.length; i++) {
            textLines[i].setText(shop.replace(hologramLines[i]));
        }
    }

    private void createHologram() {
        if (hologram != null && !hologram.isDeleted()) return;

        hologram = HologramsAPI.createHologram(PlayerShop.get(), shop.getLocation().clone().add(0.5D, 3.65, 0.5D));
        hologram.getVisibilityManager().setVisibleByDefault(false);

        textLines = new TextLine[hologramLines.length];

        for (int i = 0; i < hologramLines.length; i++) {
            textLines[i] = hologram.insertTextLine(i, shop.replace(hologramLines[i]));
        }
    }

    private void removeHologram() {
        if (hologram == null || hologram.isDeleted()) return;

        hologram.delete();
        hologram = null;
    }

    private void spawnItem() {
        Item displayItem = getDisplayItem();
        if (displayItem != null && !displayItem.isDead()) return;

        ItemStack shopItem = shop.getItem().clone();
        shopItem.setAmount(1);

        if (shopItem.getType().equals(Material.AIR)) return;

        displayItem = shop.getLocation().getWorld().dropItem(shop.getLocation().clone().add(0.5D, 1D, 0.5D), shopItem);
        displayItem.setVelocity(new Vector(0, 0.1, 0));
        displayItem.setPickupDelay(Integer.MAX_VALUE);
        displayItem.setCustomName("Shop Item");
        displayItem.setCustomNameVisible(false);
        displayItem.setMetadata("***", new FixedMetadataValue(PlayerShop.get(), true));
        this.displayItemId = displayItem.getUniqueId();
    }

    private void removeItem() {
        shop.getLocation().getChunk().load();

        Item displayItem = getDisplayItem();
        if (displayItem == null || displayItem.isDead()) return;

        displayItem.remove();
        this.displayItemId = null;
    }

    public void updateLines() {
        ShopType type = shop.getType();

        switch (type) {
            case BUY:
                this.hologramLines = Settings.BUY_HOLOGRAM;
                break;
            case SELL:
                this.hologramLines = Settings.SELL_HOLOGRAM;
                break;
            case BOTH:
                this.hologramLines = Settings.BOTH_HOLOGRAM;
                break;
        }
    }

    public void removeHologramAndItem() {
        removeHologram();
        removeItem();
    }

    private Item getDisplayItem() {
        if (displayItemId == null) return null;

        Optional<Entity> displayItem = shop.getLocation().getWorld().getNearbyEntities(shop.getLocation(), 2, 2, 2).stream().filter(
                entity -> entity.getUniqueId().equals(displayItemId)
                        || entity.getCustomName().equals("Shop Item")
        ).findFirst();

        return (Item) displayItem.orElse(null);
    }
}