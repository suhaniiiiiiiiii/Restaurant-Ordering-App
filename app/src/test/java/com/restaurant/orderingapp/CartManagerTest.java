package com.restaurant.orderingapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.restaurant.orderingapp.models.FoodItem;
import com.restaurant.orderingapp.utils.CartManager;

import org.junit.Before;
import org.junit.Test;

public class CartManagerTest {

    private CartManager cartManager;
    private FoodItem burger;
    private FoodItem fries;

    @Before
    public void setUp() {
        cartManager = CartManager.getInstance();
        cartManager.clearCart();

        burger = new FoodItem("item_burger", "Cheeseburger", "Juicy grilled beef patty", 150.0, "Burgers", "", true, 4.5);
        fries = new FoodItem("item_fries", "Crispy French Fries", "Golden salted fries", 80.0, "Sides", "", true, 4.2);
    }

    @Test
    public void testEmptyCartInitially() {
        assertTrue(cartManager.isEmpty());
        assertEquals(0, cartManager.getItemCount());
        assertEquals(0.0, cartManager.getSubtotal(), 0.001);
    }

    @Test
    public void testAddItem_NewAndExisting() {
        cartManager.addItem(burger);
        assertFalse(cartManager.isEmpty());
        assertEquals(1, cartManager.getItemCount());
        assertEquals(150.0, cartManager.getSubtotal(), 0.001);

        // Adding same item should increment quantity rather than add duplicate row
        cartManager.addItem(burger);
        assertEquals(2, cartManager.getItemCount());
        assertEquals(1, cartManager.getItems().size());
        assertEquals(300.0, cartManager.getSubtotal(), 0.001);
    }

    @Test
    public void testUnavailableItemNotAdded() {
        FoodItem outOfStock = new FoodItem("item_oos", "Special Dessert", "None left", 200.0, "Dessert", "", false, 4.0);
        cartManager.addItem(outOfStock);
        assertTrue(cartManager.isEmpty());
    }

    @Test
    public void testUpdateQuantity() {
        cartManager.addItem(burger);
        cartManager.updateQuantity("item_burger", 3);
        assertEquals(3, cartManager.getItemCount());
        assertEquals(450.0, cartManager.getSubtotal(), 0.001);

        // Setting quantity to 0 removes the item
        cartManager.updateQuantity("item_burger", 0);
        assertTrue(cartManager.isEmpty());
    }

    @Test
    public void testRemoveItem() {
        cartManager.addItem(burger);
        cartManager.addItem(fries);
        assertEquals(2, cartManager.getItems().size());

        cartManager.removeItem("item_burger");
        assertEquals(1, cartManager.getItems().size());
        assertEquals("item_fries", cartManager.getItems().get(0).getFoodItem().getId());
    }

    @Test
    public void testTaxAndGrandTotalCalculation() {
        cartManager.addItem(burger); // 150.0
        cartManager.addItem(fries);  // 80.0
        // Subtotal = 230.00
        // Tax 5% = 11.50
        // Grand Total = 241.50
        assertEquals(230.00, cartManager.getSubtotal(), 0.001);
        assertEquals(11.50, cartManager.getTax(), 0.001);
        assertEquals(241.50, cartManager.getGrandTotal(), 0.001);
    }
}
