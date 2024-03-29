package com.zpedroo.playershop.commands;

import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.zpedroo.playershop.managers.ShopManager;
import com.zpedroo.playershop.objects.Shop;
import com.zpedroo.playershop.objects.ShopCreator;
import com.zpedroo.playershop.utils.config.Messages;
import com.zpedroo.playershop.utils.menu.Menus;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.HashSet;

public class CreateShopCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] strings) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        Block block = player.getTargetBlock((HashSet<Byte>) null, 5);

        if (block.getType().equals(Material.AIR)) {
            player.sendMessage(Messages.NEED_LOOK);
            return true;
        }

        Shop shop = ShopManager.getInstance().getShop(block.getLocation());

        if (shop != null) {
            player.sendMessage(Messages.HAS_SHOP);
            return true;
        }

        ItemStack item = player.getInventory().getItemInHand().clone();

        if (item.getType().equals(Material.AIR)) {
            player.sendMessage(Messages.NEED_ITEM);
            return true;
        }

        if (!player.hasPermission("playershop.admin")) {
            Location location = new Location(
                    block.getWorld().getName(),
                    block.getX(),
                    block.getY(),
                    block.getZ()
            );
            Plot plot = Plot.getPlot(location);
            if (plot == null || !plot.isAdded(player.getUniqueId())) {
                player.sendMessage(Messages.WITHOUT_PERMISSION);
                return true;
            }
        }

        ShopCreator creator = new ShopCreator(block.getLocation(), player.getUniqueId(), item, null, BigInteger.ZERO, BigInteger.ZERO, item.getAmount(), null);
        Menus.getInstance().openCreatorMenu(player, creator);
        return false;
    }
}