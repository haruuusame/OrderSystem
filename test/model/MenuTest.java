package test.model;

import org.junit.Test;

import model.Menu;

import static org.junit.Assert.*;

public class MenuTest {
    
    @Test
    public void fieldsSetCorrectly() {
        Menu menu = new Menu(1,"ハンバーガー",300,10,"Food");

        assertEquals(1,menu.getItemId());
        assertEquals("ハンバーガー",menu.getItemName());
        assertEquals(300,menu.getPrice());
        assertEquals(10,menu.getStockQuantity());
        assertEquals("Food",menu.getCategory());

    }

    @Test
    public void fieldsEqWork() {
        Menu menu1 = new Menu(2, "ポテト", 200, 5, "Side");
        Menu menu2 = new Menu(2, "ポテト", 200, 5, "Side");
        Menu menu3 = new Menu(3, "コーラ", 150, 8, "Drink");

        assertEquals(menu1,menu2);
        assertNotEquals(menu1,menu3);

    }

    @Test
    public void fieldChangedEqWork() {
        Menu menu1 = new Menu(2, "ポテト", 200, 5, "Side");
        Menu menu2 = menu1;
        assertEquals(menu2,menu1);  //同じインスタンスを参照なのでequals
        menu1.setStockQuantity(13);
        assertFalse(menu2.setStockQuantity(-15));    //負の在庫設定は禁止されている
        assertEquals(13,menu2.getStockQuantity());  //menu1の変更はmenu2にも反映され、負の在庫設定は失敗したのでequals。
    }
}
