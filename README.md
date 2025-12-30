# UHAW: Unified Hardware for Automated Wholesale/Retail
UHAW: Unified Hardware for Automated Wholesale/Retail, is a desktop application designed to streamline the sales and receipt generation process for a hardware store. It allows store personnel to quickly input customer purchases and generate a digital receipt (e-receipt).

## System Diagram
``` mermaid
    ---
    config:
      layout: elk
    ---
    classDiagram
        class MainActivity {
            -CardLayout cardLayout
            -JPanel cardPanel
            -UserScreen userScreen
            -AdminInventoryScreen adminInventoryScreen
            -InvoiceScreen invoiceScreen
            +showScreen(String)
            +refreshAllScreens()
            +getInstance() MainActivity
        }
    
        class AppConstants {
            <<utility>>
            +PRIMARY_BLUE Color
            +BG_LIGHT_GRAY Color
            +TEXT_DARK Color
            +ACCENT_GREEN Color
            +FONT_TITLE_LARGE Font
            +PADDING_LARGE int
        }
        namespace UserScreens {
            class MainMenuScreen {
                +MainMenuScreen()
                -createStyledMenuButton()
                -showAboutDialog()
            }
    
            class UserScreen {
                -InventoryManager inventoryManager
                -Map~String,Integer~ shoppingCart
                -String currentSearchText
                +refreshData()
                -generateInvoice()
                -updateInventoryFile()
            }
    
            class InvoiceScreen {
                -DefaultTableModel tableModel
                -String currentSearchText
                +refreshData()
                -loadPurchasesFromFolder()
                -viewSelectedPurchase()
            }
        }
    
        namespace AdminScreens {
            class AdminLoginScreen {
                +AdminLoginScreen()
                -authenticateAdmin()
            }
    
            class AdminDashboardScreen {
                -JLabel invoicesValueLabel
                -JLabel revenueValueLabel
                +refreshDashboardStats()
                +refreshActivityList()
            }
    
            class AdminInventoryScreen {
                -InventoryManager inventoryManager
                -String currentSearchText
                +refreshData()
                -showAddItemDialog()
                -deleteSelectedItem()
            }
    
            class AdminInvoicesScreen {
                -DefaultTableModel tableModel
                -String currentSearchText
                -loadInvoicesFromFolder()
                -deleteSelectedInvoice()
            }
        }
        class NavBarPanel {
            -String activeScreen
            -Consumer~String~ searchListener
            +setSearchListener()
            +resetSearch()
        }
    
        class AdminNavBarPanel {
            -String activeScreen
            -Consumer~String~ searchListener
            +setSearchListener()
            +resetSearch()
        }
        class InventoryItem {
            <<abstract>>
            -String name
            -double price
            -String category
            -int quantity
            +getName() String
            +getPrice() double
            +getQuantity() int
        }
    
        class Tools {
            -String powerSource
            +getPowerSource() String
            +getCategoryName() String
        }
    
        class BuildingMaterials {
            -String material
            +getMaterial() String
            +getCategoryName() String
        }
    
        class PaintAndSupplies {
            -String color
            +getColor() String
            +getCategoryName() String
        }
        class InventoryManager {
            -List~InventoryItem~ inventoryItems
            +loadInventory()
            +saveInventory()
            +getAllItems() List
            +addItem(InventoryItem)
            +deleteItem(String)
            +updateItem(String, InventoryItem)
        }
    
        class InvoiceItem {
            -String description
            -int qty
            -double unitPrice
            -double amount
            +getDescription() String
            +getQty() int
        }
    
        class ItemRowData {
            -String itemName
            -JSpinner spinner
            -JLabel totalLabel
            +getSpinner() JSpinner
        }
    
        class RoundedBorder {
            -int radius
            -Color color
            +paintBorder()
            +getBorderInsets() Insets
        }
        MainActivity --> MainMenuScreen
        MainActivity --> UserScreen
        MainActivity --> InvoiceScreen
        MainActivity --> AdminLoginScreen
        MainActivity --> AdminDashboardScreen
        MainActivity --> AdminInventoryScreen
        MainActivity --> AdminInvoicesScreen
        UserScreen --> NavBarPanel
        InvoiceScreen --> NavBarPanel
        AdminInventoryScreen --> AdminNavBarPanel
        AdminInvoicesScreen --> AdminNavBarPanel
        AdminDashboardScreen --> AdminNavBarPanel
        UserScreen --> InventoryManager
        AdminInventoryScreen --> InventoryManager
        InventoryManager --> InventoryItem
        InventoryItem <|-- Tools
        InventoryItem <|-- BuildingMaterials
        InventoryItem <|-- PaintAndSupplies
        UserScreen ..> InvoiceItem
        UserScreen ..> ItemRowData
        UserScreen ..> RoundedBorder
        UserScreen ..> AppConstants
        InvoiceScreen ..> AppConstants
        AdminInventoryScreen ..> AppConstants
        note for MainActivity "SINGLETON PATTERN\nManages all screens\nusing CardLayout"
        
        note for InventoryItem "INHERITANCE HIERARCHY\nPolymorphism for\ndifferent product types"
        
        note for InventoryManager "FACTORY PATTERN\nCreates specific\nitem types from JSON"
```
## What the system does:
The system facilitates the transaction process by allowing users (store clerks/cashiers) to select items from an existing inventory file, input quantities, calculate the total cost, process payment details, and instantly generate a formatted e-receipt.

## Features
- Inventory File Loading: The system must be able to load product data (Item Name and Unit Price) from an external file (e.g., CSV, text file, or JSON).
- Sales Transaction Entry: A user-friendly interface to search for/select items, input quantity, and add them to the current transaction list.
- Automatic Calculation: Real-time calculation of sub-total, tax (if applicable), and grand total.
- E-Receipt Generation: Generate a structured, digital receipt (e-receipt) that can be saved, displayed, or printed.
- Data Persistence: The system will store transaction history (though the core inventory remains in the file).

## Running the Program
To run the program you only need to run the UHAW.jar file 

## Members:
- Bañares, Peter Andrew
- Margarata, Sean Eric
- Muñoz, Carl Johannes
- Santos, Gebhel Anselm
