# Active Command - `/ah active`

The active command opens a GUI showing all your currently active auction listings.

## Syntax

```
/ah active
```

## Description

This command displays all auctions you have currently listed. You can view details, cancel listings (if allowed), and manage your active auctions from this interface.

## Usage

Simply type:
```
/ah active
```

This opens the Active Auctions GUI showing:
- All your currently listed items
- Current bid prices (for auction listings)
- Time remaining until expiration
- Number of bids (for auction listings)
- Quick actions (cancel, view details, etc.)

## Permission

- **Permission**: `auctionhouse.cmd.active`
- **Default**: All players (if granted by server admin)

## GUI Features

The Active Auctions GUI provides:

### Item Display
- Visual representation of each listed item
- Current price or highest bid
- Time remaining
- Buyer/bidder information

### Actions Available
- **View Details** - Click an item to see full details
- **Cancel Listing** - Cancel a listing (if no bids exist or if allowed)
- **Collect Early** - Some settings allow early collection
- **Relist** - Relist expired items (if they appear here)

### Information Displayed
- Item name and quantity
- Listing type (Auction or BIN)
- Current price/highest bid
- Time until expiration
- Number of bids (for auctions)
- Buyer information (for sold items)

## When to Use

Use this command to:
- Check the status of your listings
- See if items have received bids
- Monitor time remaining on listings
- Cancel unwanted listings
- View buyer information for sold items

## Related Commands

- `/ah expired` - View your expired listings
- `/ah sell` - Create new listings
- `/ah` - Main auction house GUI

## Notes

- Only shows your own listings
- Updates in real-time if auto-refresh is enabled
- Canceling listings with bids may have restrictions
- Some listings cannot be canceled (e.g., if they have bids and settings restrict cancellation)

