package test.model;

import model.CartItem;
import model.Menu;
import org.junit.Test;
import static org.junit.Assert.*;

public class CartItemTest {

    private Menu dummyMenu() {
        return new Menu(1, "テストメニュー", 500, 20, "Food");
    }

    @Test
    public void fieldsSetCorrectly() {
        CartItem item = new CartItem(dummyMenu(), 3);
        assertEquals(3, item.getQuantity());
        assertEquals("テストメニュー", item.getMenu().getItemName());
    }

    @Test
    public void setQuantityWorks() {
        CartItem item = new CartItem(dummyMenu(), 3);

        assertTrue(item.setQuantity(3));
        assertEquals(3, item.getQuantity());

        assertFalse(item.setQuantity(-1));
        assertEquals(3, item.getQuantity());

        assertTrue(item.setQuantity(5));
        assertEquals(5, item.getQuantity());
    }

    @Test
    public void addQuantityWorks() {
        CartItem item = new CartItem(dummyMenu(), 5);

        assertTrue(item.addQuantity(0));
        assertEquals(5, item.getQuantity());

        assertTrue(item.addQuantity(3));
        assertEquals(8, item.getQuantity());

        assertFalse(item.addQuantity(-10));
        assertEquals(8, item.getQuantity());

        assertTrue(item.addQuantity(-8));
        assertEquals(0, item.getQuantity());
    }

    @Test
    public void copyCreatesShallowCopy() {
        CartItem original = new CartItem(dummyMenu(), 4);
        CartItem copy = original.copy();

        assertNotSame(original, copy);
        assertSame(original.getMenu(), copy.getMenu());
        assertEquals(original.getQuantity(), copy.getQuantity());
    }

    @Test
    public void deepcopyCreatesDeepCopy() {
        CartItem original = new CartItem(dummyMenu(), 4);
        CartItem deep = original.deepcopy();

        assertNotSame(original, deep);
        assertNotSame(original.getMenu(), deep.getMenu());
        assertEquals(original.getMenu().getItemId(), deep.getMenu().getItemId());
        assertEquals(original.getQuantity(), deep.getQuantity());
    }

    @Test
    public void equalsAndHashCodeWork() {
        Menu menu1 = new Menu(1, "menu1", 100, 10, "Food");
        Menu menu2 = new Menu(1, "menu2", 200, 20, "Food"); // idは同じ
        CartItem ci1 = new CartItem(menu1, 3);
        CartItem ci2 = new CartItem(menu2, 3); // quantityも同じ

        assertEquals(ci1, ci2);
        assertEquals(ci1.hashCode(), ci2.hashCode());

        CartItem ci3 = new CartItem(menu1, 4); // quantity違い
        assertNotEquals(ci1, ci3);
    }
}
