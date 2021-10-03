package com.zpedroo.playershop.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;

public class WorldGuardHook {

    private static WorldGuardHook instance;
    public static WorldGuardHook getInstance() { return instance; }

    public WorldGuardHook() {
        instance = this;
    }

    public boolean canBuild(Player player, org.bukkit.Location where) {
        if (player.hasPermission("playershop.admin")) return true;

        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        Location location = BukkitAdapter.adapt(where);

        query.testState(location, WorldGuardPlugin.inst().wrapPlayer(player), Flags.BUILD);
        return false;
    }
}