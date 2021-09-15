package com.zpedroo.playershop.utils.menu;

import com.zpedroo.playershop.PlayerShop;
import com.zpedroo.playershop.shop.Shop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryUtils {

    private static InventoryUtils instance;
    public static InventoryUtils getInstance() { return instance; }

    private HashMap<Inventory, List<Action>> inventoryActions;
    private HashMap<Inventory, Shop> closeMenus;

    public InventoryUtils() {
        instance = this;
        this.inventoryActions = new HashMap<>(64);
        this.closeMenus = new HashMap<>(64);
        PlayerShop.get().getServer().getPluginManager().registerEvents(new ActionListeners(), PlayerShop.get()); // register inventory listener
    }

    public void addAction(Inventory inventory, Integer slot, Runnable action, ActionClick click) {
        List<Action> actions = getInventoryActions().containsKey(inventory) ? getInventoryActions().get(inventory) : new ArrayList<>(40);

        actions.add(new Action(click, slot, action));

        getInventoryActions().put(inventory, actions);
    }

    public HashMap<Inventory, List<Action>> getInventoryActions() {
        return inventoryActions;
    }

    public HashMap<Inventory, Shop> getCloseMenus() {
        return closeMenus;
    }

    public Action getAction(Inventory inventory, Integer slot, ActionClick click) {
        for (Action action : getInventoryActions().get(inventory)) {
            if (action == null) continue;

            if (action.getClick() == click && action.getSlot().equals(slot)) return action;
        }

        return null;
    }

    public static class Action {

        private ActionClick click;
        private Integer slot;
        private Runnable action;

        public Action(ActionClick click, Integer slot, Runnable action) {
            this.click = click;
            this.slot = slot;
            this.action = action;
        }

        public ActionClick getClick() {
            return click;
        }

        public Integer getSlot() {
            return slot;
        }

        public Runnable getAction() {
            return action;
        }

        public void run() {
            if (action == null) return;

            action.run();
        }
    }

    public class ActionListeners implements Listener {

        @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onClick(InventoryClickEvent event) {
            if (!InventoryUtils.getInstance().getInventoryActions().containsKey(event.getInventory())) return;

            event.setCancelled(true);

            Action action = getAction(event.getInventory(), event.getSlot(), ActionClick.ALL);

            if (action == null) {
                // try to found individual actions
                switch (event.getClick()) {
                    case LEFT:
                    case SHIFT_LEFT:
                        action = getAction(event.getInventory(), event.getSlot(), ActionClick.LEFT);
                        break;
                    case RIGHT:
                    case SHIFT_RIGHT:
                        action = getAction(event.getInventory(), event.getSlot(), ActionClick.RIGHT);
                        break;
                }
            }

            if (action == null) return;

            action.run();
        }

        @EventHandler
        public void onClose(InventoryCloseEvent event) {
            InventoryUtils.getInstance().getInventoryActions().remove(event.getInventory());
            if (!getCloseMenus().containsKey(event.getInventory())) return;

            Shop shop = getCloseMenus().remove(event.getInventory());
            if (shop == null) return;

            shop.setChest(event.getInventory());
        }
    }

    public enum ActionClick {
        LEFT,
        RIGHT,
        ALL
    }
}