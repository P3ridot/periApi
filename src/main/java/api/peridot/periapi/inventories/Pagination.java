package api.peridot.periapi.inventories;

import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pagination {

    private final Map<Integer, InventoryContent> pagesMap = new HashMap<>();

    public Pagination(InventoryContent firstPage) {
        this.pagesMap.put(1, firstPage);
    }

    public Map<Integer, InventoryContent> getPagesMap() {
        return new HashMap<>(pagesMap);
    }

    public List<InventoryContent> getPages() {
        return new ArrayList<>(pagesMap.values());
    }

    public int pagesAmount() {
        return getPages().size();
    }

    public InventoryContent getPage(int page) {
        Validate.isTrue(page >= 1, "Page value must be bigger or equal 1");
        return pagesMap.get(page);
    }

    public void setPage(int page, InventoryContent content) {
        Validate.isTrue(page >= 1, "Page value must be bigger or equal 1");
        if (page > pagesAmount() + 1) {
            pagesMap.put(pagesAmount() + 1, content);
            return;
        }
        pagesMap.put(page, content);
    }

    public void setUnsafePage(int page, InventoryContent content) {
        Validate.isTrue(page >= 1, "Page value must be bigger or equal 1");
        pagesMap.put(page, content);
    }

    public void addPage(InventoryContent content) {
        pagesMap.put(pagesAmount(), content);
    }

}
