package api.peridot.periapi.inventories.items;

import api.peridot.periapi.inventories.InventoryContent;
import api.peridot.periapi.items.ItemParser;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class InventoryItemParser {

    private InventoryItemParser() {
    }

    public static void parseAndSetInventoryItem(InventoryContent content, ConfigurationSection section, Consumer<InventoryClickEvent> consumer) {
        content.setItem(section.getInt("slot"), parseInventoryItem(section, consumer));
    }

    public static void parseAndSetInventoryItem(InventoryContent content, ConfigurationSection section) {
        parseAndSetInventoryItem(content, section, event -> {
        });
    }

    public static InventoryItem parseInventoryItem(ConfigurationSection section, Consumer<InventoryClickEvent> consumer) {
        return parseInventoryItemBuilder(section, consumer).build();
    }

    public static InventoryItem.Builder parseInventoryItemBuilder(ConfigurationSection section, Consumer<InventoryClickEvent> consumer) {
        return parseInventoryItemBuilder(section).consumer(consumer);
    }

    public static InventoryItem parseInventoryItem(ConfigurationSection section) {
        return parseInventoryItemBuilder(section).build();
    }

    public static InventoryItem.Builder parseInventoryItemBuilder(ConfigurationSection section) {
        ItemStack item = ItemParser.parseItemStack(section);
        return InventoryItem.builder().item(item);
    }

}
