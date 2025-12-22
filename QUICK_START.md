# Quick Start Guide - Invoice Management System with Admin Panel

## Project Structure

```
UHAW/
├── src/
│   ├── components/
│   │   ├── AdminNavBarPanel.java      # Admin navigation bar
│   │   ├── NavBarPanel.java           # User navigation bar (with Admin button)
│   │   ├── NavButton.java             # Navigation button component
│   │   └── NewJPanel.java             # Custom panel component
│   ├── items/
│   │   └── inventory.json             # Inventory data (editable via admin)
│   ├── main/
│   │   ├── Inventory.java             # Inventory model
│   │   └── MainActivity.java          # Main application entry point
│   └── screens/
│       ├── UserScreen.java            # User invoice creation screen
│       ├── InvoiceScreen.java         # Invoice viewing screen
│       ├── SummaryScreen.java         # Summary screen
│       ├── AdminLoginScreen.java      # Admin authentication
│       ├── AdminDashboardScreen.java  # Admin dashboard
│       ├── AdminInventoryScreen.java  # Inventory management
│       ├── AdminInvoicesScreen.java   # Invoice management
│       └── AdminUsersScreen.java      # User management
├── out/                               # Compiled classes output
├── README.md                          # User side documentation
└── ADMIN_README.md                    # Admin side documentation
```

## How to Run

### Option 1: Using Command Line

1. **Compile the project:**
   ```powershell
   cd "c:\Users\oguhn\OneDrive\Desktop\BSCS\BSCS 2A\OOP\UHAW"
   javac -d out -sourcepath src src/main/MainActivity.java
   ```

2. **Run the application:**
   ```powershell
   java -cp out main.MainActivity
   ```

### Option 2: Using VS Code

1. Open the project folder in VS Code
2. Press `F5` or use the Run button
3. Select "Java" as the debug configuration

### Option 3: Using IDE (IntelliJ IDEA, Eclipse, NetBeans)

1. Import the project as a Java project
2. Set `src` as the source folder
3. Set `out` as the output folder
4. Run `MainActivity.java`

## Application Features

### User Side (Default View)

**Navigation:** Home | Invoices | Summary | Admin

1. **Home Screen**
   - Generate new invoices for Peter Loves Carl Co.
   - Input customer information (Name, Contact, Address)
   - Select items with categories (1, 2, 3)
   - Add items to invoice

2. **Invoices Screen**
   - View summary of all invoices
   - Display company names and items

3. **Summary Screen**
   - View application summary

### Admin Side (Requires Login)

**How to Access:**
1. Click the "Admin" button in the user navigation bar
2. Enter credentials:
   - Username: `admin`
   - Password: `admin123`

**Admin Navigation:** Dashboard | Inventory | Invoices | Users | Logout

1. **Dashboard**
   - View total invoices count
   - View inventory items count
   - View total revenue
   - See recent activities

2. **Inventory Management**
   - View all inventory items
   - Add new items (Name, Description, Value, Category)
   - Edit existing items
   - Delete items
   - Changes auto-save to `inventory.json`

3. **Invoice Management**
   - View all invoices in table format
   - Search invoices
   - See invoice status (Paid/Pending)
   - View customer details

4. **User Management**
   - View all users
   - Add new users
   - Edit user details
   - Delete users
   - Manage user roles (User/Admin)
   - Track user status (Active/Inactive)

## Key Features

### User Side
✓ Invoice generation
✓ Customer information management
✓ Item selection with category filters
✓ Real-time value calculations
✓ Clean and intuitive UI

### Admin Side
✓ Secure authentication
✓ Comprehensive dashboard
✓ Full inventory CRUD operations
✓ Invoice overview and management
✓ User account management
✓ Data persistence (JSON)
✓ Modern admin interface

## Default Admin Credentials

**Username:** admin  
**Password:** admin123

> ⚠️ **Security Note:** Change these credentials in production!

## Color Scheme

### User Side
- Primary: Blue (130, 170, 255)
- Background: White
- Navigation: Blue gradient

### Admin Side
- Primary: Blue (70, 130, 180)
- Success: Green (100, 200, 150)
- Warning: Orange (255, 140, 0)
- Danger: Red (255, 100, 100)

## Testing the Application

### Test Scenario 1: User Invoice Creation
1. Launch the application
2. Fill in customer information
3. Select items from the dropdown
4. Enter quantities
5. Click "Add to Invoice"
6. Navigate to "Invoices" to see the result

### Test Scenario 2: Admin Inventory Management
1. Click "Admin" button
2. Login with admin credentials
3. Go to "Inventory"
4. Click "Add Item"
5. Fill in the form and save
6. Check that the item appears in the table
7. Verify the change in `inventory.json`

### Test Scenario 3: Admin Dashboard
1. Login to admin panel
2. View the dashboard statistics
3. Navigate through different admin screens
4. Click "Logout" to return to login

## Troubleshooting

### Issue: Application doesn't start
- Ensure JDK is installed (Java 8 or higher)
- Check that all files are in correct directories
- Verify `inventory.json` exists in `src/items/`

### Issue: Cannot login to admin panel
- Verify credentials: `admin` / `admin123`
- Check case sensitivity
- Ensure AdminLoginScreen.java compiled successfully

### Issue: Inventory changes not saving
- Check file permissions for `src/items/inventory.json`
- Ensure the path is correct
- Verify JSON format is valid

### Issue: UI looks wrong
- Check screen resolution (minimum 1200x600)
- Verify Swing libraries are available
- Try resizing the window

## Next Steps

1. **Enhance Security:**
   - Implement password hashing
   - Add session management
   - Create user registration

2. **Add Database:**
   - Replace JSON with database
   - Add invoice persistence
   - Store user credentials securely

3. **Improve Features:**
   - Add invoice PDF export
   - Implement email notifications
   - Add advanced reporting
   - Create backup/restore functionality

4. **UI Improvements:**
   - Add date pickers
   - Implement auto-complete
   - Add data validation
   - Improve error messages

## Support

For questions or issues:
1. Check the documentation in `ADMIN_README.md`
2. Review the code comments
3. Test with sample data
4. Contact the development team

---

**Developed for:** BSCS 2A - OOP Course  
**Version:** 1.0  
**Date:** December 2025
