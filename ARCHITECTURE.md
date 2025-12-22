# Application Architecture

## System Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    Invoice Management System                     │
│                         (MainActivity)                           │
└─────────────────────────────────────────────────────────────────┘
                                │
                    ┌───────────┴───────────┐
                    │                       │
            ┌───────▼────────┐      ┌──────▼────────┐
            │   USER SIDE    │      │  ADMIN SIDE   │
            │                │      │               │
            │  (CardLayout)  │      │ (CardLayout)  │
            └───────┬────────┘      └──────┬────────┘
                    │                      │
        ┌───────────┼──────────┐          │
        │           │          │          │
    ┌───▼───┐  ┌───▼────┐  ┌──▼───┐     │
    │ User  │  │Invoice │  │Summary│     │
    │Screen │  │Screen  │  │Screen │     │
    └───────┘  └────────┘  └───────┘     │
                                          │
                    ┌─────────────────────┼─────────────────────┐
                    │                     │                     │
            ┌───────▼───────┐     ┌──────▼─────┐      ┌───────▼──────┐
            │ Admin Login   │     │  Admin     │      │    Admin     │
            │   Screen      │     │ Dashboard  │      │  Inventory   │
            └───────────────┘     └────────────┘      └──────────────┘
                                          │
                            ┌─────────────┼─────────────┐
                            │                           │
                    ┌───────▼──────┐            ┌──────▼───────┐
                    │    Admin     │            │    Admin     │
                    │   Invoices   │            │    Users     │
                    └──────────────┘            └──────────────┘
```

## Navigation Flow

### User Side Flow
```
┌─────────────┐
│   Launch    │
│   App       │
└──────┬──────┘
       │
       ▼
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│    Home     │────▶│  Invoices   │────▶│   Summary   │
│   Screen    │◀────│   Screen    │◀────│   Screen    │
└──────┬──────┘     └─────────────┘     └─────────────┘
       │
       │ Click "Admin"
       │
       ▼
┌─────────────┐
│  Admin      │
│  Login      │
└─────────────┘
```

### Admin Side Flow
```
┌─────────────┐
│  Admin      │
│  Login      │
└──────┬──────┘
       │ Authenticate
       │
       ▼
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Dashboard  │────▶│ Inventory   │────▶│  Invoices   │
│             │◀────│ Management  │◀────│   View      │
└──────┬──────┘     └─────────────┘     └─────────────┘
       │                                         │
       │                                         │
       ▼                                         ▼
┌─────────────┐                         ┌─────────────┐
│    User     │                         │   Logout    │
│ Management  │                         │  → Login    │
└─────────────┘                         └─────────────┘
```

## Component Hierarchy

### User Side Components
```
UserScreen
├── NavBarPanel
│   ├── NavButton (Home)
│   ├── NavButton (Invoices)
│   ├── NavButton (Summary)
│   └── NavButton (Admin)
├── Customer Info Panel
│   ├── Name Field
│   ├── Contact Field
│   └── Address Field
├── Invoice Table Panel
│   ├── Category Toggles
│   ├── Table Headers
│   └── Item Rows (7)
│       ├── Item Dropdown
│       ├── Quantity Spinner
│       ├── Value Label
│       └── Total Label
└── Action Buttons
    ├── Add to Invoice
    └── Add to Summary
```

### Admin Side Components
```
AdminDashboardScreen
├── AdminNavBarPanel
│   ├── Dashboard Button
│   ├── Inventory Button
│   ├── Invoices Button
│   ├── Users Button
│   └── Logout Button
├── Statistics Panel
│   ├── Total Invoices Card
│   ├── Inventory Items Card
│   └── Total Revenue Card
└── Recent Activities Panel

AdminInventoryScreen
├── AdminNavBarPanel
├── Action Buttons
│   ├── Add Item
│   ├── Edit Item
│   └── Delete Item
└── Inventory Table
    └── JTable with scrolling

AdminInvoicesScreen
├── AdminNavBarPanel
├── Search Panel
│   ├── Search Field
│   └── Search Button
└── Invoice Table
    └── Color-coded status

AdminUsersScreen
├── AdminNavBarPanel
├── Action Buttons
│   ├── Add User
│   ├── Edit User
│   └── Delete User
└── User Table
    └── Role & status display
```

## Data Flow

### Inventory Management
```
inventory.json
     │
     │ Read on startup
     ▼
┌─────────────────┐
│  User Screen    │ ─┐
│  (Item Dropdown)│  │
└─────────────────┘  │
                     │ Load items
                     │
┌─────────────────┐  │
│ Admin Inventory │◀─┘
│     Screen      │
└────────┬────────┘
         │
         │ Add/Edit/Delete
         │
         ▼
   inventory.json
   (Auto-save)
```

### Authentication Flow
```
┌──────────────┐
│ Click "Admin"│
└──────┬───────┘
       │
       ▼
┌──────────────────┐
│ AdminLoginScreen │
└──────┬───────────┘
       │
       │ Enter credentials
       │
       ▼
┌──────────────────┐     ┌─────────┐
│ Validate:        │────▶│ Success │──▶ Dashboard
│ admin/admin123   │     └─────────┘
└──────┬───────────┘
       │
       │ Failed
       ▼
┌──────────────────┐
│ Show error msg   │
└──────────────────┘
```

## File Operations

### Reading Inventory
```
Application Startup
        │
        ▼
Load inventory.json
        │
        ├──▶ Parse JSON manually
        │    (no external libraries)
        │
        ├──▶ Create Map<String, Double> (values)
        │
        ├──▶ Create Map<String, String> (categories)
        │
        └──▶ Populate UI components
```

### Saving Inventory (Admin)
```
User Action (Add/Edit/Delete)
        │
        ▼
Update inventoryItems List
        │
        ▼
Open inventory.json for writing
        │
        ├──▶ Write JSON array start "["
        │
        ├──▶ For each item:
        │    Write formatted JSON object
        │
        ├──▶ Write JSON array end "]"
        │
        └──▶ Close file
             │
             ▼
        Show success message
```

## Screen Transition States

### CardLayout Screen IDs
```
USER_SCREEN              → "USER_SCREEN"
INVOICE_SCREEN           → "INVOICE_SCREEN"
SUMMARY_SCREEN           → "SUMMARY_SCREEN"
ADMIN_LOGIN_SCREEN       → "ADMIN_LOGIN_SCREEN"
ADMIN_DASHBOARD_SCREEN   → "ADMIN_DASHBOARD_SCREEN"
ADMIN_INVENTORY_SCREEN   → "ADMIN_INVENTORY_SCREEN"
ADMIN_INVOICES_SCREEN    → "ADMIN_INVOICES_SCREEN"
ADMIN_USERS_SCREEN       → "ADMIN_USERS_SCREEN"
```

### Screen Switching Mechanism
```java
MainActivity.getInstance().showScreen(SCREEN_ID);
    │
    ▼
cardLayout.show(cardPanel, SCREEN_ID);
    │
    ▼
Display requested screen
```

## Security Layers

```
┌─────────────────────────────────────┐
│        User Interface Layer         │
│  (Public access - no restrictions)  │
└────────────────┬────────────────────┘
                 │
                 │ Click "Admin"
                 │
                 ▼
┌─────────────────────────────────────┐
│     Authentication Layer            │
│  (Username/Password verification)   │
└────────────────┬────────────────────┘
                 │
                 │ Credentials validated
                 │
                 ▼
┌─────────────────────────────────────┐
│    Admin Interface Layer            │
│  (Protected - Admin access only)    │
│  - Inventory Management             │
│  - Invoice Management               │
│  - User Management                  │
└─────────────────────────────────────┘
```

## Technology Stack

```
┌─────────────────────────────────────┐
│          Java Swing GUI             │
│  - JFrame, JPanel, JButton          │
│  - JTable, JScrollPane              │
│  - CardLayout, BorderLayout         │
└────────────────┬────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│         Core Java                   │
│  - Collections (List, Map, Set)     │
│  - File I/O                         │
│  - Event Handling                   │
└────────────────┬────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│       Data Persistence              │
│  - JSON file (inventory.json)       │
│  - Manual JSON parsing/writing      │
└─────────────────────────────────────┘
```

## Color Scheme Reference

### User Side Palette
```
Primary Navigation:   RGB(130, 170, 255)
Secondary Background: RGB(200, 220, 255)
Border Color:         RGB(180, 210, 255)
Header Background:    RGB(200, 200, 200)
White Background:     RGB(255, 255, 255)
```

### Admin Side Palette
```
Admin Navigation:     RGB(70, 130, 180)
Success Green:        RGB(100, 200, 150)
Action Blue:          RGB(130, 170, 255)
Delete Red:           RGB(255, 100, 100)
Pending Orange:       RGB(255, 140, 0)
Active Status:        RGB(0, 150, 0)
```

## Responsive Design

```
Minimum Window Size: 1200 x 600 pixels
Default Window Size: 1200 x 720 pixels
Layout: Fluid (adapts to window resize)
Components: Fixed heights, flexible widths
```

---

This architecture provides a clear separation between user and admin functionality while maintaining a cohesive application structure.
