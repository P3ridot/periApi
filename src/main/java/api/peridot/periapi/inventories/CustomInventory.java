package api.peridot.periapi.inventories;

import api.peridot.periapi.inventories.providers.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomInventory {

    private final Plugin plugin;
    private final PeriInventoryManager manager;

    private String title;
    private int rows;
    private boolean closeable;
    private int updateDelay;

    private InventoryProvider provider;
    private InventoryContent content;

    private final Map<UUID, PersonalInventoryData> personalInventoriesDataMap = new HashMap<>();

    private CustomInventory(Plugin plugin, PeriInventoryManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        this.content = new InventoryContent();
        manager.addInventory(this);
    }

    private Plugin getPlugin() {
        return plugin;
    }

    private PeriInventoryManager getManager() {
        return manager;
    }

    public String getTitle() {
        return title;
    }

    public int getRows() {
        return rows;
    }

    public boolean isCloseable() {
        return closeable;
    }

    public int getUpdateDelay() {
        return updateDelay;
    }

    public InventoryProvider getProvider() {
        return provider;
    }

    public InventoryContent getContent() {
        return content;
    }

    public PersonalInventoryData getPersonalInventoryData(Player player) {
        PersonalInventoryData inventoryData = personalInventoriesDataMap.get(player.getUniqueId());

        if (inventoryData == null) {
            inventoryData = new PersonalInventoryData();
            personalInventoriesDataMap.put(player.getUniqueId(), inventoryData);
        }

        return inventoryData;
    }

    public Inventory getInventory(Player player) {
        PersonalInventoryData inventoryData = getPersonalInventoryData(player);
        Inventory inventory = inventoryData.getInventory();

        if (inventory == null) {
            inventory = Bukkit.createInventory(player, rows * 9, title);
            inventoryData.setInventory(inventory);
        }

        return inventory;
    }

    public BukkitTask getUpdateTask(Player player) {
        PersonalInventoryData inventoryData = getPersonalInventoryData(player);
        BukkitTask updateTask = inventoryData.getUpdateTask();
        Inventory inventory = getInventory(player);

        if (updateTask == null) {
            updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable() {
                @Override
                public void run() {
                    provider.update(player, content);
                    content.fillInventory(inventory);
                }
            }, 0, this.updateDelay);
            inventoryData.setUpdateTask(updateTask);
        }

        return updateTask;
    }

    public void open(Player player) {
        Inventory inventory = getInventory(player);

        provider.init(player, this.content);
        this.content.fillInventory(inventory);

        getUpdateTask(player);

        player.openInventory(inventory);
    }

    public void update(Player player) {
        Inventory inventory = getInventory(player);

        provider.init(player, this.content);
        this.content.fillInventory(inventory);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String title = "";
        private int rows = 6;
        private boolean closeable = true;
        private int updateDelay = 1;

        private Plugin plugin;
        private PeriInventoryManager manager;
        private InventoryProvider provider;
        private InventoryContent content;

        private Builder() {
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder rows(int rows) {
            this.rows = rows;
            return this;
        }

        public Builder closeable(boolean closeable) {
            this.closeable = closeable;
            return this;
        }

        public Builder updateDelay(int delay) {
            this.updateDelay = delay;
            return this;
        }

        public Builder plugin(Plugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder manager(PeriInventoryManager manager) {
            this.manager = manager;
            return this;
        }

        public Builder provider(InventoryProvider provider) {
            this.provider = provider;
            return this;
        }

        public Builder content(InventoryContent content) {
            this.content = content;
            return this;
        }

        public CustomInventory build() {
            if (this.plugin == null) {
                throw new IllegalStateException("The plugin instance is required");
            }

            if (this.manager == null) {
                throw new IllegalStateException("The manager of the CustomInventory.Builder is required");
            }

            if (this.provider == null) {
                throw new IllegalStateException("The provider of the CustomInventory.Builder is required");
            }

            CustomInventory inventory = new CustomInventory(plugin, manager);

            inventory.title = this.title;
            inventory.rows = this.rows;
            inventory.closeable = this.closeable;
            inventory.updateDelay = this.updateDelay;
            inventory.provider = this.provider;

            if (content != null) inventory.content = this.content;

            return inventory;
        }
    }

}
