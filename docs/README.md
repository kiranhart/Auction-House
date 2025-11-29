# Auction House Plugin Documentation

Welcome to the Auction House plugin documentation! Auction House is a premium auction solution for your Minecraft server, providing a comprehensive GUI-based auction system with advanced features.

## What is Auction House?

Auction House is a feature-packed auction plugin that allows players to buy and sell items through an intuitive GUI interface. It supports both traditional auctions with bidding and Buy It Now (BIN) listings, along with many advanced features like item bundles, requests, shopping carts, and more.

## Key Features

-   **GUI-Based Interface** - Easy-to-use graphical interface for all auction operations
-   **Bidding System** - Traditional auction-style listings with bidding
-   **Buy It Now** - Instant purchase listings without bidding
-   **Item Bundles** - Sell multiple items together as a bundle
-   **Item Requests** - Players can request specific items they want to buy
-   **Shopping Cart** - Add multiple items to cart before purchasing
-   **Advanced Filtering** - Filter auctions by category, price, seller, and more
-   **Statistics Tracking** - Track sales, purchases, and earnings
-   **Payment Collection** - Collect payments from sold items
-   **Transaction History** - View complete transaction history
-   **Admin Tools** - Comprehensive admin tools for managing auctions
-   **Price Limits** - Set minimum and maximum prices for items
-   **Multiple Currencies** - Support for various economy plugins
-   **Multi-World Support** - Different auction houses per world

## Getting Started

If you're new to Auction House, start here:

-   [Getting Started](getting-started.md) - Installation and basic setup guide

## Documentation Sections

### [Commands](commands.md)

Learn about all available commands in Auction House.

-   [Main Command](commands/main-command.md) - `/ah` - Open the auction house GUI
-   [Sell Command](commands/sell.md) - `/ah sell` - Sell items on the auction house
-   [Search Command](commands/search.md) - `/ah search` - Search for specific items
-   [Active Command](commands/active.md) - `/ah active` - View your active listings
-   [Expired Command](commands/expired.md) - `/ah expired` - View expired listings
-   [Bids Command](commands/bids.md) - `/ah bids` - View your active bids
-   [Cart Command](commands/cart.md) - `/ah cart` - Open your shopping cart
-   [Request Command](commands/request.md) - `/ah request` - Request items you want to buy
-   [Payments Command](commands/payments.md) - `/ah payments` - Collect payments from sales
-   [Transactions Command](commands/transactions.md) - `/ah transactions` - View transaction history
-   [Stats Command](commands/stats.md) - `/ah stats` - View statistics
-   [Admin Command](commands/admin.md) - `/ah admin` - Admin commands
-   [Other Commands](commands/other-commands.md) - Ban, filter, reload, settings, and more

### [Permissions](permissions.md)

Complete reference of all permissions in Auction House, including command permissions, feature permissions, and listing limits.

### [GUIs](guis.md)

Detailed documentation of all graphical interfaces in Auction House.

-   [Main Auction House GUI](guis/main-auction-house.md) - The main auction browsing interface
-   [Selling GUIs](guis/selling.md) - GUIs for listing items for sale
-   [Bidding GUIs](guis/bidding.md) - GUIs for placing and managing bids
-   [Cart GUI](guis/cart.md) - Shopping cart interface
-   [Expired Items GUI](guis/expired-items.md) - View and collect expired items
-   [Active Auctions GUI](guis/active-auctions.md) - View your active listings
-   [Payments GUI](guis/payments.md) - Payment collection interface
-   [Transactions GUI](guis/transactions.md) - Transaction history interface
-   [Requests GUI](guis/requests.md) - Item request interface
-   [Statistics GUIs](guis/statistics.md) - Statistics and leaderboards
-   [Admin GUIs](guis/admin.md) - Administrative interfaces
-   [Filter GUIs](guis/filters.md) - Filter configuration interfaces
-   [Confirmation GUIs](guis/confirmations.md) - Confirmation dialogs

### [Features](features.md)

In-depth explanations of major features:

-   Bidding System
-   Buy It Now (BIN) System
-   Item Bundles
-   Item Requests
-   Shopping Cart
-   Price Limits
-   Filtering System
-   Statistics Tracking
-   Payment Collection
-   Transaction History
-   Admin Features

## Quick Examples

### Opening the Auction House

```
/ah
```

This opens the main auction house GUI where you can browse all available listings.

### Selling an Item

```
/ah sell 1000
```

This sells the item in your hand for 1000 currency units.

### Searching for Items

```
/ah search diamond
```

This opens the auction house GUI filtered to show only items containing "diamond" in their name.

### Viewing Your Active Listings

```
/ah active
```

This opens a GUI showing all your currently active auction listings.

## Version Compatibility

Auction House supports:

-   **Minecraft**: 1.8+ (1.8-1.15 support is limited, use latest version for best experience)
-   **Server Types**: Spigot, Paper, and compatible forks

## Support

For issues, questions, or contributions, please refer to:

-   Discord: https://discord.tweetzy.ca/
-   Spigot: https://www.spigotmc.org/resources/auction-house-the-ultimate-auction-house.60325/

## License

Auction House is licensed under the GNU General Public License v3.0.
