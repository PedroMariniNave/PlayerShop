package com.zpedroo.playershop.hooks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.zpedroo.playershop.managers.ShopManager;
import com.zpedroo.playershop.objects.Shop;
import com.zpedroo.playershop.objects.ShopHologram;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ProtocolLibHook extends PacketAdapter {

    private Map<Player, List<ShopHologram>> holograms = new HashMap<>(64);

    public ProtocolLibHook(Plugin plugin, PacketType packetType) {
        super(plugin, packetType);
    }

    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        Block block = player.getTargetBlock(null, 15);

        Location location = block.getLocation();

        Shop shop = ShopManager.getInstance().getShop(location);

        if (shop == null) {
            if (!holograms.containsKey(player)) return;

            List<ShopHologram> holoList = holograms.remove(player);

            for (ShopHologram hologram : holoList) {
                hologram.hideTo(player);
            }
            return;
        }

        ShopHologram hologram = shop.getHologram();

        hologram.showTo(player);

        List<ShopHologram> holoList = holograms.containsKey(player) ? holograms.get(player) : new ArrayList<>();
        holoList.add(hologram);

        holograms.put(player, holoList);
    }
}
