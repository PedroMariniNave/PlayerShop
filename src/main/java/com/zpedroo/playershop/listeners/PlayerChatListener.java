package com.zpedroo.playershop.listeners;

import br.com.devpaulo.legendchat.api.events.ChatMessageEvent;
import com.zpedroo.playershop.PlayerShop;
import com.zpedroo.playershop.enums.ShopAction;
import com.zpedroo.playershop.managers.ShopManager;
import com.zpedroo.playershop.shop.Shop;
import com.zpedroo.playershop.objects.ShopCreator;
import com.zpedroo.playershop.utils.chat.PlayerChat;
import com.zpedroo.playershop.utils.config.Messages;
import com.zpedroo.playershop.utils.formatter.NumberFormatter;
import com.zpedroo.playershop.utils.menu.Menus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.math.BigInteger;
import java.util.HashMap;

public class PlayerChatListener implements Listener {

    private static final HashMap<Player, PlayerChat> playerChat;

    static {
        playerChat = new HashMap<>(32);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(ChatMessageEvent event) {
        if (!getPlayerChat().containsKey(event.getSender())) return;

        event.setCancelled(true);

        PlayerChat playerChat = getPlayerChat().remove(event.getSender());
        Player player = event.getSender();
        PlayerChat.PlayerChatAction action = playerChat.getAction();
        PlayerChat.PlayerChatAction next = playerChat.getNext();
        ShopAction shopAction = playerChat.getShopAction();
        ShopCreator creator = playerChat.getCreator();
        final Shop shop = playerChat.getShop();

        BigInteger value = NumberFormatter.getInstance().filter(event.getMessage());

        if (value.signum() <= 0) {
            player.sendMessage(Messages.INVALID_VALUE);
            return;
        }

        switch (action) {
            case EDIT_BUY_PRICE -> {
                if (shop != null) {
                    shop.setBuyPrice(value);
                    PlayerShop.get().getServer().getScheduler().runTaskLater(PlayerShop.get(), () -> Menus.getInstance().openEditMenu(player, shop), 0L);
                    break;
                }
                creator.setBuyPrice(value);
            }
            case EDIT_SELL_PRICE -> {
                if (shop != null) {
                    shop.setSellPrice(value);
                    PlayerShop.get().getServer().getScheduler().runTaskLater(PlayerShop.get(), () -> Menus.getInstance().openEditMenu(player, shop), 0L);
                    break;
                }
                creator.setSellPrice(value);
            }
            case EDIT_AMOUNT -> {
                int limit = 36;

                if (shop.getItem().getMaxStackSize() == 64) limit = 2304;
                if (value.compareTo(BigInteger.valueOf(limit)) > 0) value = BigInteger.valueOf(limit);

                shop.setAmount(value.intValue());
                PlayerShop.get().getServer().getScheduler().runTaskLater(PlayerShop.get(), () -> Menus.getInstance().openEditMenu(player, shop), 0L);
            }
            case SELECT_AMOUNT -> {
                int limit = 36;

                if (shop.getItem().getMaxStackSize() == 64) limit = 2304;
                if (value.compareTo(BigInteger.valueOf(limit)) > 0) value = BigInteger.valueOf(limit);

                shop.setAmount(value.intValue());
                final BigInteger finalValue = value;
                PlayerShop.get().getServer().getScheduler().runTaskLater(PlayerShop.get(), () -> Menus.getInstance().openShopMenu(player, shop, finalValue.intValue(), shopAction), 0L);
            }
        }

        if (creator == null) return;
        if (next == null) {
            Shop toCreate = creator.create();
            toCreate.setQueueUpdate(true);
            ShopManager.getInstance().getDataCache().addShop(toCreate);
            player.sendMessage(Messages.SHOP_CREATED);
            return;
        }

        switch (next) {
            case EDIT_SELL_PRICE -> {
                for (int i = 0; i < 25; ++i) {
                    player.sendMessage("");
                }
                for (String msg : Messages.SELL_PRICE) {
                    if (msg == null) break;

                    player.sendMessage(msg);
                }
                getPlayerChat().put(player, new PlayerChat(next, null, creator));
            }
        }
    }

    public static HashMap<Player, PlayerChat> getPlayerChat() {
        return playerChat;
    }
}
