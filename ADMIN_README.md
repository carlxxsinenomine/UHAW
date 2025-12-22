# Admin Side - Invoice Management System

## Overview
The admin side provides comprehensive management capabilities for the Invoice Management System. It includes features for managing inventory, viewing invoices, and managing users.

## Admin Credentials
- **Username:** admin
- **Password:** admin123

## Features

### 1. Admin Login Screen
- Secure authentication for admin access
- Username and password validation
- Option to return to user side

### 2. Admin Dashboard
- Overview of key metrics:
  - Total invoices count
  - Inventory items count
  - Total revenue
- Recent activities display
- Quick access to all admin functions

### 3. Inventory Management
- **View Inventory:** Display all inventory items in a table format
- **Add Items:** Create new inventory items with:
  - Item name
  - Description
  - Value/Price
  - Category (1, 2, or 3)
- **Edit Items:** Modify existing inventory items
- **Delete Items:** Remove items from inventory
- **Auto-save:** Changes are automatically saved to `inventory.json`

### 4. Invoice Management
- **View All Invoices:** Display all generated invoices
- **Search Functionality:** Find invoices by company or customer name
- **Invoice Details:**
  - Invoice ID
  - Company name
  - Customer name
  - Date
  - Total amount
  - Status (Paid/Pending)
- **Color-coded Status:** Visual indicators for invoice status

### 5. User Management
- **View Users:** Display all system users
- **Add Users:** Create new user accounts with:
  - User ID
  - Username
  - Email
  - Role (User/Admin)
  - Status (Active/Inactive)
- **Edit Users:** Modify user information and permissions
- **Delete Users:** Remove users from the system
- **Status Tracking:** Monitor user login activities

## Navigation

### Admin Navigation Bar
- **Dashboard:** Overview and statistics
- **Inventory:** Manage inventory items
- **Invoices:** View and manage all invoices
- **Users:** User management interface
- **Logout:** Return to admin login screen

### Access Points
- From **User Side:** Click the "Admin" button in the navigation bar
- From **Admin Side:** Use the logout button to return to login

## File Structure

```
src/
├── components/
│   ├── AdminNavBarPanel.java    # Admin navigation component
│   └── NavBarPanel.java          # User navigation (with admin access)
├── screens/
│   ├── AdminLoginScreen.java          # Admin authentication
│   ├── AdminDashboardScreen.java      # Admin dashboard
│   ├── AdminInventoryScreen.java      # Inventory management
│   ├── AdminInvoicesScreen.java       # Invoice viewing
│   └── AdminUsersScreen.java          # User management
└── main/
    └── MainActivity.java               # Updated with admin screens
```

## Technical Details

### Color Scheme
- **Admin Navigation:** Blue (70, 130, 180)
- **Success Actions:** Green (100, 200, 150)
- **Primary Actions:** Blue (130, 170, 255)
- **Delete Actions:** Red (255, 100, 100)
- **Warning/Pending:** Orange (255, 140, 0)

### Data Persistence
- Inventory changes are saved to `src/items/inventory.json`
- JSON format is maintained for compatibility with user side
- Automatic backup before modifications

### Security Considerations
> **Note:** The current implementation uses simple authentication for demonstration purposes. In a production environment, implement proper security measures such as:
> - Password hashing
> - Session management
> - Role-based access control (RBAC)
> - Database integration
> - Audit logging

## Usage Guide

### Managing Inventory
1. Navigate to **Dashboard** after login
2. Click **Inventory** in the navigation bar
3. Use action buttons:
   - **Add Item:** Fill form and click Save
   - **Edit Item:** Select row, click Edit Item, modify, and Save
   - **Delete Item:** Select row and click Delete Item

### Viewing Invoices
1. Click **Invoices** in the admin navigation
2. Browse all invoices in the table
3. Use search functionality to filter results
4. Status indicators show paid/pending status

### Managing Users
1. Click **Users** in the admin navigation
2. View all system users with their details
3. Add, edit, or delete users as needed
4. Monitor user status and last login times

## Future Enhancements
- Database integration for persistent storage
- Advanced reporting and analytics
- Export functionality (PDF, Excel)
- Email notifications
- Activity logs and audit trails
- Advanced search filters
- Batch operations
- Data backup and restore

## Support
For issues or questions, contact the development team.
