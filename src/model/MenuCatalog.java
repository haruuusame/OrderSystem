package model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * 複数のMenuクラスをまとめて管理するクラス。
 */
public class MenuCatalog {

    // ======= Field =======
    private final Map<Integer, Menu> menuMap;

    // ======= Constructor =======
    public MenuCatalog(List<Menu> menus){
        this.menuMap = new LinkedHashMap<>();
        for(Menu m : menus){
            menuMap.put(m.getItemId(), m);
        }
    }

    // ======= Method =======

    // itemIdに対応するMenuを返す
    public Menu get(int itemId) {
        return menuMap.get(itemId);
    }

    // 登録されているMenuをListとして返す
    public List<Menu> getAll() {
        return List.copyOf(menuMap.values());
    }

    // menuMapにitemIdを含んでいればtrueを返す
    public boolean has(int itemId) {
        return menuMap.containsKey(itemId);
    }

    // categoryに合致するMenuをListとして返す
    public List<Menu> findByCategory(String category) {
        if (category == null || category.isBlank()) {
            return List.copyOf(menuMap.values());
        }
        return menuMap.values().stream().filter(m -> m.getCategory().equals(category)).toList();
    }

    // 登録されているメニューの数を返す
    public int size() {
        return menuMap.size();
    }

    // メニューが一つも登録されていなければtrueを返す
    public boolean isEmpty() {
        return menuMap.isEmpty();
    }

}
