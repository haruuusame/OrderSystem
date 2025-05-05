package test.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import model.Menu;
import model.MenuCatalog;

public class MenuCatalogTest {
    private Menu dummyMenu(int id,String category) {
        return new Menu(id, "商品" + id, 300, 100, category);
    }

    @Test
    public void fieldsSetAndGetCorrectly() {
        List<Menu> menus = new ArrayList<>();
        menus.add(dummyMenu(1,"A"));
        menus.add(dummyMenu(2,"B"));
        menus.add(dummyMenu(3,"C"));

        MenuCatalog catalog = new MenuCatalog(menus);
        
        // get()
        Menu result_A = catalog.get(1);
        assertNotNull(result_A);
        assertEquals(1,result_A.getItemId());
        assertEquals("商品1", result_A.getItemName());
        assertEquals(300, result_A.getPrice());
        assertEquals(100, result_A.getStockQuantity());
        assertEquals("A",result_A.getCategory());

        // getAll()
        List<Menu> result_B = catalog.getAll();
        assertNotNull(result_B);
        for(int i = 0; i < 3; i++){
            assertEquals(menus.get(i).getItemId(),result_B.get(i).getItemId());
            assertEquals(menus.get(i).getItemName(),result_B.get(i).getItemName());
            assertEquals(menus.get(i).getPrice(),result_B.get(i).getPrice());
            assertEquals(menus.get(i).getStockQuantity(),result_B.get(i).getStockQuantity());
            assertEquals(menus.get(i).getCategory(),result_B.get(i).getCategory());
        }
    }

    @Test
    public void hassizeisEmptyWorks() {
        List<Menu> menus = new ArrayList<>();
        menus.add(dummyMenu(1,"A"));
        menus.add(dummyMenu(2,"B")); 
        menus.add(dummyMenu(3,"C"));

        MenuCatalog catalog_A = new MenuCatalog(menus);
        MenuCatalog catalog_B = new MenuCatalog(new ArrayList<>());

        // has()
        assertTrue(catalog_A.has(1));
        assertTrue(catalog_A.has(2));
        assertTrue(catalog_A.has(3));
        assertFalse(catalog_A.has(4));
        assertFalse(catalog_B.has(1));

        // size()
        assertEquals(3,catalog_A.size());
        assertEquals(0,catalog_B.size());

        // isEmpty()
        assertFalse(catalog_A.isEmpty());
        assertTrue(catalog_B.isEmpty());
        
    }
    @Test
    public void getReturnsNullForInvalidId() {
        List<Menu> menus = new ArrayList<>();
        menus.add(dummyMenu(1, "A"));
    
        MenuCatalog catalog = new MenuCatalog(menus);
        assertNull(catalog.get(999));  // 存在しないID
    }
    

    @Test
    public void findByCategoryWorks() {
        List<Menu> menus = new ArrayList<>();
        menus.add(dummyMenu(1,"A"));
        menus.add(dummyMenu(2,"B")); 
        menus.add(dummyMenu(3,"C"));
        menus.add(dummyMenu(4,"C"));
        menus.add(dummyMenu(5,"A"));

        MenuCatalog catalog = new MenuCatalog(menus);

        List<Menu> result_A = catalog.findByCategory("A");
        assertNotNull(result_A);
        assertEquals(2,result_A.size());
        
        assertEquals(menus.get(0).getItemId(),result_A.get(0).getItemId());
        assertEquals(menus.get(0).getItemName(),result_A.get(0).getItemName());
        assertEquals(menus.get(0).getPrice(),result_A.get(0).getPrice());
        assertEquals(menus.get(0).getStockQuantity(),result_A.get(0).getStockQuantity());
        assertEquals("A",result_A.get(0).getCategory());

        assertEquals(menus.get(4).getItemId(),result_A.get(1).getItemId());
        assertEquals(menus.get(4).getItemName(),result_A.get(1).getItemName());
        assertEquals(menus.get(4).getPrice(),result_A.get(1).getPrice());
        assertEquals(menus.get(4).getStockQuantity(),result_A.get(1).getStockQuantity());
        assertEquals("A",result_A.get(1).getCategory());

        List<Menu> result_B = catalog.findByCategory("B");
        assertNotNull(result_B);
        assertEquals(1,result_B.size());
        assertEquals(menus.get(1).getItemId(),result_B.get(0).getItemId());
        assertEquals(menus.get(1).getItemName(),result_B.get(0).getItemName());
        assertEquals(menus.get(1).getPrice(),result_B.get(0).getPrice());
        assertEquals(menus.get(1).getStockQuantity(),result_B.get(0).getStockQuantity());
        assertEquals("B",result_B.get(0).getCategory());

        List<Menu> result_C = catalog.findByCategory("C");
        assertNotNull(result_C);
        assertEquals(2,result_C.size());
        assertEquals(menus.get(2).getItemId(),result_C.get(0).getItemId());
        assertEquals(menus.get(2).getItemName(),result_C.get(0).getItemName());
        assertEquals(menus.get(2).getPrice(),result_C.get(0).getPrice());
        assertEquals(menus.get(2).getStockQuantity(),result_C.get(0).getStockQuantity());
        assertEquals("C",result_C.get(0).getCategory());
    
        assertEquals(menus.get(3).getItemId(),result_C.get(1).getItemId());
        assertEquals(menus.get(3).getItemName(),result_C.get(1).getItemName());
        assertEquals(menus.get(3).getPrice(),result_C.get(1).getPrice());
        assertEquals(menus.get(3).getStockQuantity(),result_C.get(1).getStockQuantity());
        assertEquals("C",result_C.get(1).getCategory());

        List<Menu> result_D = catalog.findByCategory("D");
        assertNotNull(result_D);
        assertEquals(0,result_D.size());
    }
    @Test
    public void findByCategoryNullOrBlankReturnsAll() {
        List<Menu> menus = new ArrayList<>();
        menus.add(dummyMenu(1, "A"));
        menus.add(dummyMenu(2, "B"));

        MenuCatalog catalog = new MenuCatalog(menus);

        assertEquals(menus.size(), catalog.findByCategory(null).size());
        assertEquals(menus.size(), catalog.findByCategory("").size());
        assertEquals(menus.size(), catalog.findByCategory(" ").size());
    }
    
    @Test
    public void duplicateItemIdUsesLastOne() {
        List<Menu> menus = new ArrayList<>();
        menus.add(new Menu(1, "A", 300, 10, "A"));
        menus.add(new Menu(1, "B", 500, 20, "B")); // 同じIDだが異なる内容
    
        MenuCatalog catalog = new MenuCatalog(menus);
        Menu m = catalog.get(1);
        assertEquals("B", m.getItemName());
        assertEquals(500, m.getPrice());
        assertEquals("B", m.getCategory());
    }
    

}
