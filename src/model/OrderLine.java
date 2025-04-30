package model;

import java.util.Objects;
/**
 * 商品(Menu)と数量をひとまとまりとして管理するクラス。
 */
public class OrderLine {

    // ======= Field =======
    private Menu menu;
    private int quantity;

    // ======= Constructor =======
    public OrderLine(Menu menu,int quantity) {
        this.menu = menu;
        this.quantity = quantity;
    }

    // ======= Getter / Setter =======
    public Menu getMenu() {
        return menu;
    }

    public int getQuantity() {
        return quantity;
    }

    // `quantity` が正の値であればその値をセットし、trueを返す。変更不可能であればfalseを返す。
    public boolean setQuantity(int quantity) {
        // 変更前後で同じであれば何も行わない
        if (this.quantity == quantity) return true;
        // quantityが負の値であれば却下する
        if (quantity < 0) return false;

        this.quantity = quantity;
        return true;
    }

    // 加算結果が正の値であればその値をセットし、trueを返す。変更不可能であればfalseを返す。
    public boolean addQuantity(int quantity) {
        // 変更前後で同じであれば何も行わない
        if(quantity == 0) return true;
        // 加算結果が負の値であれば却下する
        if(this.quantity + quantity < 0) return false;

        this.quantity += quantity;
        return true;
    }

    // ======= Method =======

    // シャローコピー(後に在庫数を確認・変更すべき状況では、こちらを用いる)
    public OrderLine copy() {
        return new OrderLine(this.getMenu(),this.getQuantity()); 
    }

    // ディープコピー(現在の状態(金額など)を保持したいときは、こちらを用いる)
    public OrderLine deepcopy() {
        return new OrderLine(this.getMenu().copy(),this.getQuantity());
    }

    // ======= Other Method =======

    // OrderLineクラスの区別は(itemId,quantity)で行う
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderLine ci)) return false;
        return quantity == ci.quantity && Objects.equals(menu.getItemId(), ci.menu.getItemId());
    }
    @Override
    public int hashCode() {
        return Objects.hash(menu.getItemId(), quantity);
    }

}
