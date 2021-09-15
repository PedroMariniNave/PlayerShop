package com.zpedroo.playershop.hooks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.zpedroo.playershop.PlayerShop;
import com.zpedroo.playershop.managers.ShopManager;
import com.zpedroo.playershop.shop.Shop;
import com.zpedroo.playershop.shop.ShopHologram;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProtocolLib {

    private static ProtocolLib instance;
    public static ProtocolLib getInstance() { return instance; }

    private ProtocolManager protocolManager;

    private HashMap<Player, List<ShopHologram>> holograms;

    public ProtocolLib() {
        instance = this;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.holograms = new HashMap<>(512);
        this.registerPackets();
    }

    private void registerPackets() {
        getProtocolManager().addPacketListener(new PacketAdapter(PlayerShop.get(), ListenerPriority.LOWEST, PacketType.Play.Client.LOOK) {
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

                show(player, shop);
            }
        });
    }

    private void show(Player player, Shop shop) {
        ShopHologram hologram = shop.getHologram();

        hologram.showTo(player);

        List<ShopHologram> holoList = holograms.containsKey(player) ? holograms.get(player) : new ArrayList<>();
        holoList.add(hologram);

        holograms.put(player, holoList);
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}
