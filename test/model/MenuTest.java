package test.model;

import org.junit.Test;

import model.Menu;

import static org.junit.Assert.*;

public class MenuTest {
    private Menu dummyMenu(int id, int price) {
        return new Menu(id, "商品" + id, price, 100, "カテゴリ");
    }
    
    @Test
    public void fieldsSetCorrectly() {
        Menu menu = dummyMenu(1,500);

        assertEquals(1,menu.getItemId());
        assertEquals("商品" + menu.getItemId(),menu.getItemName());
        assertEquals(500,menu.getPrice());
        assertEquals(100,menu.getStockQuantity());
        assertEquals("カテゴリ",menu.getCategory());

    }

    @Test
    public void fieldsEqWork() {
        Menu menu1 = dummyMenu(1, 400);
        Menu menu2 = dummyMenu(1, 400);
        Menu menu3 = dummyMenu(1, 600); 
        Menu menu4 = dummyMenu(2, 400);
        Menu menu5 = dummyMenu(2, 600);

        assertEquals(menu1,menu2);  //インスタンスが違ってもidが同じであればeq
        assertEquals(menu1,menu3);  //idが同じであればeq
        assertEquals(menu1,menu3);  //idが同じであれば他(価格)が違ってもeq
        assertNotEquals(menu1,menu4);   //idが違えば他が同じでもneq
        assertNotEquals(menu1,menu5);   //すべて違えば別のもの

    }

    @Test
    public void fieldChangedEqWork() {
        Menu menu1 = dummyMenu(1, 400);
        Menu menu2 = menu1;
        assertEquals(menu2,menu1);  //同じインスタンスを参照なのでequals
        menu1.setStockQuantity(13);
        assertFalse(menu2.setStockQuantity(-15));    //負の在庫設定は禁止されている
        assertEquals(13,menu2.getStockQuantity());  //menu1の変更はmenu2にも反映され、負の在庫設定は失敗したのでequals。
    }

    @Test
    public void copyCreatesDeepCopy() {
        Menu original = dummyMenu(1,300);
        Menu copied = original.copy();

        assertNotSame(original,copied);
        assertEquals(original.getItemId(), copied.getItemId());
        assertEquals(original.getItemName(), copied.getItemName());
        assertEquals(original.getPrice(), copied.getPrice());
        assertEquals(original.getStockQuantity(), copied.getStockQuantity());
        assertEquals(original.getCategory(), copied.getCategory());
    }
}
