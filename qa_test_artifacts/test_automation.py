#!/usr/bin/env python3
"""
BiteSwift Restaurant Ordering App - Automated QA Validation Suite
Author: Suhani Kaleeswaran (QA Engineer)
Description: Automated tests validating app business logic, financial rounding,
             session integrity, schema contracts, and regression defects.
"""

import unittest
import re
from decimal import Decimal, ROUND_HALF_UP


# =====================================================================
# Business Logic Implementations (Mirrored from Java ValidationUtils & CartManager)
# =====================================================================

EMAIL_REGEX = re.compile(r"^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$")
PHONE_REGEX = re.compile(r"^[0-9]{10}$")

def is_valid_email(email: str) -> bool:
    if not email or not isinstance(email, str):
        return False
    return bool(EMAIL_REGEX.match(email.strip()))

def is_valid_password(password: str) -> bool:
    if not password or not isinstance(password, str):
        return False
    return len(password.strip()) >= 6

def is_valid_phone(phone: str) -> bool:
    if not phone or not isinstance(phone, str):
        return False
    cleaned = re.sub(r"[\s\-()]", "", phone)
    return bool(PHONE_REGEX.match(cleaned))

def is_valid_address(address: str) -> bool:
    if not address or not isinstance(address, str):
        return False
    return len(address.strip()) >= 5

def calculate_subtotal(cart_items: list) -> float:
    if not cart_items:
        return 0.0
    subtotal = Decimal("0.00")
    for item in cart_items:
        if item.get("available", True) and item.get("quantity", 0) > 0:
            price = Decimal(str(item.get("price", 0.0)))
            qty = Decimal(str(item.get("quantity", 0)))
            subtotal += price * qty
    return float(subtotal.quantize(Decimal("0.01"), rounding=ROUND_HALF_UP))

def calculate_tax(subtotal: float, rate: float = 0.05) -> float:
    if subtotal <= 0 or rate <= 0:
        return 0.0
    sub = Decimal(str(subtotal))
    tax_rate = Decimal(str(rate))
    tax = sub * tax_rate
    return float(tax.quantize(Decimal("0.01"), rounding=ROUND_HALF_UP))

def calculate_grand_total(subtotal: float, tax: float) -> float:
    sub = Decimal(str(max(0.0, subtotal)))
    tx = Decimal(str(max(0.0, tax)))
    return float((sub + tx).quantize(Decimal("0.01"), rounding=ROUND_HALF_UP))


class InMemoryCart:
    """Simulates CartManager singleton state machine."""
    def __init__(self, tax_rate: float = 0.05):
        self.tax_rate = tax_rate
        self.items = {}

    def add_item(self, food_id: str, name: str, price: float, available: bool = True):
        if not available:
            return False
        if food_id in self.items:
            self.items[food_id]["quantity"] += 1
        else:
            self.items[food_id] = {
                "id": food_id,
                "name": name,
                "price": price,
                "quantity": 1,
                "available": available
            }
        return True

    def update_quantity(self, food_id: str, quantity: int):
        if food_id in self.items:
            if quantity <= 0:
                del self.items[food_id]
            else:
                self.items[food_id]["quantity"] = quantity

    def remove_item(self, food_id: str):
        if food_id in self.items:
            del self.items[food_id]

    def clear(self):
        self.items.clear()

    @property
    def item_count(self) -> int:
        return sum(item["quantity"] for item in self.items.values())

    @property
    def subtotal(self) -> float:
        return calculate_subtotal(list(self.items.values()))

    @property
    def tax(self) -> float:
        return calculate_tax(self.subtotal, self.tax_rate)

    @property
    def grand_total(self) -> float:
        return calculate_grand_total(self.subtotal, self.tax)


# =====================================================================
# Automated Test Cases
# =====================================================================

class TestAuthenticationValidation(unittest.TestCase):
    """Test Suite TC-AUTH: Form Input & Boundary Validations"""

    def test_tc_auth_01_valid_emails(self):
        valid_cases = [
            "suhani@example.com",
            "qa.engineer+test@logitech.com",
            "user123@sub.domain.org"
        ]
        for email in valid_cases:
            with self.subTest(email=email):
                self.assertTrue(is_valid_email(email), f"Expected '{email}' to be valid")

    def test_tc_auth_02_invalid_emails(self):
        invalid_cases = [
            None,
            "",
            "   ",
            "plainaddress",
            "@missinguser.com",
            "missingdomain@.com",
            "user@domain"
        ]
        for email in invalid_cases:
            with self.subTest(email=email):
                self.assertFalse(is_valid_email(email), f"Expected '{email}' to be invalid")

    def test_tc_auth_03_password_boundaries(self):
        # Length < 6 is invalid; Length >= 6 is valid
        self.assertFalse(is_valid_password(None))
        self.assertFalse(is_valid_password(""))
        self.assertFalse(is_valid_password("12345"))  # length 5 (boundary - 1)
        self.assertTrue(is_valid_password("123456"))  # length 6 (boundary)
        self.assertTrue(is_valid_password("securePassword@2026"))

    def test_tc_auth_04_phone_number_formats(self):
        self.assertTrue(is_valid_phone("9307793123"))
        self.assertTrue(is_valid_phone("930-779-3123"))
        self.assertTrue(is_valid_phone("(930) 779-3123"))
        self.assertFalse(is_valid_phone("12345"))       # too short
        self.assertFalse(is_valid_phone("930779312345")) # too long
        self.assertFalse(is_valid_phone("930abc3123"))  # alphanumeric


class TestCartAndPricingLogic(unittest.TestCase):
    """Test Suite TC-CART: In-Memory Cart & Financial Precision"""

    def setUp(self):
        self.cart = InMemoryCart()

    def test_tc_cart_01_empty_cart_defaults(self):
        self.assertEqual(self.cart.item_count, 0)
        self.assertEqual(self.cart.subtotal, 0.0)
        self.assertEqual(self.cart.tax, 0.0)
        self.assertEqual(self.cart.grand_total, 0.0)

    def test_tc_cart_02_add_item_and_duplicate_increment(self):
        # Adding new item
        added = self.cart.add_item("item_1", "Margherita Pizza", 299.00)
        self.assertTrue(added)
        self.assertEqual(self.cart.item_count, 1)
        self.assertEqual(self.cart.subtotal, 299.00)

        # Adding same item again must increment quantity, not create duplicate row (Regression: BUG-002)
        self.cart.add_item("item_1", "Margherita Pizza", 299.00)
        self.assertEqual(self.cart.item_count, 2)
        self.assertEqual(len(self.cart.items), 1)
        self.assertEqual(self.cart.subtotal, 598.00)

    def test_tc_cart_03_out_of_stock_item_rejected(self):
        added = self.cart.add_item("item_oos", "Special Dessert", 150.00, available=False)
        self.assertFalse(added)
        self.assertEqual(self.cart.item_count, 0)

    def test_tc_cart_04_quantity_update_and_auto_removal(self):
        self.cart.add_item("item_burger", "Crispy Chicken Burger", 199.00)
        self.cart.update_quantity("item_burger", 3)
        self.assertEqual(self.cart.item_count, 3)
        self.assertEqual(self.cart.subtotal, 597.00)

        # Updating quantity to 0 removes the item
        self.cart.update_quantity("item_burger", 0)
        self.assertEqual(self.cart.item_count, 0)
        self.assertNotIn("item_burger", self.cart.items)

    def test_tc_cart_05_floating_point_rounding_precision(self):
        """Regression test for BUG-001: Rounding errors in tax calculation"""
        # Item with odd fractional price
        self.cart.add_item("item_odd", "Special Pasta", 249.99)
        # Subtotal: 249.99
        # Tax 5%: 249.99 * 0.05 = 12.4995 -> ROUND_HALF_UP = 12.50
        # Grand Total: 249.99 + 12.50 = 262.49
        self.assertEqual(self.cart.subtotal, 249.99)
        self.assertEqual(self.cart.tax, 12.50)
        self.assertEqual(self.cart.grand_total, 262.49)


class TestOrderFlowAndSchemaContract(unittest.TestCase):
    """Test Suite TC-ORD: Order Schema & Firebase Data Integrity"""

    def test_tc_ord_01_valid_order_payload(self):
        order_payload = {
            "orderId": "ORD-1700000000000-A1B2",
            "userId": "user_xyz123",
            "userEmail": "suhani@example.com",
            "userName": "Suhani K",
            "deliveryAddress": "456 Anna Salai, Chennai",
            "paymentMethod": "Cash on Delivery",
            "items": [
                {"foodId": "item_1", "name": "Margherita Pizza", "price": 299.00, "quantity": 1}
            ],
            "subtotal": 299.00,
            "tax": 14.95,
            "grandTotal": 313.95,
            "timestamp": 1700000000000,
            "status": "PLACED"
        }

        # Validate schema contract
        required_keys = [
            "orderId", "userId", "userEmail", "deliveryAddress",
            "paymentMethod", "items", "subtotal", "tax", "grandTotal", "status"
        ]
        for key in required_keys:
            self.assertIn(key, order_payload, f"Order payload missing required key: {key}")

        self.assertTrue(is_valid_address(order_payload["deliveryAddress"]))
        self.assertGreater(len(order_payload["items"]), 0)
        self.assertEqual(
            order_payload["grandTotal"],
            calculate_grand_total(order_payload["subtotal"], order_payload["tax"])
        )

    def test_tc_ord_02_status_transition_lifecycle(self):
        valid_transitions = {
            "PLACED": ["PREPARING", "CANCELLED"],
            "PREPARING": ["OUT_FOR_DELIVERY", "CANCELLED"],
            "OUT_FOR_DELIVERY": ["DELIVERED"],
            "DELIVERED": [],
            "CANCELLED": []
        }

        current_status = "PLACED"
        next_status = "PREPARING"
        self.assertIn(next_status, valid_transitions[current_status])

        current_status = "DELIVERED"
        attempted_transition = "CANCELLED"
        self.assertNotIn(attempted_transition, valid_transitions[current_status])


def run_qa_suite():
    print("=" * 70)
    print("🚀 BiteSwift Restaurant App - Automated QA Validation Suite")
    print("🎯 Execution Mode: Functional & Regression Tests")
    print("=" * 70)
    suite = unittest.TestSuite()
    loader = unittest.defaultTestLoader
    suite.addTest(loader.loadTestsFromTestCase(TestAuthenticationValidation))
    suite.addTest(loader.loadTestsFromTestCase(TestCartAndPricingLogic))
    suite.addTest(loader.loadTestsFromTestCase(TestOrderFlowAndSchemaContract))

    runner = unittest.TextTestRunner(verbosity=2)
    result = runner.run(suite)
    print("\n" + "=" * 70)
    print(f"📊 SUMMARY: Ran {result.testsRun} tests | Failures: {len(result.failures)} | Errors: {len(result.errors)}")
    if result.wasSuccessful():
        print("✅ ALL QA AUTOMATION TESTS PASSED (100% SUCCESS)")
    else:
        print("❌ QA TEST FAILURES DETECTED")
    print("=" * 70)
    return result.wasSuccessful()


if __name__ == "__main__":
    success = run_qa_suite()
    exit(0 if success else 1)
