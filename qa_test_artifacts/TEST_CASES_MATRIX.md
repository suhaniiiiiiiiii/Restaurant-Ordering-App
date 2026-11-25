# Test Cases Specification & Execution Matrix

**Project:** BiteSwift Restaurant Ordering App  
**QA Lead:** Suhani Kaleeswaran (Software QA Engineer)  
**Total Test Cases:** 24  
**Passed:** 24 | **Failed:** 0 | **Blocked:** 0  

---

## 1. Authentication & Session Management (TC-AUTH)

| TC ID | Scenario | Pre-conditions | Test Steps | Test Data | Expected Result | Actual Result | Status | Severity |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-AUTH-001** | User Registration with valid credentials | App installed, on Registration screen | 1. Enter valid Name, Email, Phone, Password.<br>2. Click 'Sign Up'. | Name: "Suhani K"<br>Email: "suhani@test.com"<br>Phone: "9307793123"<br>Pass: "Pass@123" | Firebase Auth account created, user profile stored in `/users/{uid}`, navigated to Menu screen. | As Expected | **PASS** | Critical |
| **TC-AUTH-002** | Registration with invalid email format | On Registration screen | 1. Enter invalid email without `@` or domain.<br>2. Click 'Sign Up'. | Email: "invalid_email_str" | Error message displayed on email field: "Please enter a valid email address". Request blocked. | As Expected | **PASS** | High |
| **TC-AUTH-003** | Registration with short password (Boundary: 5 chars) | On Registration screen | 1. Enter 5 character password.<br>2. Click 'Sign Up'. | Password: "12345" | Error message: "Password must be at least 6 characters". | As Expected | **PASS** | High |
| **TC-AUTH-004** | Registration with non-10 digit phone number | On Registration screen | 1. Enter 9 digit or alphabetical phone.<br>2. Click 'Sign Up'. | Phone: "987654321" | Error: "Please enter a valid 10-digit phone number". | As Expected | **PASS** | Medium |
| **TC-AUTH-005** | User Login with valid credentials | User registered in Firebase | 1. Enter valid email and password.<br>2. Click 'Sign In'. | Registered credentials | User successfully authenticated, session cached, navigated to MainActivity. | As Expected | **PASS** | Critical |
| **TC-AUTH-006** | User Login with incorrect password | User registered in Firebase | 1. Enter valid email and wrong password.<br>2. Click 'Sign In'. | Wrong password: "wrong_password" | Authentication error toast displayed: "Login Failed: The password is invalid". | As Expected | **PASS** | High |
| **TC-AUTH-007** | Session Persistence upon app kill & relaunch | User logged in | 1. Close and kill the app task.<br>2. Re-open the app. | Existing active session | SplashActivity detects `FirebaseUser != null`, bypasses Login, directly opens MainActivity. | As Expected | **PASS** | High |
| **TC-AUTH-008** | User Logout | User logged in on MainActivity | 1. Click overflow menu.<br>2. Select 'Logout'. | None | User signed out from Firebase, cart cleared, redirected to LoginActivity. | As Expected | **PASS** | Medium |

---

## 2. Menu Browsing & Search Filtering (TC-MENU)

| TC ID | Scenario | Pre-conditions | Test Steps | Test Data | Expected Result | Actual Result | Status | Severity |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-MENU-001** | Real-time menu items fetch from Firebase | Database contains menu items | 1. Launch MainActivity. | Firebase `/menu_items` | RecyclerView displays food cards with Name, Price, Description, Rating, and Category. | As Expected | **PASS** | Critical |
| **TC-MENU-002** | Category chip filtering | Menu loaded | 1. Tap 'Pizza' chip.<br>2. Tap 'Beverages' chip. | Categories: "Pizza", "Beverages" | List dynamically updates to show only items belonging to the selected category. | As Expected | **PASS** | High |
| **TC-MENU-003** | Dynamic search query filtering | Menu loaded | 1. Type "burger" in search bar.<br>2. Clear search bar. | Query: "burger" | Only items containing "burger" in name or description are shown. Clearing restores list. | As Expected | **PASS** | Medium |
| **TC-MENU-004** | Empty search result handling | Menu loaded | 1. Search for non-existent item name. | Query: "xyz999" | "No menu items match your search" placeholder message displayed cleanly. | As Expected | **PASS** | Low |

---

## 3. Cart Management & Calculations (TC-CART)

| TC ID | Scenario | Pre-conditions | Test Steps | Test Data | Expected Result | Actual Result | Status | Severity |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-CART-001** | Add single item to cart | Item available in menu | 1. Click 'Add to Cart' on item card. | Food Item: ₹299.00 | Cart count increments to 1, Toast confirmation displayed, Cart FAB shows updated total. | As Expected | **PASS** | Critical |
| **TC-CART-002** | Duplicate item addition increases quantity | Item already in cart (qty: 1) | 1. Click 'Add to Cart' on same item again. | Food Item: ₹299.00 | Cart maintains single row with quantity = 2; total reflects ₹598.00. No duplicate row. | As Expected | **PASS** | High |
| **TC-CART-003** | Increment item quantity from Cart screen | Item in cart | 1. Open CartActivity.<br>2. Click '+' button. | Current Qty: 1 | Quantity increases to 2, subtotal, 5% tax, and grand total recalculate dynamically. | As Expected | **PASS** | High |
| **TC-CART-004** | Decrement item quantity to zero removes row | Item in cart with qty: 1 | 1. Open Cart.<br>2. Click '-' button. | Current Qty: 1 | Item is removed from cart list. If cart is now empty, empty cart view is shown. | As Expected | **PASS** | High |
| **TC-CART-005** | Direct item deletion via trash icon | Item in cart | 1. Click trash icon next to item. | Any cart item | Item immediately removed from cart; totals update; toast feedback given. | As Expected | **PASS** | Medium |
| **TC-CART-006** | Financial rounding precision on fractional prices | Items with fractional prices in cart | 1. Add item priced ₹249.99.<br>2. Verify Tax & Grand Total. | Price: ₹249.99, Tax Rate: 5% | Subtotal: ₹249.99, Tax: ₹12.50 (rounded HALF_UP from 12.4995), Grand Total: ₹262.49. | As Expected | **PASS** | Critical |

---

## 4. Checkout & Real-time Order Placement (TC-ORD)

| TC ID | Scenario | Pre-conditions | Test Steps | Test Data | Expected Result | Actual Result | Status | Severity |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-ORD-001** | Checkout with empty cart | Cart is empty | 1. Click 'Checkout' button. | Cart Items: 0 | Action prevented; Toast "Cart is empty" shown. Navigation blocked. | As Expected | **PASS** | High |
| **TC-ORD-002** | Address field validation (< 5 chars) | Items in cart | 1. Enter address "St".<br>2. Click 'Place Order'. | Address: "St" | Error displayed on TextInputLayout: "Please enter a complete delivery address". | As Expected | **PASS** | High |
| **TC-ORD-003** | Successful Order Placement with COD | Valid items, valid address | 1. Select 'Cash on Delivery'.<br>2. Click 'Place Order'. | Address: "45 Anna Nagar, Chennai", Payment: "COD" | Order record created in `/orders/{orderId}`, cart cleared, user redirected to Order History. | As Expected | **PASS** | Critical |
| **TC-ORD-004** | Real-time Order Status Synchronization | Order placed in `/orders/{orderId}` | 1. Update order status in Firebase DB to 'PREPARING' then 'DELIVERED'. | Status: "PREPARING" -> "DELIVERED" | OrderHistoryActivity dynamically updates status badge color and text without manual refresh. | As Expected | **PASS** | Critical |
| **TC-ORD-005** | Order history chronological sort order | Multiple orders placed by user | 1. Open Order History screen. | Multiple order records | Most recently placed orders appear at the top of the list (`Collections.reverse`). | As Expected | **PASS** | Medium |
| **TC-ORD-006** | User data isolation | Two distinct user accounts | 1. Login as User A and place order.<br>2. Logout, login as User B. | User A vs User B | User B can only see their own placed orders; User A's orders are not visible. | As Expected | **PASS** | Critical |
