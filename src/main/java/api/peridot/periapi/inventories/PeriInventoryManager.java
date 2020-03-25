package api.peridot.periapi.inventories;

import api.peridot.periapi.PeriApi;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class PeriInventoryManager {

    private final Plugin plugin;
    private final PeriApi periApi;

    public List<CustomInventory> inventoriesList = new ArrayList<>();

    public PeriInventoryManager(Plugin plugin, PeriApi periApi) {
        this.plugin = plugin;
        this.periApi = periApi;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public PeriApi getPeriApi() {
        return periApi;
    }

    @Deprecated
    private static final Map<String, CustomInventory> inventoriesMap = new HashMap<>();

    @Deprecated
    public CustomInventory getMapInventory(String id) {
        return inventoriesMap.get(id);
    }

    @Deprecated
    public Set<String> getMapInventoriesIdList() {
        return inventoriesMap.keySet();
    }

    @Deprecated
    public Collection<CustomInventory> getMapInventoriesList() {
        return inventoriesMap.values();
    }

    @Deprecated
    public void addMapInventory(String id, CustomInventory inventory) {
        inventoriesMap.put(id, inventory);
    }

    public List<CustomInventory> getInventories() {
        return inventoriesList;
    }

    public void addInventory(CustomInventory inventory) {
        this.inventoriesList.add(inventory);
    }

}
