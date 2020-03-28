package api.peridot.periapi.items;

import api.peridot.periapi.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ItemsParser {

    private final ConfigurationSection section;
    private final Logger logger;

    private final Map<String, ItemBuilder> items = new HashMap<>();

    public ItemsParser(ConfigurationSection section) {
        this.section = section;
        this.logger = Bukkit.getLogger();
        reload();
    }

    public ItemBuilder getItemBuilder(String id) {
        ItemBuilder item = items.get(id);

        if(item == null) {
            ConfigurationSection itemSection = section.getConfigurationSection(id);

            Material material = Material.matchMaterial(itemSection.getString("material"));
            boolean unbreakable = itemSection.getBoolean("unbreakable");
            int amount = Math.max(itemSection.getInt("amount"), 1);

            String name = ColorUtil.color(itemSection.getString("name"));
            List<String> lore = ColorUtil.color(itemSection.getStringList("lore"));
            Map<Enchantment, Integer> enchantments = parseEnchantments(itemSection.getStringList("enchantments"));
            Map<Enchantment, Integer> bookEnchantments = parseEnchantments(itemSection.getStringList("book-enchantments"));
            String skullOwner = itemSection.getString("skull-owner");
            String skullTexture = itemSection.getString("skull-texture");
            Color color = Color.fromRGB(itemSection.getInt("color.red"), itemSection.getInt("color.green"), itemSection.getInt("color.blue"));

            item = new ItemBuilder(material, amount);

            if (unbreakable) item.setUnbreakable();
            if (!name.isEmpty()) item.setName(name);
            if (!lore.isEmpty() && !(lore.size() == 1 && lore.get(0).isEmpty())) item.setLore(lore);
            if (!enchantments.isEmpty()) item.addUnsafeEnchantments(enchantments);
            if (!bookEnchantments.isEmpty()) item.addUnsafeBookEnchantments(bookEnchantments);
            if (skullOwner != null && !skullOwner.isEmpty()) item.setSkullOwner(skullOwner);
            if (skullTexture != null && !skullTexture.isEmpty()) item.setCustomSkullOwner(skullTexture);
            if (color == null) item.setLeatherArmorColor(color);

            items.put(id, item);
        }

        return item;
    }

    public ItemStack getItem(String id) {
        return getItemBuilder(id).build();
    }

    private Map<Enchantment, Integer> parseEnchantments(List<String> enchantments) {
        Map<Enchantment, Integer> enchantmentsMap = new HashMap<>();
        for(String enchantmentString : enchantments) {
            try {
                String[] splitedEnchantment = enchantmentString.split(":", 2);

                Enchantment enchantment = Enchantment.getByName(splitedEnchantment[0]);
                int level = Integer.parseInt(splitedEnchantment[1]);

                enchantmentsMap.put(enchantment, level);
            } catch (Exception ex) { }
        }

        return enchantmentsMap;
    }

    public void reload() {
        if (section == null) {
            logger.warning("[LangAPI] Missing messages section!");
            return;
        }

        if (!items.isEmpty()) {
            items.keySet().forEach(id -> {
                items.put(id, getItemBuilder(id));
            });
        }
    }
}
