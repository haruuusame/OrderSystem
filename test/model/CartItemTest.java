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

        // 元と同じ値に変更(成功)
        assertTrue(item.setQuantity(3));        
        assertEquals(3, item.getQuantity());    

        // 値をマイナスに変更(失敗)
        assertFalse(item.setQuantity(-1));               
        assertEquals(3, item.getQuantity());    

        // 大きい値に変更(成功)
        assertTrue(item.setQuantity(5));
        assertEquals(5, item.getQuantity());
    }

    @Test
    public void addQuantityWorks() {
        CartItem item = new CartItem(dummyMenu(), 5);

        // 0を加算(成功)
        assertTrue(item.addQuantity(0));
        assertEquals(5, item.getQuantity());

        // 正の値を加算(成功)
        assertTrue(item.addQuantity(3));
        assertEquals(8, item.getQuantity());

        // 結果がマイナスになる加算(失敗)
        assertFalse(item.addQuantity(-10));
        assertEquals(8, item.getQuantity());

        // 結果が0になる加算(成功)
        assertTrue(item.addQuantity(-8));
        assertEquals(0, item.getQuantity());
    }

    @Test
    public void copyCreatesDeepCopy() {
        CartItem original = new CartItem(dummyMenu(), 4);
        CartItem copy = original.copy();

        assertNotSame(original, copy); // 別インスタンス
        assertSame(original.getMenu(),copy.getMenu()); // メニューは同じインスタンス

        // 値は一致
        assertEquals(original.getQuantity(), copy.getQuantity());
        assertEquals(original.getMenu(), copy.getMenu()); 
    }
}
