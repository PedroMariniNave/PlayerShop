package com.zpedroo.playershop.listeners;

import com.zpedroo.playershop.enums.ShopAction;
import com.zpedroo.playershop.enums.ShopType;
import com.zpedroo.playershop.managers.ShopManager;
import com.zpedroo.playershop.objects.Shop;
import com.zpedroo.playershop.utils.menu.Menus;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ShopListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        Shop shop = ShopManager.getInstance().getShop(event.getClickedBlock().getLocation());
        if (shop == null) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        if (player.getUniqueId().equals(shop.getOwnerUUID())) {
            Menus.getInstance().openEditMenu(player, shop);
            return;
        }

        ShopType type = shop.getType();

        switch (type) {
            case BUY:
                Menus.getInstance().openShopMenu(player, shop, shop.getDefaultAmount(), ShopAction.BUY);
                break;
            case SELL:
                Menus.getInstance().openShopMenu(player, shop, shop.getDefaultAmount(), ShopAction.SELL);
                break;
            case BOTH:
                Menus.getInstance().openChooseMenu(player, shop);
                break;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        Shop shop = ShopManager.getInstance().getShop(block.getLocation());
        if (shop == null) return;

        Player player = event.getPlayer();
        if (!player.getUniqueId().equals(shop.getOwnerUUID())) {
            event.setCancelled(true);
            return;
        }

        shop.delete();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDespawn(ItemDespawnEvent event) {
        if (!event.getEntity().hasMetadata("***")) return;

        event.setCancelled(true);
    }
}