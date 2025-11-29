# Getting Started with Auction House

This guide will help you get started with Auction House, from installation to your first auction.

## Installation

1. **Download the Plugin**
   - Download Auction House from Spigot, Songoda, or Polymart
   - Ensure you have the latest version for your Minecraft server version

2. **Server Requirements**
   - Minecraft 1.8+ (1.13+ recommended for full feature support)
   - Spigot, Paper, or compatible fork
   - Java 16+ (for plugin compilation, server may need different version)

3. **Installation Steps**
   - Place the `AuctionHouse.jar` file in your server's `plugins` folder
   - Start or restart your server
   - The plugin will generate configuration files in `plugins/AuctionHouse/`

4. **Dependencies (Optional)**
   Auction House supports integration with:
   - **Vault** - For economy integration
   - **PlayerPoints** - For PlayerPoints currency
   - **PlaceholderAPI** - For placeholders
   - **MMOItems** - For MMOItems support
   - **UltraEconomy** - For UltraEconomy integration
   - **CMI** - For CMI integration
   - **Essentials** - For Essentials integration
   - **ChestShop** - For ChestShop integration
   - **EcoEnchants** - For EcoEnchants support
   - **EcoBits** - For EcoBits support
   - **Funds** - For Funds integration
   - **CoinsEngine** - For CoinsEngine support

## First Steps

### 1. Basic Configuration

After installation, the plugin will create a `config.yml` file. You can configure:
- Database settings (SQLite or MySQL)
- Default listing times
- Price limits
- GUI settings
- And much more

### 2. Permissions Setup

Set up permissions for your players. Basic permissions include:
- `auctionhouse.cmd` - Base command permission
- `auctionhouse.cmd.sell` - Allow selling items
- `auctionhouse.cmd.*` - All commands (use with caution)

See [Permissions](permissions.md) for a complete list.

### 3. Opening the Auction House

Players can open the auction house GUI using:
```
/ah
```

Or use the alias configured in your settings.

### 4. Your First Sale

To sell an item:
1. Hold the item you want to sell in your hand
2. Type `/ah sell <price>` (e.g., `/ah sell 1000`)
3. Confirm the listing in the GUI
4. Your item is now listed!

### 5. Your First Purchase

To buy an item:
1. Open the auction house with `/ah`
2. Browse available listings
3. Click on an item to view details
4. Click "Buy" or "Bid" depending on the listing type
5. Confirm the purchase

## Basic Commands

Here are the essential commands to get started:

- `/ah` - Open the auction house GUI
- `/ah sell <price>` - Sell the item in your hand
- `/ah search <keywords>` - Search for items
- `/ah active` - View your active listings
- `/ah expired` - View your expired listings

For detailed command information, see [Commands](commands.md).

## Understanding Listing Types

Auction House supports two main listing types:

### Buy It Now (BIN)
- Items are sold at a fixed price
- First player to buy gets the item
- No bidding required
- Instant purchase

### Auction (Bidding)
- Players bid on items
- Highest bidder wins when auction ends
- Bids can be placed until expiration
- Buy It Now option may be available

## Next Steps

Now that you have the basics, explore:

- [Commands](commands.md) - Learn all available commands
- [GUIs](guis.md) - Understand the graphical interfaces
- [Features](features.md) - Discover advanced features
- [Permissions](permissions.md) - Configure permissions properly

## Troubleshooting

### Plugin Not Loading
- Ensure you're using a compatible server version
- Check server logs for error messages
- Verify the JAR file is not corrupted

### Economy Not Working
- Install Vault and an economy plugin
- Check that Vault is properly configured
- Verify economy plugin is loaded before Auction House

### Database Issues
- Check database connection settings in config.yml
- Ensure MySQL credentials are correct (if using MySQL)
- Verify database permissions

### Players Can't Use Commands
- Check permission setup
- Verify players have required permissions
- Check if command aliases are configured correctly

## Configuration Tips

1. **Start Simple** - Begin with default settings and adjust as needed
2. **Test Permissions** - Test permissions in a test environment first
3. **Backup First** - Always backup your database before major changes
4. **Read the Config** - Many features can be enabled/disabled in config.yml

## Support

If you encounter issues:
- Check the [Features](features.md) documentation
- Review server logs for error messages
- Join the Discord for community support
- Check the Spigot resource page for updates

