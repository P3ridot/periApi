package api.peridot.periapi.configuration;

import api.peridot.periapi.items.ItemBuilder;
import api.peridot.periapi.items.ItemParser;
import api.peridot.periapi.utils.ColorUtil;
import api.peridot.periapi.utils.NumberUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ConfigurationProvider {

    private final ConfigurationSection section;
    private final Logger logger;

    private final Map<String, Object> valuesMap = new ConcurrentHashMap<>();

    public ConfigurationProvider(ConfigurationSection section) {
        this.section = section;
        this.logger = Bukkit.getLogger();
        reload();
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

    public String getString(String path) {
        Object value = getObject(path);
        return value instanceof String ? value.toString() : null;
    }

    public String getColoredString(String path) {
        String value = ColorUtil.color(getString(path));
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

    public List<String> getStringList(String path) {
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
        return result;
    }

    public List<String> getColoredStringList(String path) {
        List<String> list = ColorUtil.color(getStringList(path));
        if (list != null) {
            valuesMap.put(path, list);
        }
        return list;
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
                    valuesMap.put(path, getObject(path));
                } catch (Exception ignored) {
                }
            });
        }
    }
}
