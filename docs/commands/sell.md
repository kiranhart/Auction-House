# Sell Command - `/ah sell`

The sell command allows you to list items for sale on the auction house. It supports extensive flags and options for advanced listing configurations.

## Syntax

```
/ah sell [price] [starting bid] [bid increment] [flags...]
```

## Basic Usage

### Simple Sale (Buy It Now)

```
/ah sell 1000
```

Sells the item in your hand for 1000 currency units as a Buy It Now listing.

### Auction with Bidding

```
/ah sell 5000 1000 100
```

Creates an auction listing with:
- Buy It Now price: 5000
- Starting bid: 1000
- Bid increment: 100

## Command Flags

The sell command supports many flags for advanced options:

### Bundle Flag
Sell multiple items together as a bundle:
```
/ah sell 5000 bundle
```

**Permission**: No special permission required (if bundles are enabled)

### Partial Buy Flag
Allow players to buy partial amounts from a stack:
```
/ah sell 1000 partial
```

### Single Item Flag
Sell only one item from a stack:
```
/ah sell 1000 single
```

### Stack Price Flag
Set the price for the entire stack (not per item):
```
/ah sell 1000 stack
```

**Permission**: `auctionhouse.cmdflag.stack`

### Infinite Listing Flag
Create a listing that never expires (admin only):
```
/ah sell 1000 infinite
```

**Permission**: `auctionhouse.admin` or OP

### Server Auction Flag
Create a server-owned auction (admin only):
```
/ah sell 1000 server
```

**Permission**: `auctionhouse.admin` or OP

### Time Flag
Set a custom expiration time:
```
/ah sell 1000 time 2 days
/ah sell 1000 time 1 week
/ah sell 1000 time 3 hours
```

Supported time units: `second`, `minute`, `hour`, `day`, `week`, `month`

**Note**: Must be enabled in settings and within max time limits

### Currency Flag
Specify a different currency (if multiple currencies are enabled):
```
/ah sell 1000 currency:PlayerPoints
```

## Permission

- **Permission**: `auctionhouse.cmd.sell`
- **Default**: All players (if granted by server admin)

## Advanced Examples

### Bundle with Custom Time
```
/ah sell 5000 bundle time 3 days
```

### Auction with All Options
```
/ah sell 10000 2000 500 bundle partial time 1 week
```

### Single Item from Stack
```
/ah sell 500 single
```

### Stack Price for Multiple Items
```
/ah sell 5000 stack
```

## GUI Mode

If you use `/ah sell` without arguments (and GUI mode is enabled), it opens the sell GUI where you can:
- Select listing type (Auction or BIN)
- Set prices visually
- Configure all options through the interface
- Preview your listing before confirming

## Requirements

Before listing an item, ensure:
1. **Item in Hand** - You must be holding the item you want to sell
2. **Valid Item** - Item must meet listing requirements (not blocked items)
3. **Price Limits** - Price must be within min/max limits for that item type
4. **Listing Limit** - You must not exceed your maximum listing limit
5. **Cooldown** - You must not be on listing cooldown
6. **Not Banned** - You must not be banned from selling

## Listing Types

### Buy It Now (BIN)
- Fixed price, instant purchase
- No bidding required
- First buyer gets the item

### Auction (Bidding)
- Players bid on the item
- Highest bidder wins
- Optional Buy It Now price
- Bid increment determines minimum bid increases

## Price Validation

The plugin validates:
- **Minimum Price** - Must meet global minimum
- **Maximum Price** - Must not exceed global maximum
- **Item-Specific Limits** - Must meet item-specific price limits if configured
- **Starting Bid** - Must meet minimum starting bid requirements
- **Bid Increment** - Must meet minimum increment requirements

## Bundle Creation

When using the bundle flag:
- All similar items from your inventory are bundled together
- The bundle is created automatically
- You can preview the bundle before listing
- Bundle limits apply (separate from regular listing limits)

## Related Commands

- `/ah active` - View your active listings
- `/ah expired` - View your expired listings
- `/ah confirm` - Confirm pending listing cancellations

## Notes

- Items are removed from your inventory when listed
- You can cancel listings before they expire (if no bids exist)
- Expired items can be collected from the expired items GUI
- Server auctions are owned by the server, not a player
- Infinite listings never expire (admin feature)
- Smart min buy price multiplies price by stack size automatically

