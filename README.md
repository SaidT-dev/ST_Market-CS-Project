# 🛒 ST Market - Supermarket Management System

**ST Market** is a robust desktop application designed to streamline retail operations. It provides a comprehensive solution for Point of Sale (POS) processing, inventory management, and sales tracking. Built with **Java Swing** and **MySQL**, this project emphasizes clean software architecture, data integrity, and security.

---

## 🚀 Key Features

### 🔹 For Cashiers (Point of Sale)
* **Real-time Transaction Processing:** Efficient interface for scanning/adding products to the cart.
* **Automated Stock Updates:** Inventory is automatically decremented upon checkout.
* **Receipt Generation:** (Optional: calculates totals, change, and taxes).

### 🔹 For Administrators (Management)
* **Inventory Control:** Add, update, and delete products. View low-stock alerts.
* **User Management:** Create accounts for cashiers and other admins with Role-Based Access Control (RBAC).
* **Sales History:** View detailed logs of past transactions using a Master-Detail view.
* **Dashboard:** Visual overview of store performance and stock levels.

### ⚙️ Technical Highlights
* **3-Tier Architecture:** Strict separation of concerns (Presentation Layer, Business Logic/Service Layer, Data Access Layer/DAO).
* **Data Integrity:** Implements **ACID-compliant JDBC Transactions** (Commit/Rollback) to ensure sales and inventory updates happen atomically.
* **Security:** Uses **PBKDF2** salt-based password hashing for secure credential storage.
* **Performance:** Utilizes **Java Multithreading (SwingWorker)** to prevent UI freezing during database operations and **Lazy Loading** for efficient data retrieval.
* **Modern UI:** Styled with **FlatLaf** for a clean, professional look.

---

## 🛠️ Tech Stack

* **Language:** Java (JDK 17+)
* **GUI Framework:** Java Swing, AWT
* **Database:** MySQL
* **Connectivity:** JDBC (Java Database Connectivity)
* **Libraries:** FlatLaf (UI Theme), JCalendar (Date picking)

---

## 📸 Screenshots

*(Replace this text with screenshots of your application to make the repo look attractive!)*

| Login Screen | Dashboard |
|:---:|:---:|
| <img src="screenshots/login.png" width="400"> | <img src="screenshots/dashboard.png" width="400"> |

| Point of Sale | Inventory Management |
|:---:|:---:|
| <img src="screenshots/pos.png" width="400"> | <img src="screenshots/inventory.png" width="400"> |

---

## 💾 Installation & Setup

### Prerequisites
1.  **Java Development Kit (JDK):** Ensure JDK 8 or higher (JDK 17 recommended) is installed.
2.  **MySQL Server:** Ensure MySQL is running locally or on a server.
3.  **IDE:** IntelliJ IDEA, Eclipse, or NetBeans.

### Step 1: Database Setup
1.  Open your MySQL Workbench or Command Line.
2.  Create a new database named `st_market`.
3.  Import the provided SQL script located in the `database/` folder (or run the SQL commands manually).

```sql
CREATE DATABASE st_market;
USE st_market;
-- Run the tables creation script here
```

### Step 2: Configure Connection
1. open `src/dao/DBConnection.java`
2. update the redentials to match your local MySQL setup:

```java
private static final String URL = "jdbc:mysql://localhost:3306/st_market";
private static final String USER = "root";
private static final String PASSWORD = "your_password";
```

### Step 3: Run the Application
1. Open the project in your IDE.
2. Locate the main class (e.g., `Main.java` or `LoginFrame.java`).
3. Run the application.

---

## 🧠 Architecture Design
This project follows the DAO (Data Access Object) Pattern:
- **View (GUI):** `JFrame` and `JPanel` classes handling user interaction.
- **Model (POJO):** Simple Java classes representing database tables (e.g., `Product.java`, `User.java`).
- **DAO Layer:** Handles raw SQL queries (e.g., `ProductDAO.java`, `UserDAO.java`).
- **Service Layer:** Handles business logic and transactions (e.g., `SalesService.java`).

---

## 🔮 Future Improvements
- [ ] **Barcode Scanner:** Add support for physical barcode scanners via USB input.
- [ ] **Reports:** Implement PDF export for daily and monthly sales reports.
- [ ] **Cloud Sync:** Add support for a cloud database (AWS RDS/Firebase) for remote management.
- [ ] **Localization:** Support multiple languages (English, French, Arabic).


---
## 👤 Author

**Said Tadjine**

* [LinkedIn Profile](https://www.linkedin.com/in/said-tadjine)
* [GitHub Profile](https://github.com/SaidT-dev)
