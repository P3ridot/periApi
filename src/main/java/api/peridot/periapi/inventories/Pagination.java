package api.peridot.periapi.inventories;

import api.peridot.periapi.inventories.items.InventoryItem;

import java.util.Arrays;

public class Pagination {

    private InventoryItem[] items = new InventoryItem[0];
    private int itemsPerPage = 1;

    public InventoryItem[] getItems() {
        return Arrays.copyOf(items, items.length);
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public InventoryItem[] getItemsForPage(int page) {
        return Arrays.copyOfRange(items, page * itemsPerPage, (page + 1) * itemsPerPage);
    }

    public int getPageCount() {
        return (int) Math.ceil((float) this.items.length / itemsPerPage);
    }

    public void setItems(InventoryItem... items) {
        this.items = items;
    }

}
