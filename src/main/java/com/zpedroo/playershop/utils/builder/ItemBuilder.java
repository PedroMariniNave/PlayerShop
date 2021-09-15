package com.zpedroo.playershop.utils.builder;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private static ItemStack item;

    public ItemBuilder(Material material, int amount) {
        if (StringUtils.equals(material.toString(), "PLAYER_HEAD")) {
            item = new ItemStack(material, amount, (short) 3);
        } else {
            item = new ItemStack(material, amount);
        }
    }

    public ItemBuilder(Material material, int amount, short durability) {
        if (StringUtils.equals(material.toString(), "PLAYER_HEAD")) {
            item = new ItemStack(material, amount, (short) 3);
        } else {
            item = new ItemStack(material, amount, durability);
        }
    }

    public static ItemStack build(FileConfiguration file, String where) {
        return build(file, where, null, null);
    }

    public static ItemStack build(FileConfiguration file, String where, String[] placeholders, String[] replacers) {
        ItemBuilder builder = null;
        String type = StringUtils.replace(file.getString(where + ".type"), " ", "").toUpperCase();
        int amount = file.contains(where + ".amount") ? file.getInt(where + ".amount") : 1;

        if (StringUtils.contains(type, ":")) {
            String[] typeSplit = type.split(":");
            short durability = Short.parseShort(typeSplit[1]);

            builder = new ItemBuilder(getMaterial(typeSplit[0]), amount, durability);
        } else {
            builder = new ItemBuilder(getMaterial(type), amount);
        }

        if (file.contains(where + ".name")) {
            String name = ChatColor.translateAlternateColorCodes('&', file.getString(where + ".name"));
            builder.setName(replace(name, placeholders, replacers));
        }

        if (file.contains(where + ".lore")) {
            builder.setLore(file.getStringList(where + ".lore"), placeholders, replacers);
        }

        if (file.contains(where + ".owner")) {
            String owner = file.getString(where + ".owner");

            if (owner.length() <= 16) { // max player name lenght
                builder.setSkullOwner(StringUtils.replaceEach(owner, placeholders, replacers));
            } else {
                builder.setCustomTexture(owner);
            }
        }

        if (file.contains(where + ".glow") && file.getBoolean(where + ".glow")) {
            builder.setGlow();
        }

        if (file.contains(where + ".enchants")) {
            for (String str : file.getStringList(where + ".enchants")) {
                String enchantment = StringUtils.replace(str, " ", "");

                try {
                    if (StringUtils.contains(enchantment, ",")) {
                        String[] enchantmentSplit = enchantment.split(",");
                        builder.addEnchantment(Enchantment.getByName(enchantmentSplit[0]), Integer.parseInt(enchantmentSplit[1]));
                    } else {
                        builder.addEnchantment(Enchantment.getByName(enchantment));
                    }
                } catch (Exception ex) {
                    // invalid enchantment
                }
            }
        }

        return builder.build();
    }

    private void setName(String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);
    }

    private void setLore(List<String> lore, String[] placeholders, String[] replacers) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || lore == null || lore.size() <= 0) return;

        List<String> toAdd = new ArrayList<>(lore.size());

        for (String str : lore) {
            toAdd.add(ChatColor.translateAlternateColorCodes('&', replace(str, placeholders, replacers)));
        }

        meta.setLore(toAdd);
        item.setItemMeta(meta);
    }

    private void addEnchantment(Enchantment enchantment) {
        addEnchantment(enchantment, 1);
    }

    private void addEnchantment(Enchantment enchantment, int level) {
        if (enchantment == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.addEnchant(enchantment, level, true);
        item.setItemMeta(meta);

    }

    private void setGlow() {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
    }

    private void setSkullOwner(String owner) {
        if (!StringUtils.contains(item.getType().toString(), "PLAYER_HEAD")) return;
        if (owner == null || owner.isEmpty()) return;

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null) return;

        meta.setOwner(owner);
        item.setItemMeta(meta);
    }

    private void setCustomTexture(String url) {
        if (!StringUtils.contains(item.getType().toString(), "PLAYER_HEAD")) return;
        if (url == null || url.isEmpty()) return;

        SkullMeta meta = (SkullMeta) item.getItemMeta();
        if (meta == null) return;

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", url));

        try {
            Field field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(meta, profile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        item.setItemMeta(meta);
    }

    private ItemStack build() {
        return item.clone();
    }

    private static Material getMaterial(String type) {
        if (type == null || type.isEmpty()) return null;

        Material material = null;
        try {
            material = Material.getMaterial(type.toUpperCase());
        } catch (Exception ex) {
            // invalid material
        }

        return material;
    }

    private static String replace(String text, String[] placeholders, String[] replacers) {
        if (text == null || text.isEmpty() || placeholders == null) return text;
        if (placeholders.length == 0 || placeholders.length != replacers.length) return text;

        text = StringUtils.replaceEach(text, placeholders, replacers);

        return text;
    }
}
