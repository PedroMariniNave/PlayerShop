package com.zpedroo.playershop.hooks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.zpedroo.playershop.PlayerShop;
import com.zpedroo.playershop.managers.ShopManager;
import com.zpedroo.playershop.objects.Shop;
import com.zpedroo.playershop.objects.ShopHologram;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ProtocolLibHook extends PacketAdapter {

    private final Map<Player, List<ShopHologram>> holograms = new HashMap<>(8);

    public ProtocolLibHook(Plugin plugin, PacketType packetType) {
        super(plugin, packetType);
    }

    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        Block block = player.getTargetBlock((HashSet<Byte>) null, 15);

        Location location = block.getLocation();

        Shop shop = ShopManager.getInstance().getShop(location);

        if (shop == null) {
            if (!holograms.containsKey(player)) return;

            List<ShopHologram> holoList = holograms.remove(player);

            for (ShopHologram hologram : holoList) {
                PlayerShop.get().getServer().getScheduler().runTaskLater(PlayerShop.get(),
                        () -> hologram.hideHologramTo(player), 0L);
            }
            return;
        }

        ShopHologram hologram = shop.getHologram();

        PlayerShop.get().getServer().getScheduler().runTaskLater(PlayerShop.get(),
                () -> hologram.showHologramTo(player), 0L);

        List<ShopHologram> holoList = holograms.containsKey(player) ? holograms.get(player) : new ArrayList<>();
        holoList.add(hologram);

        holograms.put(player, holoList);
    }
}
