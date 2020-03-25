package api.peridot.periapi;

import api.peridot.periapi.inventories.PeriInventoryManager;
import api.peridot.periapi.listeners.InventoryClickListener;
import api.peridot.periapi.listeners.InventoryCloseListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PeriApi {

    private final Plugin plugin;

    private PeriInventoryManager inventoryManager;

    public PeriApi(Plugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new InventoryClickListener(plugin, this), plugin);
        pluginManager.registerEvents(new InventoryCloseListener(plugin, this), plugin);

        inventoryManager = new PeriInventoryManager(plugin, this);
    }

    public PeriInventoryManager getInventoryManager() {
        return inventoryManager;
    }
}
