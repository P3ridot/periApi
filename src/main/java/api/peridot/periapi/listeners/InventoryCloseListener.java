package api.peridot.periapi.listeners;

import api.peridot.periapi.PeriApi;
import api.peridot.periapi.inventories.CustomInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;

public class InventoryCloseListener implements Listener {

    private final Plugin plugin;
    private final PeriApi periApi;

    public InventoryCloseListener(Plugin plugin, PeriApi periApi) {
        this.plugin = plugin;
        this.periApi = periApi;
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();

        if (periApi.getInventoryManager().getInventories().isEmpty()) return;

        for (CustomInventory customInventory : periApi.getInventoryManager().getInventories()) {
            if (!customInventory.getInventory(player).equals(event.getInventory())) continue;
            if (customInventory.isCloseable()) {
                customInventory.getPersonalInventoryData(player).setUpdateTask(null);
                return;
            }

            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> customInventory.open(player), 2);

            return;
        }
    }

}
