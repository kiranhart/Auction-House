# Main Command - `/ah`

The main command opens the Auction House GUI and can also be used to search for items.

## Syntax

```
/ah [search keywords]
```

## Description

The main `/ah` command serves two purposes:
1. Opens the main Auction House GUI where you can browse all available listings
2. Allows you to search for items directly by providing keywords

## Usage

### Opening the Auction House

Simply type:
```
/ah
```

This opens the main auction house GUI where you can:
- Browse all available listings
- Filter items by category, price, seller, etc.
- Sort listings by various criteria
- View item details
- Purchase or bid on items

### Searching for Items

You can search for items directly by providing keywords:
```
/ah diamond
/ah enchanted sword
/ah oak wood
```

This opens the auction house GUI filtered to show only items matching your search keywords.

## Permission

- **Permission**: `auctionhouse.cmd`
- **Default**: All players (if granted by server admin)

## Examples

```
/ah
```
Opens the main auction house GUI.

```
/ah diamond sword
```
Opens the auction house GUI showing only items containing "diamond sword" in their name or description.

```
/ah enchanted
```
Searches for all enchanted items.

## GUI Features

When the GUI opens, you'll see:
- **Item Listings** - All available auctions displayed as items
- **Filter Button** - Access to filtering options
- **Sort Options** - Sort by price, time, seller, etc.
- **Navigation** - Previous/Next page buttons for pagination
- **Your Listings** - Quick access to your active/expired listings
- **Cart** - Access to your shopping cart
- **Search** - Search functionality within the GUI

## Related Commands

- `/ah search <keywords>` - Alternative search command
- `/ah active` - View your active listings
- `/ah expired` - View your expired listings
- `/ah cart` - Open your shopping cart

## Notes

- The command alias can be configured in the plugin settings
- Search is case-insensitive
- Search matches item names and descriptions
- The GUI auto-refreshes if enabled in settings

