package api.peridot.periapi.inventories;

import api.peridot.periapi.inventories.items.InventoryItem;
import api.peridot.periapi.items.ItemBuilder;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InventoryContent {

    private final int rows;
    private final Map<Integer, InventoryItem> inventoryItems = new HashMap<>();

    public InventoryContent(int rows) {
        this.rows = rows;
    }

    public int getRows() {
        return rows;
    }

    public int getInventorySize() {
        return rows * 9;
    }

    public Collection<InventoryItem> getItems() {
        return inventoryItems.values();
    }

    public Map<Integer, InventoryItem> getInventoryItemsMap() {
        return new HashMap<>(inventoryItems);
    }

    public InventoryItem getItem(int row, int column) {
        return inventoryItems.get(slotFromRowAndColumn(row, column));
    }

    public InventoryItem getItem(int slot) {
        return inventoryItems.get(slot);
    }

    public void setItem(int row, int column, InventoryItem inventoryItem) {
        this.setItem(slotFromRowAndColumn(row, column), inventoryItem);
    }

    public void setItem(int slot, InventoryItem inventoryItem) {
        Validate.isTrue(slot >= 0, "Slot must be bigger or equal 0");
        Validate.isTrue(slot <= getInventorySize() - 1, "Slot must be smaller or equal " + (getInventorySize() - 1));
        inventoryItems.put(slot, inventoryItem);
    }

    public void addItem(InventoryItem inventoryItem) {
        int slot = firstEmptySlot();
        if (slot == -1) return;
        this.setItem(slot, inventoryItem);
    }

    public void fill(InventoryItem inventoryItem) {
        for (int i = 0; i < getInventorySize() - 1; i++) {
            setItem(i, inventoryItem);
        }
    }

    public void fillEmpty(InventoryItem inventoryItem) {
        for (int i : emptySlots()) {
            setItem(i, inventoryItem);
        }
    }

    public void fillRow(int row, InventoryItem inventoryItem) {
        Validate.isTrue(row >= 1, "Row value must be bigger or equal 1");
        Validate.isTrue(row <= rows, "Row value must be smaller or equal " + rows);

        for (int i = 0; i < 9; i++) {
            setItem((row - 1) * 9 + i, inventoryItem);
        }
    }

    public void fillRowEmpty(int row, InventoryItem inventoryItem) {
        Validate.isTrue(row >= 1, "Row value must be bigger or equal 1");
        Validate.isTrue(row <= rows, "Row value must be smaller or equal " + rows);

        for (int i = 0; i < 9; i++) {
            int slot = (row - 1) * 9 + i;
            if (!isEmptySlot(slot)) continue;
            setItem(slot, inventoryItem);
        }
    }

    public void fillColumn(int column, InventoryItem inventoryItem) {
        Validate.isTrue(column >= 1, "Column value must be bigger or equal 1");
        Validate.isTrue(column <= 9, "Column value must be smaller or equal 9");

        for (int i = 0; i < rows; i++) {
            setItem((column + (i * 9)) - 1, inventoryItem);
        }
    }

    public void fillColumnEmpty(int column, InventoryItem inventoryItem) {
        Validate.isTrue(column >= 1, "Column value must be bigger or equal 1");
        Validate.isTrue(column <= 9, "Column value must be smaller or equal 9");

        for (int i = 0; i < rows; i++) {
            int slot = (column + (i * 9)) - 1;
            if (!isEmptySlot(slot)) continue;
            setItem(slot, inventoryItem);
        }
    }

    public int slotFromRowAndColumn(int row, int column) {
        Validate.isTrue(column >= 1, "Column value must be bigger or equal 1");
        Validate.isTrue(column <= 9, "Column value must be smaller or equal 9");
        Validate.isTrue(row >= 1, "Row value must be bigger or equal 1");
        Validate.isTrue(row <= rows, "Row value must be smaller or equal " + rows);

        int slot = 0;

        slot += (row - 1) * 9;
        slot += column;

        return slot - 1;
    }

    public boolean isEmpty() {
        return inventoryItems.isEmpty();
    }

    public boolean isEmptySlot(int slot) {
        return inventoryItems.get(slot) == null
                || inventoryItems.get(slot).getItem() == null
                || inventoryItems.get(slot).getItem().getType() == null
                || inventoryItems.get(slot).getItem().getType() == Material.AIR;
    }

    public List<Integer> emptySlots() {
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < getInventorySize() - 1; i++) {
            if (isEmptySlot(i)) list.add(i);
        }

        return list;
    }

    public int firstEmptySlot() {
        int slot = -1;

        for (int i = 0; i < getInventorySize() - 1; i++) {
            if (isEmptySlot(i)) return i;
        }

        return slot;
    }

    public void fillInventory(Inventory inventory) {
        inventory.clear();

        inventoryItems.forEach((slot, item) -> {
            if (slot + 1 <= inventory.getSize()) {
                inventory.setItem(slot, item.getItem());
            }
        });
    }

    public void clear() {
        inventoryItems.clear();
    }
}
