# ST Market — Supermarket POS & Inventory Manager

Desktop application for running a small supermarket: point of sale, inventory, sales history and employee accounts. Built with Java Swing, backed by MySQL.

## Features

**Point of sale**
* Product search by name or ID, add to cart by double click or button.
* Editable quantities, capped by the available stock.
* Total, cash received and change calculation; the sale is refused if the cash is lower than the total.
* Checkout runs in a single transaction: stock decrement, sale insert and ticket insert are committed or rolled back together.

**Inventory**
* Product list with ID, name, unit price, quantity and stock state (`Faible` when quantity <= alert threshold).
* Add a product (reference, name, price, initial quantity, alert threshold).
* Restock an existing product, delete a product with confirmation.

**Sales history**
* List of past sales: ID, date, cashier, total.

**Employees**
* List, create, update and delete employee accounts.
* Usernames are generated automatically from first and family name (`j.smith`, `j.smith1`, ...).
* Passwords are stored as PBKDF2 hashes with a per-user salt; on update an empty password keeps the old one.
* Deleting an employee also removes their sales.

**Access control**
* Roles: `1` Manager, `2` Cashier, `3` Stock clerk. The sidebar enables only the panels the role may use.
* Roles are inserted by `database/schema.sql`, and again by the application on first run if the `ROLE` table is empty.
* On first run (empty `EMPLOYE` table) an administrator account is created automatically and its credentials are shown in a dialog.

## Tech stack

* Java 17 or newer (compiled and verified with JDK 21)
* Swing / AWT for the interface, FlatLaf for the theme
* MySQL, accessed through JDBC
* No build tool: sources are compiled directly with `javac`

## Install

1. Install a JDK (17+) and a MySQL server.
### Database setup

1. Make sure MySQL 8.x is running on `localhost:3306`.

2. Create the database, tables and roles by running the schema script from the project root:

```bash
mysql -u root -p < database/schema.sql
```

The script creates `supermarket_db` (InnoDB, utf8mb4), defines all tables (`ROLE`, `EMPLOYE`, `PRODUCT`, `STOCK`, `SALE`, `SALE_DETAIL`, `TICKET`) with foreign keys, and inserts the required roles: `1 Manager`, `2 Caissier`, `3 Magasinier`. It uses `IF NOT EXISTS` and `INSERT IGNORE` so it can be run safely multiple times.

3. Configure database credentials for the app:

```bash
cp src/db_config.example.properties src/db_config.properties
```

Edit `src/db_config.properties` if your MySQL credentials differ:

```properties
db.url=jdbc:mysql://localhost:3306/supermarket_db
db.username=root
db.password=admin
```

If `src/db_config.properties` is missing, the application falls back to these default values. This file is ignored by Git and should not be committed.

#### Optional: Docker

If you don't have MySQL installed locally, you can start it with Docker:

```bash
docker run -d --name stmarket-mysql -e MYSQL_ROOT_PASSWORD=admin -p 3306:3306 mysql:8.4
# Give MySQL a few seconds to start, then create the schema
docker exec -i stmarket-mysql mysql -uroot -padmin < database/schema.sql
```

The connection settings above will work with this container.

5. Compile:

```bash
javac -cp "lib/flatlaf.jar:lib/mysql-connector-j.jar" -d out $(find src -name "*.java")
```

On Windows, replace `:` with `;` in the classpath. Compiling from an IDE works the same way.

## Usage

```bash
java -cp "out:src:lib/flatlaf.jar:lib/mysql-connector-j.jar" com.main.Main
```

`src` stays on the classpath so the icons in `src/ressources/` are found.

On the first launch, with an empty `EMPLOYE` table, the application creates the default administrator and shows its username and password. The password is `admin`; the username is generated (`a.system` for the default `Admin System` account).

Log in, then use the sidebar to switch between **Inventaire**, **Ventes** and **Employés**, depending on your role.

## Project structure

```
database/
└── schema.sql          SQL script: database, tables, roles
src/
├── com/
│   ├── auth/          PasswordUtil (PBKDF2 hashing)
│   ├── dao/           ProductDAO, SaleDAO, EmployeDAO, RoleDAO
│   ├── db/            DBConnectionManager
│   ├── exception/     ProductNotFound, ProductAlreadyExists, InsufficientStock
│   ├── GUI/           LoginView, DashboardView, InventoryPanel, SalesPanel,
│   │                  SalesHistoryPanel, EmployesPanel, form dialogs, StyleUtils
│   ├── main/          Main (entry point)
│   ├── model/         Product, Stock, Sale, SaleDetail, Ticket, Employe, Role
│   └── service/       ProductService, SaleService, EmployeService
├── ressources/        icons and logos used by the interface
├── META-INF/          MANIFEST.MF
├── db_config.example.properties   connection settings template
└── test/              empty test skeletons (no test framework wired up)
```

Layers: `GUI` -> `service` -> `dao` -> MySQL. The `service` layer owns transactions and validation, the `dao` layer owns SQL.

## Author

Said Tadjine
