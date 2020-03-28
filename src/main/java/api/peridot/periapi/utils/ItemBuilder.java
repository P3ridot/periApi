package api.peridot.periapi.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private ItemStack itemStack;

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this(new ItemStack(material, amount));
    }

    public ItemBuilder(Material material, int amount, byte durability) {
        this(new ItemStack(material, amount, durability));
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder clone() {
        return new ItemBuilder(itemStack);
    }

    public ItemBuilder setDurability(short durability) {
        itemStack.setDurability(durability);

        return this;
    }

    public ItemBuilder setUnbreakable() {
        itemStack.setDurability(Short.MAX_VALUE);

        return this;
    }

    public ItemBuilder setName(String name) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder setLore(String... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ItemBuilder addLoreLine(String line) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = itemMeta.hasLore() ? new ArrayList<>(itemMeta.getLore()) : new ArrayList<>();
        lore.add(line);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder addLoreLine(String line, int lineIndex) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = itemMeta.hasLore() ? new ArrayList<>(itemMeta.getLore()) : new ArrayList<>();
        lore.set(lineIndex, line);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder removeLoreLine(String line) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = itemMeta.hasLore() ? new ArrayList<>(itemMeta.getLore()) : new ArrayList<>();
        if(!lore.contains(line)) return this;
        lore.remove(line);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder removeLoreLine(int lineIndex) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> lore = itemMeta.hasLore() ? new ArrayList<>(itemMeta.getLore()) : new ArrayList<>();
        if(lineIndex < 0 || lineIndex > lore.size()) return this;
        lore.remove(lineIndex);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        Validate.isTrue(level >= 1, "Enchantment level must be equal or greater 1");

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.addEnchant(enchantment, level, false);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder addUnsafeEnchantment(Enchantment enchantment, int level) {
        Validate.isTrue(level >= 1, "Enchantment level must be equal or greater 1");

        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.addEnchant(enchantment, level, true);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.removeEnchant(enchantment);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder addBookEnchantment(Enchantment enchantment, int level) {
        Validate.isTrue(level >= 1, "Enchantment level must be equal or greater 1");
        try {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            enchantmentStorageMeta.addStoredEnchant(enchantment, level, false);
            itemStack.setItemMeta(enchantmentStorageMeta);
        } catch (Exception ignored) { }
        return this;
    }

    public ItemBuilder addUnsafeBookEnchantment(Enchantment enchantment, int level) {
        Validate.isTrue(level >= 1, "Enchantment level must be equal or greater 1");
        try {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            enchantmentStorageMeta.addStoredEnchant(enchantment, level, true);
            itemStack.setItemMeta(enchantmentStorageMeta);
        } catch (Exception ignored) { }
        return this;
    }

    public ItemBuilder removeBookEnchantment(Enchantment enchantment) {
        try {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            enchantmentStorageMeta.removeStoredEnchant(enchantment);
            itemStack.setItemMeta(enchantmentStorageMeta);
        } catch (Exception ignored) { }
        return this;
    }

    public ItemBuilder setSkullOwner(String skullOwner) {
        try {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setOwner(skullOwner);
            itemStack.setItemMeta(skullMeta);
        } catch (Exception ignored) { }
        return this;
    }

    public ItemBuilder setCustomSkullOwner(String url) {
        try {
            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();

            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", url));
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);

            itemStack.setItemMeta(skullMeta);
        } catch (Exception ignored) { }
        return this;
    }

    @Deprecated
    public ItemBuilder setDyeColor(DyeColor color) {
        itemStack.setDurability(color.getDyeData());
        return this;
    }

    @Deprecated
    public ItemBuilder setWoolColor(DyeColor color) {
        if(itemStack.getType().name().contains("WOOL")) return this;
        itemStack.setDurability(color.getWoolData());
        return this;
    }

    public ItemBuilder setLeatherArmorColor(Color color) {
        try {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            leatherArmorMeta.setColor(color);
            itemStack.setItemMeta(leatherArmorMeta);
        } catch (Exception ignored) { }
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }

}
