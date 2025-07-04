# E-Commerce System

My solution for the Fawry Rise Journey Full Stack Development Internship Challenge. Built this e-commerce system from scratch using Java to handle different product types, shopping carts, and order processing.

## What This System Does

This is a complete e-commerce backend that can handle:

**Different Product Types**
- Products that expire (like Cheese and Biscuits) 
- Products that don't expire (like TVs and Mobile scratch cards)
- Products that need shipping vs digital products

**Shopping Cart Features**
- Add products with quantities
- Checks if items are in stock
- Prevents adding expired products
- Combines duplicate items automatically

**Order Processing**
- Calculates subtotal and shipping fees
- Processes customer payments
- Updates inventory after purchase
- Generates receipts and shipping notices

## Test Cases Included

I built in tests for all the scenarios mentioned in the requirements:
- Normal checkout with mixed products
- What happens when customer can't afford the order
- Empty cart errors
- Out of stock situations
- Expired product handling

## Future Ideas

If I had more time, I'd add:
- A proper database instead of storing everything in memory
- More shipping options (express, standard, etc.)
- Discount codes and promotions
- Better product search and filtering
- Maybe a simple web interface

This was a fun challenge that let me practice object-oriented programming while building something that feels like a real e-commerce system. The requirements were clear and I tried to implement everything exactly as specified while keeping the code clean and maintainable.

---

**Built for**: Fawry Rise Journey Internship Challenge  
**Language**: Java  
**Focus**: Object-Oriented Programming, Error Handling, Clean Code