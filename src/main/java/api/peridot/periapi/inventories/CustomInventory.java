package api.peridot.periapi.inventories;

import api.peridot.periapi.inventories.providers.InventoryProvider;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CustomInventory {

    private final Plugin plugin;
    private final PeriInventoryManager manager;

    private String title;
    private int rows;
    private boolean closeable;
    private int updateDelay;

    private InventoryProvider provider;
    private Pagination pagination;
    private boolean paginated;

    private final Map<UUID, PersonalInventoryData> personalInventoriesDataMap = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> openedPageMap = new ConcurrentHashMap<>();

    private CustomInventory(Plugin plugin, PeriInventoryManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        this.pagination = new Pagination(new InventoryContent());
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

    public Pagination getPagination() {
        return pagination;
    }

    public boolean isPaginated() {
        return paginated;
    }

    public PersonalInventoryData getPersonalInventoryData(Player player) {
        PersonalInventoryData inventoryData = personalInventoriesDataMap.get(player.getUniqueId());
        if (inventoryData == null) {
            inventoryData = new PersonalInventoryData();
            personalInventoriesDataMap.put(player.getUniqueId(), inventoryData);
        }
        return inventoryData;
    }

    public int getOpenedPageIndex(Player player) {
        int page = openedPageMap.get(player.getUniqueId());
        if (openedPageMap.get(player.getUniqueId()) == null || page <= 0) {
            page = -1;
            openedPageMap.put(player.getUniqueId(), page);
        }
        return page;
    }

    public void setOpenedPageIndex(Player player, int page) {
        Validate.isTrue(page >= 1, "Page value must be bigger or equal 1");
        Validate.isTrue(page <= pagination.pagesAmount(), "Page value must be smaller or equal amount of pages");
        openedPageMap.put(player.getUniqueId(), page);
    }

    public InventoryContent getOpenedPageContent(Player player) {
        if (getOpenedPageIndex(player) == -1) return null;
        return getPageContent(getOpenedPageIndex(player));
    }

    public InventoryContent getPageContent(int page) {
        return getPagination().getPage(page);
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

    public BukkitTask getUpdateTask(Player player, int page) {
        Validate.isTrue(page > 1 && isPaginated() || page == 1, "Inventory isn't paginated");

        PersonalInventoryData inventoryData = getPersonalInventoryData(player);
        BukkitTask updateTask = inventoryData.getUpdateTask();
        Inventory inventory = getInventory(player);
        InventoryContent content = getPageContent(page);

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

    public void open(Player player, int page) {
        Validate.isTrue(page > 1 && isPaginated() || page == 1, "Inventory isn't paginated");

        Inventory inventory = getInventory(player);
        InventoryContent content = getPageContent(page);

        inventory.clear();

        provider.init(player, content);
        content.fillInventory(inventory);

        getUpdateTask(player, page);
        player.openInventory(inventory);
        setOpenedPageIndex(player, page);
    }

    public void open(Player player) {
        open(player, 1);
    }

    public void update(Player player, int page) {
        Validate.isTrue(page > 1 && isPaginated() || page == 1, "Inventory isn't paginated");

        Inventory inventory = getInventory(player);
        InventoryContent content = getPageContent(page);

        inventory.clear();

        provider.init(player, content);
        content.fillInventory(inventory);
    }

    public void update(Player player) {
        update(player, 1);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String title = "";
        private int rows = 6;
        private boolean closeable = true;
        private int updateDelay = -1;

        private Plugin plugin;
        private PeriInventoryManager manager;
        private InventoryProvider provider;
        private Pagination pagination;
        private boolean paginated = false;

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
            this.pagination = new Pagination(content);
            return this;
        }

        public Builder pagination(Pagination pagination) {
            this.pagination = pagination;
            this.paginated = true;
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
            inventory.paginated = this.paginated;
            if (pagination != null) inventory.pagination = this.pagination;

            return inventory;
        }
    }

}
