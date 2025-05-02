package model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MenuCatalog {
    private final Map<Integer, Menu> menuMap;

    public MenuCatalog(List<Menu> menus){
        this.menuMap = new LinkedHashMap<>();
        for(Menu m : menus){
            menuMap.put(m.getItemId(), m);
        }
    }

    public Menu get(int itemId) {
        return menuMap.get(itemId);
    }

    public boolean has(int itemId) {
        return menuMap.containsKey(itemId);
    }

    public List<Menu> getAll() {
        return List.copyOf(menuMap.values());
    }

    public List<Menu> findByCategory(String category) {
        if (category == null || category.isBlank()) {
            return List.copyOf(menuMap.values());
        }
        return menuMap.values().stream().filter(m -> m.getCategory().equals(category)).toList();
    }

    public int size() {
        return menuMap.size();
    }

    public boolean isEmpty() {
        return menuMap.isEmpty();
    }

}
