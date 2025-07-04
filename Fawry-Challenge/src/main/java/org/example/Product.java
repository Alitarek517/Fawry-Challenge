package org.example;

import java.time.LocalDate;
import java.util.Objects;
interface Expires {
    boolean isExpired();
}
interface Shippable{
    String getName();
    double getWeight();
}
abstract class Product {
    protected String name;
    protected double price;
    protected int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }


    public String getName() { return name; }
    public double getPrice() { return price; }

    public boolean hasAvailableQuantity(int requestedQty) {
        return quantity >= requestedQty;
    }

    public void reduceQuantity(int amount) {
        if (amount > quantity) {
            throw new IllegalArgumentException("Cannot reduce quantity: not enough available");
        }
        this.quantity -= amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
class ExpirableShippableProduct extends Product implements Expires {
    private LocalDate expiryDate;
    private double weight;

    public ExpirableShippableProduct(String name, double price, int quantity,
                                     LocalDate expiryDate, double weight) {
        super(name, price, quantity);
        this.expiryDate = expiryDate;
        this.weight = weight;
    }

    @Override
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public boolean hasAvailableQuantity(int requestedQty) {
        return !isExpired() && super.hasAvailableQuantity(requestedQty);
    }
}

class ShippableProduct extends Product {
    private double weight;

    public ShippableProduct(String name, double price, int quantity, double weight) {
        super(name, price, quantity);
        this.weight = weight;
    }

    @Override
    public double getWeight() {
        return weight;
    }
}
