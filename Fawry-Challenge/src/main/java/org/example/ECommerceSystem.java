package org.example;

import java.time.LocalDate;
import java.util.*;

interface Expires {
    boolean isExpired();
}

interface Shippable {
    String getName();
    double getWeight();
}

interface ShippingService {
    void ship(List<Shippable> items);
    double calculateShippingFee(List<Shippable> items);
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

class ExpirableShippableProduct extends Product implements Expires, Shippable {
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

class ShippableProduct extends Product implements Shippable {
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

class DigitalProduct extends Product {
    public DigitalProduct(String name, double price, int quantity) {
        super(name, price, quantity);
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

class Customer {
    private String name;
    private double balance;

    public Customer(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() { return name; }
    public double getBalance() { return balance; }

    public void deductBalance(double amount) {
        if (amount > balance) {
            throw new IllegalArgumentException("Customer's balance is insufficient");
        }
        this.balance -= amount;
    }
}

class BasicShippingService implements ShippingService {
    private static final double SHIPPING_RATE_PER_KG = 10.0; // My assumption: 10 units per kg

    @Override
    public void ship(List<Shippable> items) {
        if (items.isEmpty()) return;

        System.out.println("** Shipment notice **");

        Map<String, Integer> itemCounts = new HashMap<>();
        Map<String, Double> itemWeights = new HashMap<>();
        double totalWeight = 0;

        for (Shippable item : items) {
            String name = item.getName();
            itemCounts.put(name, itemCounts.getOrDefault(name, 0) + 1);
            itemWeights.put(name, item.getWeight());
            totalWeight += item.getWeight();
        }

        for (String itemName : itemCounts.keySet()) {
            int count = itemCounts.get(itemName);
            double weightPerItem = itemWeights.get(itemName);
            int totalWeightGrams = (int) (weightPerItem * count * 1000);
            System.out.println(count + "x " + itemName + " " + totalWeightGrams + "g");
        }

        System.out.println("Total package weight " + totalWeight + "kg");
    }

    @Override
    public double calculateShippingFee(List<Shippable> items) {
        double totalWeight = 0;
        for (Shippable item : items) {
            totalWeight += item.getWeight();
        }
        return totalWeight * SHIPPING_RATE_PER_KG;
    }
}

class Cart {
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

class OrderProcessor {
    private final ShippingService shippingService;

    public OrderProcessor(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    public void checkout(Customer customer, Cart cart) {

        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        double subtotal = cart.calculateSubtotal();
        List<Shippable> shippableItems = cart.getShippableItems();
        double shippingFees = shippingService.calculateShippingFee(shippableItems);
        double totalAmount = subtotal + shippingFees;

        customer.deductBalance(totalAmount);

        for (CartItem item : cart.getItems()) {
            item.getProduct().reduceQuantity(item.getQuantity());
        }

        if (!shippableItems.isEmpty()) {
            shippingService.ship(shippableItems);
        }

        printReceipt(cart, subtotal, shippingFees, totalAmount);

        cart.clear();
    }

    private void printReceipt(Cart cart, double subtotal, double shippingFees, double totalAmount) {
        System.out.println("** Checkout receipt **");
        for (CartItem item : cart.getItems()) {
            System.out.println(item.getQuantity() + "x " + item.getProduct().getName() +
                    " " + (int)item.getTotalPrice());
        }
        System.out.println("----------------------");
        System.out.println("Subtotal " + (int)subtotal);
        System.out.println("Shipping " + (int)shippingFees);
        System.out.println("Amount " + (int)totalAmount);
    }
}

public class ECommerceSystem {
    public static void main(String[] args) {
        try {
            ShippingService shippingService = new BasicShippingService();
            OrderProcessor processor = new OrderProcessor(shippingService);

            ExpirableShippableProduct cheese = new ExpirableShippableProduct(
                    "Cheese", 100.0, 10, LocalDate.now().plusDays(30), 0.2);
            ExpirableShippableProduct biscuits = new ExpirableShippableProduct(
                    "Biscuits", 150.0, 5, LocalDate.now().plusDays(60), 0.7);
            ShippableProduct tv = new ShippableProduct("TV", 500.0, 3, 15.0);
            DigitalProduct scratchCard = new DigitalProduct("Mobile Scratch Card", 50.0, 20);

            Customer customer = new Customer("Ahmad Hassan", 2000.0);
            Cart cart = new Cart();

            System.out.println("=== E-Commerce System Demo ===");
            System.out.println("Customer: " + customer.getName());
            System.out.println("Initial Balance: " + customer.getBalance());
            System.out.println();

            // Test case 1: Normal checkout with mixed products
            System.out.println("--- Test Case 1: Normal Checkout ---");
            cart.add(cheese, 2);
            cart.add(biscuits, 1);
            cart.add(tv, 1);
            cart.add(scratchCard, 2);

            processor.checkout(customer, cart);
            System.out.println("Customer remaining balance: " + customer.getBalance());
            System.out.println();

            // Test case 2: insufficient balance
            System.out.println("--- Test Case 2: Insufficient Balance ---");
            Customer poorCustomer = new Customer("Broke Customer", 10.0);
            Cart expensiveCart = new Cart();
            expensiveCart.add(tv, 2);

            try {
                processor.checkout(poorCustomer, expensiveCart);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();

            // Test case 3: Empty cart
            System.out.println("--- Test Case 3: Empty Cart ---");
            Cart emptyCart = new Cart();
            try {
                processor.checkout(customer, emptyCart);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();

            // Test case 4: Out of stock
            System.out.println("--- Test Case 4: Out of Stock ---");
            Cart bigCart = new Cart();
            try {
                bigCart.add(tv, 10);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}