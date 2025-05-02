package controller.cli;

import java.util.List;
import java.util.Optional;

import model.Cart;
import model.Menu;
import model.MenuCatalog;
import model.Order;

public class PurchaseController {
    private Cart cart;
    private MenuCatalog catalog;
    
    public PurchaseController(Cart cart,MenuCatalog catalog) {
        this.cart = cart;
        this.catalog = catalog;
    }

    public boolean addCart(int itemId, int quantity) {
        if (!catalog.has(itemId)) return false;
        Menu menu = catalog.get(itemId);
        return cart.addItem(menu, quantity);
    }

    public Optional<Order> checkout() {
        Optional<Order> checkout = cart.checkout();
        return checkout;
    }

    public boolean updateCartItemQuantity(int itemId, int quantity) {
        if (!catalog.has(itemId)) return false;
        return cart.updateQuantity(itemId, quantity);
    }

    public List<Menu> catalogFindByCategory(String category_filter) {
        return catalog.findByCategory(category_filter);
    } 

    


}
