package api.peridot.periapi.items;

import api.peridot.periapi.utils.ColorUtil;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemsParser {

    private ItemsParser() { }

    public ItemBuilder getItemBuilder(ConfigurationSection section) {
        if (section == null) return null;

        Material material = Material.matchMaterial(section.getString("material"));
        short durability = (short) section.getInt("durability");
        int amount = Math.max(section.getInt("amount"), 1);

        String name = ColorUtil.color(section.getString("name"));
        List<String> lore = ColorUtil.color(section.getStringList("lore"));
        Map<Enchantment, Integer> enchantments = parseEnchantments(section.getStringList("enchantments"));
        Map<Enchantment, Integer> bookEnchantments = parseEnchantments(section.getStringList("book-enchantments"));
        String skullOwner = section.getString("skull-owner");
        String skullTexture = section.getString("skull-texture");
        Color color = Color.fromRGB(section.getInt("color.red"), section.getInt("color.green"), section.getInt("color.blue"));

        ItemBuilder item = new ItemBuilder(material, amount);

        if (durability != 0) item.setDurability(durability);
        if (!name.isEmpty()) item.setName(name);
        if (!lore.isEmpty() && !(lore.size() == 1 && lore.get(0).isEmpty())) item.setLore(lore);
        if (!enchantments.isEmpty()) item.addUnsafeEnchantments(enchantments);
        if (!bookEnchantments.isEmpty()) item.addUnsafeBookEnchantments(bookEnchantments);
        if (skullOwner != null && !skullOwner.isEmpty()) item.setSkullOwner(skullOwner);
        if (skullTexture != null && !skullTexture.isEmpty()) item.setCustomSkullOwner(skullTexture);
        if (section.getConfigurationSection("color") != null) item.setLeatherArmorColor(color);

        return item;
    }

    public ItemStack getItem(ConfigurationSection section) {
        return getItemBuilder(section).build();
    }

    private Map<Enchantment, Integer> parseEnchantments(List<String> enchantments) {
        Map<Enchantment, Integer> enchantmentsMap = new HashMap<>();
        for (String enchantmentString : enchantments) {
            try {
                String[] splitedEnchantment = enchantmentString.split(":", 2);

                Enchantment enchantment = Enchantment.getByName(splitedEnchantment[0]);
                int level = Integer.parseInt(splitedEnchantment[1]);

                enchantmentsMap.put(enchantment, level);
            } catch (Exception ex) {
            }
        }

        return enchantmentsMap;
    }
}
