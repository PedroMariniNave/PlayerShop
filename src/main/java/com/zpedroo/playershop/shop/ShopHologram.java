package com.zpedroo.playershop.shop;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.zpedroo.playershop.PlayerShop;
import com.zpedroo.playershop.enums.ShopType;
import com.zpedroo.playershop.utils.config.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ShopHologram {

    private String[] hologramLines;
    private TextLine[] textLine;
    private Item displayItem;

    private Hologram hologram;

    public ShopHologram(Shop shop) {
        updateLines(shop);
        Bukkit.getScheduler().runTaskLater(PlayerShop.get(), () -> update(shop), 0L);
    }

    public void update(Shop shop) {
        if (hologram != null && hologram.isDeleted()) return;

        if (hologram == null) {
            hologram = HologramsAPI.createHologram(PlayerShop.get(), shop.getLocation().clone().add(0.5D, 3.75, 0.5D));
            textLine = new TextLine[hologramLines.length];

            for (int i = 0; i < hologramLines.length; i++) {
                textLine[i] = hologram.insertTextLine(i, shop.replace(hologramLines[i]));
            }

            hologram.getVisibilityManager().setVisibleByDefault(false);

            ItemStack shopItem = shop.getItem().clone();
            shopItem.setAmount(1);

            if (shop.getItem().getType().equals(Material.AIR)) return;

            displayItem = shop.getLocation().getWorld().dropItem(shop.getLocation().clone().add(0.5D, 1D, 0.5D), shopItem);
            displayItem.setVelocity(new Vector(0, 0.1, 0));
            displayItem.setPickupDelay(Integer.MAX_VALUE);
            displayItem.setCustomName("Shop Item");
            displayItem.setCustomNameVisible(false);
        } else {
            for (int i = 0; i < hologramLines.length; i++) {
                this.textLine[i].setText(shop.replace(hologramLines[i]));
            }
        }
    }

    public void updateLines(Shop shop) {
        ShopType type = shop.getType();

        switch (type) {
            case BUY -> this.hologramLines = Settings.BUY_HOLOGRAM;
            case SELL -> this.hologramLines = Settings.SELL_HOLOGRAM;
            case BOTH -> this.hologramLines = Settings.BOTH_HOLOGRAM;
        }
    }

    public void showTo(Player player) {
        if (hologram == null) return;

        hologram.getVisibilityManager().showTo(player);
    }

    public void hideTo(Player player) {
        if (hologram == null) return;

        hologram.getVisibilityManager().hideTo(player);
    }

    public void destroy() {
        if (hologram == null) return;

        hologram.delete();
        displayItem.remove();
        hologram = null;
    }
}