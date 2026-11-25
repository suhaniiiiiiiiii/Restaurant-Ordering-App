# Defect Tracking & Bug Report Log

**Project:** BiteSwift Restaurant Ordering App  
**QA Lead:** Suhani Kaleeswaran (Software QA Engineer)  
**Lifecycle Status:** All Logged Defects Verified & Closed Prior to Release  

---

### Defect Summary Table

| Defect ID | Module | Severity | Priority | Description | Status | Verification |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **BUG-001** | Cart / Billing | Critical | P1 | Floating point rounding error causing inaccurate grand totals | **CLOSED** | Verified via Unit & Python Tests |
| **BUG-002** | Cart | High | P1 | Adding same food item repeatedly created duplicate list rows | **CLOSED** | Verified via `CartManagerTest` |
| **BUG-003** | Auth / Session | High | P2 | App crash on launch when offline with active user session | **CLOSED** | Verified on flight mode emulator |
| **BUG-004** | Checkout | Medium | P2 | Cart session was not cleared after order successfully placed | **CLOSED** | Verified in end-to-end checkout flow |
| **BUG-005** | Menu Filtering | Medium | P3 | Search filter crashed when menu item description was null | **CLOSED** | Verified with boundary test data |

---

### Detailed Defect Reports & Root Cause Analysis (RCA)

#### 🐛 Defect ID: BUG-001
- **Title:** Floating point rounding error causing inaccurate grand totals (e.g. ₹262.489999999 instead of ₹262.49).
- **Component:** `ValidationUtils.java`, `CartManager.java`
- **Severity:** Critical | **Priority:** P1
- **Steps to Reproduce:**
  1. Add an item priced with fractional value (e.g. ₹249.99).
  2. Compute 5% tax: `249.99 * 0.05 = 12.4995`.
  3. Total displayed on screen showed IEEE 754 precision error `262.48999999999995`.
- **Root Cause:** Standard `double` binary arithmetic was used directly without scale quantizing or decimal rounding.
- **Fix Implemented:** Replaced raw `double` math with `java.math.BigDecimal` and explicit `RoundingMode.HALF_UP` with 2 decimal scale:
  ```java
  BigDecimal sub = BigDecimal.valueOf(subtotal);
  BigDecimal rate = BigDecimal.valueOf(taxRate);
  return sub.multiply(rate).setScale(2, RoundingMode.HALF_UP).doubleValue();
  ```
- **Regression Verification:** Added automated unit test `ValidationUtilsTest.testCalculateTax_FivePercent` and Python test `test_tc_cart_05_floating_point_rounding_precision`. Verified output is strictly `12.50` and Grand Total is `262.49`.

---

#### 🐛 Defect ID: BUG-002
- **Title:** Adding identical food item multiple times created duplicate rows in cart rather than incrementing quantity.
- **Component:** `CartManager.java`, `CartAdapter.java`
- **Severity:** High | **Priority:** P1
- **Steps to Reproduce:**
  1. Click 'Add to Cart' on Margherita Pizza.
  2. Click 'Add to Cart' on Margherita Pizza a second time.
  3. Navigate to Cart screen.
  4. Observed: Two distinct rows for Margherita Pizza each with quantity 1.
- **Root Cause:** `addItem()` was blindly executing `cartItems.add(new CartItem(foodItem, 1))` without searching existing list by `foodItem.getId()`.
- **Fix Implemented:** Added search loop checking if item already exists; if found, increment existing quantity:
  ```java
  for (CartItem item : cartItems) {
      if (item.getFoodItem().getId().equals(foodItem.getId())) {
          item.setQuantity(item.getQuantity() + 1);
          return;
      }
  }
  cartItems.add(new CartItem(foodItem, 1));
  ```
- **Regression Verification:** Verified via `CartManagerTest.testAddItem_NewAndExisting`. Verified only 1 row exists with `quantity = 2`.

---

#### 🐛 Defect ID: BUG-003
- **Title:** `NullPointerException` when launching app in offline / airplane mode with cached user credentials.
- **Component:** `SplashActivity.java`, `FirebaseHelper.java`
- **Severity:** High | **Priority:** P2
- **Steps to Reproduce:**
  1. Log into the app on device.
  2. Turn on Airplane Mode (no internet).
  3. Force kill app and re-open.
  4. Observed: App crashed with Firebase unhandled exception.
- **Root Cause:** Firebase Database disk persistence was disabled by default, causing network lookup hangs and null reference exceptions.
- **Fix Implemented:** Enabled offline persistence on database instance initialization:
  ```java
  database.setPersistenceEnabled(true);
  ```
- **Regression Verification:** Tested cold launch with network disabled. SplashActivity smoothly transitions to cached MainActivity without crash.

---

#### 🐛 Defect ID: BUG-004
- **Title:** Cart was not cleared after order placement, allowing duplicate submissions.
- **Component:** `CheckoutActivity.java`
- **Severity:** Medium | **Priority:** P2
- **Steps to Reproduce:**
  1. Add items to cart and navigate to checkout.
  2. Place order successfully.
  3. Press back button from Order History to return to MainActivity.
  4. Cart badge still showed previous item count.
- **Root Cause:** `CartManager.getInstance().clearCart()` was omitted from the `onComplete` success callback of `orderRef.setValue()`.
- **Fix Implemented:** Explicitly called `cm.clearCart()` immediately upon confirmed order creation.
- **Regression Verification:** Verified cart count resets to 0 and CartActivity displays empty state view after order placement.

---

#### 🐛 Defect ID: BUG-005
- **Title:** `NullPointerException` during search filtering when a menu item had a null description.
- **Component:** `MainActivity.java`
- **Severity:** Medium | **Priority:** P3
- **Steps to Reproduce:**
  1. Seed a menu item in Firebase with empty or null description string.
  2. Type any character in the search bar.
  3. Observed: Crash at `item.getDescription().toLowerCase()`.
- **Root Cause:** Missing null-safety check before invoking `.toLowerCase()` on description property.
- **Fix Implemented:** Added null-check before string evaluation:
  ```java
  (item.getDescription() != null && item.getDescription().toLowerCase(Locale.getDefault()).contains(currentSearchQuery))
  ```
- **Regression Verification:** Tested with menu items missing descriptions; filtering functions without crash.
