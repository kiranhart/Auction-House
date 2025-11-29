# Expired Command - `/ah expired`

The expired command opens a GUI showing all your expired auction listings that need to be collected.

## Syntax

```
/ah expired
```

## Description

This command displays all auctions that have expired. You can collect items and payments from expired listings. Items that weren't sold are returned to you, and items that were sold show payment information.

## Usage

Simply type:
```
/ah expired
```

This opens the Expired Items GUI showing:
- All your expired listings
- Items that need to be collected
- Payment information for sold items
- Options to collect or delete expired listings

## Permission

- **Permission**: `auctionhouse.cmd.expired`
- **Default**: All players (if granted by server admin)

## GUI Features

The Expired Items GUI provides:

### Item Display
- Visual representation of each expired item
- Sale status (sold or unsold)
- Payment amount (if sold)
- Time since expiration

### Actions Available
- **Collect Item** - Collect unsold items back to inventory
- **Collect Payment** - Collect payment for sold items
- **Delete** - Remove expired listings (if items/payments collected)
- **Relist** - Quickly relist expired items

### Information Displayed
- Item name and quantity
- Sale status (sold/unsold)
- Final sale price (if sold)
- Buyer information (if sold)
- Time expired

## Collecting Items

### Unsold Items
- Items that didn't sell are returned to you
- Click the item to collect it
- Items go to your inventory (or storage if full)
- You can collect multiple items at once

### Sold Items
- Payment is available for collection
- Use `/ah payments` to collect all payments at once
- Payment amount is shown in the GUI
- Buyer information is displayed

## When to Use

Use this command to:
- Collect unsold items
- Check payment status
- Clean up expired listings
- Relist items that didn't sell
- Review your sales history

## Related Commands

- `/ah active` - View your active listings
- `/ah payments` - Collect all payments at once
- `/ah sell` - Relist items

## Notes

- Expired items have a collection time limit (if configured)
- Items not collected within the time limit may be deleted
- Payments are stored separately and can be collected via `/ah payments`
- You can relist expired items directly from this GUI
- Some servers may auto-collect items after expiration

