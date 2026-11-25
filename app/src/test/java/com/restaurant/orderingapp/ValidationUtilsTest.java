package com.restaurant.orderingapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.restaurant.orderingapp.models.CartItem;
import com.restaurant.orderingapp.models.FoodItem;
import com.restaurant.orderingapp.utils.ValidationUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests verifying business validation logic and financial calculation precision.
 */
public class ValidationUtilsTest {

    @Test
    public void testEmailValidation_ValidFormats() {
        assertTrue(ValidationUtils.isValidEmail("user@example.com"));
        assertTrue(ValidationUtils.isValidEmail("suhani.k@company.org"));
        assertTrue(ValidationUtils.isValidEmail("test_user+tag@sub.domain.co"));
    }

    @Test
    public void testEmailValidation_InvalidFormats() {
        assertFalse(ValidationUtils.isValidEmail(null));
        assertFalse(ValidationUtils.isValidEmail(""));
        assertFalse(ValidationUtils.isValidEmail("   "));
        assertFalse(ValidationUtils.isValidEmail("plainaddress"));
        assertFalse(ValidationUtils.isValidEmail("@missingusername.com"));
        assertFalse(ValidationUtils.isValidEmail("user@domain"));
    }

    @Test
    public void testPasswordValidation_LengthBoundary() {
        assertFalse(ValidationUtils.isValidPassword(null));
        assertFalse(ValidationUtils.isValidPassword(""));
        assertFalse(ValidationUtils.isValidPassword("12345")); // length 5 -> false
        assertTrue(ValidationUtils.isValidPassword("123456"));  // length 6 -> true
        assertTrue(ValidationUtils.isValidPassword("StrongPass#2026"));
    }

    @Test
    public void testPhoneValidation_TenDigits() {
        assertFalse(ValidationUtils.isValidPhone(null));
        assertFalse(ValidationUtils.isValidPhone(""));
        assertFalse(ValidationUtils.isValidPhone("12345")); // 5 digits
        assertFalse(ValidationUtils.isValidPhone("98765432101")); // 11 digits
        assertFalse(ValidationUtils.isValidPhone("98765abcde")); // alpha characters
        assertTrue(ValidationUtils.isValidPhone("9876543210"));
        assertTrue(ValidationUtils.isValidPhone("987-654-3210")); // formatted
        assertTrue(ValidationUtils.isValidPhone("(987) 654 3210")); // formatted
    }

    @Test
    public void testCalculateSubtotal_EmptyAndPopulated() {
        assertEquals(0.0, ValidationUtils.calculateSubtotal(null), 0.001);
        assertEquals(0.0, ValidationUtils.calculateSubtotal(new ArrayList<>()), 0.001);

        List<CartItem> items = new ArrayList<>();
        FoodItem pizza = new FoodItem("1", "Pizza", "Desc", 299.00, "Pizza", "", true, 4.8);
        FoodItem shake = new FoodItem("2", "Shake", "Desc", 129.50, "Beverage", "", true, 4.9);

        items.add(new CartItem(pizza, 2)); // 598.00
        items.add(new CartItem(shake, 1)); // 129.50

        // Expected: 598.00 + 129.50 = 727.50
        assertEquals(727.50, ValidationUtils.calculateSubtotal(items), 0.001);
    }

    @Test
    public void testCalculateTax_FivePercent() {
        assertEquals(0.0, ValidationUtils.calculateTax(0.0, 0.05), 0.001);
        assertEquals(0.0, ValidationUtils.calculateTax(-100.0, 0.05), 0.001);

        // Subtotal: 100.00 -> 5.00
        assertEquals(5.00, ValidationUtils.calculateTax(100.00, 0.05), 0.001);

        // Subtotal: 727.50 -> 36.375 -> rounded to 36.38
        assertEquals(36.38, ValidationUtils.calculateTax(727.50, 0.05), 0.001);
    }

    @Test
    public void testCalculateGrandTotal() {
        // Subtotal: 727.50, Tax: 36.38 -> Grand Total: 763.88
        assertEquals(763.88, ValidationUtils.calculateGrandTotal(727.50, 36.38), 0.001);
    }
}
