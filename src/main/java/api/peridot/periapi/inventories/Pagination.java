package api.peridot.periapi.inventories;

import api.peridot.periapi.inventories.items.InventoryItem;

import java.util.Arrays;

public class Pagination {

    private InventoryItem[] items = new InventoryItem[0];
    private int itemsPerPage = 1;

    public InventoryItem[] getItems() {
        return Arrays.copyOf(items, items.length);
    }

    public void setItems(InventoryItem... items) {
        this.items = items;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public InventoryItem[] getItemsForPage(int page) {
        return Arrays.copyOfRange(items, page * itemsPerPage, (page + 1) * itemsPerPage);
    }

    public int getPageCount() {
        return (int) Math.ceil((float) this.items.length / itemsPerPage);
    }

}
