package org.example;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items = new ArrayList<>();

    public void add(Product product, int quantity) {

        if (product instanceof Expires && ((Expires) product).isExpired()) {
            throw new IllegalArgumentException("one product is out of stock or expired");
        }

        if (!product.hasAvailableQuantity(quantity)) {
            throw new IllegalArgumentException("one product is out of stock or expired");
        }

        for (CartItem existingItem : items) {
            if (existingItem.getProduct().equals(product)) {
                existingItem.addQuantity(quantity);
                return;
            }
        }

        items.add(new CartItem(product, quantity));
    }

    public List<CartItem> getItems() { return new ArrayList<>(items); }
    public boolean isEmpty() { return items.isEmpty(); }

    public double calculateSubtotal() {
        double subtotal = 0;
        for (CartItem item : items) {
            subtotal += item.getTotalPrice();
        }
        return subtotal;
    }

    public List<Shippable> getShippableItems() {
        List<Shippable> shippableItems = new ArrayList<>();
        for (CartItem item : items) {
            if (item.getProduct() instanceof Shippable) {
                for (int i = 0; i < item.getQuantity(); i++) {
                    shippableItems.add((Shippable) item.getProduct());
                }
            }
        }
        return shippableItems;
    }

    public void clear() {
        items.clear();
    }
}
class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return product.getPrice() * quantity; }

    public void addQuantity(int additionalQty) {
        this.quantity += additionalQty;
    }
}
