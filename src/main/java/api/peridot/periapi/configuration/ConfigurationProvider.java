package api.peridot.periapi.configuration;

import api.peridot.periapi.items.ItemBuilder;
import api.peridot.periapi.items.ItemParser;
import api.peridot.periapi.utils.replacements.Replacement;
import api.peridot.periapi.utils.replacements.ReplacementUtil;
import api.peridot.periapi.utils.simple.ColorUtil;
import api.peridot.periapi.utils.simple.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ConfigurationProvider {

    private final Map<String, Object> valuesMap = new ConcurrentHashMap<>();

    private final Logger logger;

    private ConfigurationSection section;

    public ConfigurationProvider(Plugin plugin, ConfigurationSection section) {
        this.logger = plugin.getLogger();
        this.section = section;
    }

    @Deprecated
    public ConfigurationProvider(ConfigurationSection section) {
        this.logger = Bukkit.getLogger();
        this.section = section;
    }

    public ConfigurationProvider(Plugin plugin) {
        this.logger = plugin.getLogger();
    }

    public void setSection(ConfigurationSection section) {
        this.section = section;
    }

    public Object getObject(String path) {
        Object value = valuesMap.get(path);
        if (value == null) {
            value = section.get(path);
            if (value != null) {
                valuesMap.put(path, value);
            }
        }
        return value;
    }

    public String getString(String path, Replacement... replacements) {
        Object value = getObject(path);
        String string = value instanceof String ? value.toString() : null;
        if (string != null) {
            string = ReplacementUtil.replace(string, replacements);
        }
        return string;
    }

    public String getColoredString(String path, Replacement... replacements) {
        String value = ColorUtil.color(getString(path, replacements));
        if (value != null) {
            valuesMap.put(path, value);
        }
        return value;
    }

    public boolean getBoolean(String path) {
        Object value = getObject(path);
        return value instanceof Boolean ? (Boolean) value : null;
    }

    public byte getByte(String path) {
        Object value = getObject(path);
        return value instanceof Byte ? NumberUtil.toByte(value) : null;
    }

    public short getShort(String path) {
        Object value = getObject(path);
        return value instanceof Short ? NumberUtil.toShort(value) : null;
    }

    public int getInt(String path) {
        Object value = getObject(path);
        return value instanceof Integer ? NumberUtil.toInt(value) : null;
    }

    public long getLong(String path) {
        Object value = getObject(path);
        return value instanceof Long ? NumberUtil.toLong(value) : null;
    }

    public float getFloat(String path) {
        Object value = getObject(path);
        return value instanceof Float ? NumberUtil.toFloat(value) : null;
    }

    public double getDouble(String path) {
        Object value = getObject(path);
        return value instanceof Double ? NumberUtil.toDouble(value) : null;
    }

    public List<?> getList(String path) {
        Object value = getObject(path);
        return (List<?>) ((value instanceof List) ? value : null);
    }

    public List<String> getStringList(String path, Replacement... replacements) {
        List<?> list = getList(path);
        if (list == null) {
            return new ArrayList<String>();
        }
        List<String> result = new ArrayList<String>();
        for (Object object : list) {
            if (object instanceof String || isPrimitiveWrapper(object)) {
                result.add(String.valueOf(object));
            }
        }
        return ReplacementUtil.replace(result, replacements);
    }

    public List<String> getColoredStringList(String path, Replacement... replacements) {
        List<String> list = ColorUtil.color(getStringList(path, replacements));
        if (list != null) {
            valuesMap.put(path, list);
        }
        return list;
    }

    public Color getColor(String path) {
        Object value = valuesMap.get(path);
        if (value == null) {
            ConfigurationSection colorSection = section.getConfigurationSection(path);
            int red = colorSection.getInt("red");
            int green = colorSection.getInt("green");
            int blue = colorSection.getInt("blue");
            value = Color.fromRGB(red, green, blue);
            valuesMap.put(path, value);
        }
        return value instanceof Color ? (Color) value : null;
    }

    public ItemStack getItemStack(String path) {
        Object value = getObject(path);
        return value instanceof ItemBuilder ? (ItemStack) value : null;
    }

    public ItemStack getItemStackFromBuilder(String path) {
        ItemBuilder value = getItemBuilder(path);
        if (value == null) {
            return null;
        }
        return value.build();
    }

    public ItemBuilder getItemBuilder(String path) {
        Object value = valuesMap.get(path);
        if (value == null) {
            try {
                value = ItemParser.parseItemBuilder(section.getConfigurationSection(path));
            } catch (Exception ex) {
                value = null;
            }
            if (value != null) {
                valuesMap.put(path, value);
            }
        }
        return (ItemBuilder) value;
    }

    protected boolean isPrimitiveWrapper(Object input) {
        return input instanceof Integer || input instanceof Boolean ||
                input instanceof Character || input instanceof Byte ||
                input instanceof Short || input instanceof Double ||
                input instanceof Long || input instanceof Float;
    }

    public void reload() {
        if (section == null) {
            logger.warning("[ConfigurationProvider] Missing configuration section!");
            return;
        }

        if (!valuesMap.isEmpty()) {
            valuesMap.keySet().forEach(path -> {
                try {
                    System.out.println(path + " - " + getObject(path));
                    valuesMap.put(path, getObject(path));
                } catch (Exception ignored) {
                }
            });
        }
    }

}
