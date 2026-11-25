# QA Test Plan: BiteSwift Restaurant Ordering App

| Field | Detail |
| :--- | :--- |
| **Project** | Restaurant Ordering App (Android & Firebase) |
| **QA Lead / Author** | Suhani Kaleeswaran (Software QA Engineer) |
| **Version** | 1.0.0 |
| **Testing Scope** | Functional, Regression, Integration, Boundary, UI/UX, Automation |
| **Target Platforms** | Android 7.0 (API 24) to Android 14 (API 34) |

---

## 1. Executive Summary & Objective
The objective of this test cycle is to ensure the **BiteSwift Restaurant Ordering App** delivers an error-free, seamless end-to-end user experience across authentication, menu browsing, cart management, checkout, and real-time order tracking. Testing guarantees 100% data integrity with Firebase Authentication and Realtime Database, robust offline resilience, and accurate billing/tax computations.

---

## 2. Scope of Testing

### 2.1 In Scope
- **Firebase Authentication & User Sessions:** Registration, login, invalid credentials handling, persistent session restoration upon app relaunch, logout.
- **Menu Browsing & Search:** Category-based filtering (Pizza, Burgers, Wraps, Pasta, Beverages), dynamic text search, availability flags, price and rating rendering.
- **Cart Management:** Add item, quantity increment/decrement, zero-quantity auto-removal, subtotal calculation, 5% tax computation with `BigDecimal` half-up rounding, Grand Total accuracy.
- **Checkout & Order Placement:** Delivery address validation, payment method selection (Cash on Delivery, UPI), unique order ID generation (`ORD-<timestamp>-<hash>`), atomic write to Firebase Realtime Database.
- **Real-Time Order Tracking:** Synchronous status updates (`PLACED` -> `PREPARING` -> `OUT_FOR_DELIVERY` -> `DELIVERED`), order history chronologically sorted.
- **Non-Functional & Boundary Scenarios:** Field boundary limits (5 vs 6 character passwords, 10-digit phone number formatting), empty cart checkout attempts, network drop / reconnect handling.

### 2.2 Out of Scope
- Third-party bank gateway sandbox redirects (simulated via UPI/COD options).
- Hardware printer integration for kitchen order tickets (KOT).

---

## 3. Test Strategy & Methodology

```
+-------------------------------------------------------------+
|                     Manual Exploratory & UI                 |
|               (Visual, UX, Navigation, Layouts)             |
+-------------------------------------------------------------+
                              |
+-------------------------------------------------------------+
|              Android Instrumentation / Espresso             |
|       (LoginActivityEspressoTest, Field Validation Tests)   |
+-------------------------------------------------------------+
                              |
+-------------------------------------------------------------+
|                  Automated Python Test Suite                |
|        (Business logic, Pricing precision, Schema checks)   |
+-------------------------------------------------------------+
                              |
+-------------------------------------------------------------+
|                      JUnit Unit Testing                     |
|           (ValidationUtilsTest, CartManagerTest)            |
+-------------------------------------------------------------+
```

1. **Unit Testing:** Executed via JUnit 4 and Mockito verifying individual business logic methods in isolation (`ValidationUtilsTest`, `CartManagerTest`).
2. **Automated Verification:** Standalone Python test automation suite executing regression verification across pricing, string schemas, and boundary limits.
3. **UI / Instrumentation Testing:** Espresso test cases verifying view rendering, user interaction, keyboard actions, and error message banners.
4. **End-to-End Regression Testing:** Executed prior to release candidate sign-off ensuring bug fixes do not cause regressions.

---

## 4. Entry and Exit Criteria

### 4.1 Entry Criteria
- Source code builds without compilation errors or warnings.
- Firebase project configuration and rules deployed.
- Test environments (emulators / physical devices) configured.

### 4.2 Exit Criteria
- 100% of P0 (Blocker) and P1 (Critical) test cases executed and passed.
- Zero open Blocker or Critical defects.
- All automated unit and regression test suites passing (100% pass rate).
- Final regression testing signed off by QA.
