package org.example;

import java.time.LocalDate;
public class ECommerceSystem {
    public static void main(String[] args) {
        try {
            ShippingServiceInterface shippingServiceInterface = new ShippingService();
            OrderProcessor processor = new OrderProcessor(shippingServiceInterface);

            ExpirableShippableProduct cheese = new ExpirableShippableProduct(
                    "Cheese", 100.0, 10, LocalDate.now().plusDays(30), 0.2);
            ExpirableShippableProduct biscuits = new ExpirableShippableProduct(
                    "Biscuits", 150.0, 5, LocalDate.now().plusDays(60), 0.7);
            ShippableProduct tv = new ShippableProduct("TV", 500.0, 3, 15.0);


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