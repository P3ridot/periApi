package api.peridot.periapi.inventories;

import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

public class PersonalInventoryData {

    private Inventory inventory;
    private BukkitTask updateTask;
    private int openedPage;

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public BukkitTask getUpdateTask() {
        return updateTask;
    }

    public void setUpdateTask(BukkitTask updateTask) {
        cancelUpdateTask();
        this.updateTask = updateTask;
    }

    public void cancelUpdateTask() {
        if (updateTask == null) return;
        updateTask.cancel();
    }

    public int getOpenedPage() {
        return openedPage;
    }

    public void setOpenedPage(int openedPage) {
        this.openedPage = openedPage;
    }
}
