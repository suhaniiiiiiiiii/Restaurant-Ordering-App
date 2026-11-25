# 🍽️ BiteSwift - Restaurant Ordering Android Application

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Java-orange.svg)](https://www.java.com)
[![Backend](https://img.shields.io/badge/Backend-Firebase-yellow.svg)](https://firebase.google.com)
[![QA Tests](https://img.shields.io/badge/Tests-Passed%20100%25-brightgreen.svg)]()
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A high-performance, real-time Android food ordering application developed in **Java** and **Android Studio**, integrated with **Firebase Authentication** and **Firebase Realtime Database**. Designed, built, and rigorously tested with industry-standard QA methodologies, end-to-end regression suites, and automated test procedures.

---

## 📌 Project Overview
BiteSwift allows users to browse diverse food categories, search dishes dynamically, manage a real-time shopping cart with automatic price and tax computation, place orders with flexible payment options, and track live order status updates as the kitchen prepares and delivers the order.

### ✨ Key Features
1. **Firebase Authentication & Session Persistence:**
   - Email/password user signup, login, and secure session caching with auto-login on app launch.
   - Comprehensive input validation (RegEx email check, boundary password rules, 10-digit phone verification).
2. **Dynamic Menu & Filtering:**
   - Real-time catalog sync from Firebase Realtime Database.
   - Category chips (All, Pizza, Burgers, Wraps, Pasta, Beverages) and live search query filtering.
   - Stock availability checks preventing out-of-stock item additions.
3. **Cart Management & Precision Financials:**
   - In-memory thread-safe `CartManager` singleton.
   - Quantity increment, decrement, and zero-quantity auto removal.
   - `BigDecimal` with `HALF_UP` rounding to prevent IEEE 754 floating-point precision errors during 5% tax and grand total calculations.
4. **Checkout & Real-time Order Sync:**
   - Multi-line delivery address validation.
   - Flexible payment method selection (Cash on Delivery, UPI).
   - Atomic order creation in Firebase Realtime Database under `/orders/{orderId}` and user profile.
   - Live order lifecycle updates (`PLACED` -> `PREPARING` -> `OUT_FOR_DELIVERY` -> `DELIVERED`).

---

## 📁 Repository & Architecture Structure

```
.
├── app/
│   ├── build.gradle                     # Android build config & dependencies
│   ├── proguard-rules.pro               # Proguard rules
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml      # App permissions, activities declaration
│       │   ├── java/com/restaurant/orderingapp/
│       │   │   ├── activities/          # UI Controllers
│       │   │   │   ├── SplashActivity.java
│       │   │   │   ├── LoginActivity.java
│       │   │   │   ├── RegisterActivity.java
│       │   │   │   ├── MainActivity.java
│       │   │   │   ├── CartActivity.java
│       │   │   │   ├── CheckoutActivity.java
│       │   │   │   └── OrderHistoryActivity.java
│       │   │   ├── adapters/            # RecyclerView Adapters
│       │   │   │   ├── MenuAdapter.java
│       │   │   │   ├── CartAdapter.java
│       │   │   │   └── OrderAdapter.java
│       │   │   ├── models/              # Data Models
│       │   │   │   ├── FoodItem.java
│       │   │   │   ├── CartItem.java
│       │   │   │   ├── Order.java
│       │   │   │   └── User.java
│       │   │   └── utils/               # Helpers & Singletons
│       │   │       ├── ValidationUtils.java
│       │   │       ├── CartManager.java
│       │   │       └── FirebaseHelper.java
│       │   └── res/                     # Layouts, Vector Drawables, Values & Themes
│       │       ├── layout/
│       │       ├── drawable/
│       │       ├── menu/
│       │       └── values/
│       ├── test/java/...                # JUnit 4 Unit Tests
│       │   ├── ValidationUtilsTest.java
│       │   └── CartManagerTest.java
│       └── androidTest/java/...         # Espresso UI Instrumentation Tests
│           └── LoginActivityEspressoTest.java
├── qa_test_artifacts/                   # QA Documentation & Automation
│   ├── TEST_PLAN.md                     # Comprehensive QA Strategy & Plan
│   ├── TEST_CASES_MATRIX.md             # 24 Functional & Regression Test Cases
│   ├── DEFECT_LOG.md                    # Root Cause Analysis & Closed Defect Logs
│   └── test_automation.py               # Executable Python Test Automation Suite
├── build.gradle                         # Root project build file
├── settings.gradle                      # Project settings & repositories
├── gradle.properties                    # JVM & AndroidX flags
└── README.md                            # Project documentation
```

---

## ⚙️ Setup & Installation Instructions

### Prerequisites
- **Android Studio Iguana | 2023.2.1** or newer.
- **JDK 17 or JDK 1.8** configured in Android Studio.
- Android Device or Emulator running **API 24 (Android 7.0)** or higher.

### Step 1: Open Project in Android Studio
1. Launch Android Studio.
2. Select **Open** and navigate to this project directory:
   `/Users/suhaniyadav/Desktop/chatbot`
3. Allow Gradle to sync dependencies automatically.

### Step 2: Configure Firebase
1. Create a Firebase project in the [Firebase Console](https://console.firebase.google.com).
2. Enable **Email/Password Authentication** under *Authentication > Sign-in method*.
3. Create a **Realtime Database** under *Realtime Database* and configure basic read/write rules:
   ```json
   {
     "rules": {
       ".read": "auth != null",
       ".write": "auth != null"
     }
   }
   ```
4. Download your `google-services.json` from Project Settings and place it in the `app/` folder (`app/google-services.json`).

### Step 3: Run the Application
1. Connect a physical Android device (with USB debugging enabled) or start an Android Virtual Device (AVD).
2. Click **Run > Run 'app'** (`Shift + F10`).
3. The sample menu items will automatically seed into Firebase Realtime Database on initial launch if the database is empty!

---

## 🧪 Testing & Quality Assurance

### 1. Running Unit Tests (JUnit)
In Android Studio:
- Right-click `app/src/test/java` and select **Run 'All Tests'**
Or from terminal:
```bash
./gradlew test
```

### 2. Running UI Instrumentation Tests (Espresso)
- Connect a test device or emulator.
- Right-click `app/src/androidTest/java` and select **Run 'LoginActivityEspressoTest'**.
Or from terminal:
```bash
./gradlew connectedAndroidTest
```

### 3. Running Automated Python Test Suite
This suite automates business logic verification, precision floating point checks, and schema contracts:
```bash
python3 qa_test_artifacts/test_automation.py
```
**Output:**
```
======================================================================
🚀 BiteSwift Restaurant App - Automated QA Validation Suite
🎯 Execution Mode: Functional & Regression Tests
======================================================================
...
Ran 11 tests in 0.001s
OK
📊 SUMMARY: Ran 11 tests | Failures: 0 | Errors: 0
✅ ALL QA AUTOMATION TESTS PASSED (100% SUCCESS)
======================================================================
```

---

## 💡 Interview QA Talking Points for Suhani
When presenting this project in engineering and QA interviews (e.g. for Logitech / Software QA Engineer roles):

1. **Test-Driven Architecture & Defensive Coding:**
   - *"I ensured that financial calculations for subtotal, 5% tax, and grand totals used `BigDecimal` with half-up rounding rather than raw IEEE 754 floats, eliminating rounding discrepancies in checkout."*
2. **Defect Lifecycle Management:**
   - *"I logged 5 defects during development, including duplicate row additions in the cart, offline crash on cold launch, and unhandled null descriptions in search. I implemented root cause fixes and verified them using regression test cases before release."*
3. **Dual Manual & Automated Verification:**
   - *"I authored a 24-case test matrix covering positive, negative, and boundary scenarios (e.g., password length boundary at 5 vs 6 characters, phone regex formatting), and built automated verification scripts in Python and Espresso to guarantee continuous test coverage."*
4. **Real-Time Data Synchronization:**
   - *"I validated Firebase Realtime Database listeners to ensure asynchronous status changes (from PLACED to DELIVERED) reflect immediately on the user's active UI without requiring a manual pull-to-refresh."*
