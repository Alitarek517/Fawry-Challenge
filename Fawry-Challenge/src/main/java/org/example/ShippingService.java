package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

interface ShippingServiceInterface {
    void ship(List<Shippable> items);
    double calculateShippingFee(List<Shippable> items);
}
public class ShippingService implements ShippingServiceInterface {
    private static final double SHIPPING_RATE_PER_KG = 10.0;

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

class OrderProcessor {
    private final ShippingServiceInterface shippingServiceInterface;

    public OrderProcessor(ShippingServiceInterface shippingServiceInterface) {
        this.shippingServiceInterface = shippingServiceInterface;
    }

    public void checkout(Customer customer, Cart cart) {

        if (cart.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        double subtotal = cart.calculateSubtotal();
        List<Shippable> shippableItems = cart.getShippableItems();
        double shippingFees = shippingServiceInterface.calculateShippingFee(shippableItems);
        double totalAmount = subtotal + shippingFees;

        customer.deductBalance(totalAmount);

        for (CartItem item : cart.getItems()) {
            item.getProduct().reduceQuantity(item.getQuantity());
        }

        if (!shippableItems.isEmpty()) {
            shippingServiceInterface.ship(shippableItems);
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