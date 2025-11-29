# Bids Command - `/ah bids`

The bids command opens a GUI showing all auctions where you are currently the highest bidder.

## Syntax

```
/ah bids
```

## Description

This command displays all auctions where you have placed the highest bid. You can monitor your bids, see if you've been outbid, and manage your bidding activity.

## Usage

Simply type:
```
/ah bids
```

This opens the Active Bids GUI showing:
- All auctions where you're the highest bidder
- Your current bid amount
- Time remaining on each auction
- Whether you've been outbid (if applicable)
- Options to increase your bid or cancel

## Permission

- **Permission**: `auctionhouse.cmd.bids`
- **Default**: All players (if granted by server admin)

## GUI Features

The Active Bids GUI provides:

### Bid Display
- Visual representation of each auction item
- Your current bid amount
- Time remaining until auction ends
- Outbid status (if applicable)

### Actions Available
- **View Auction** - Click to view full auction details
- **Increase Bid** - Place a higher bid
- **Cancel Bid** - Cancel your bid (if allowed)
- **Buy It Now** - Use Buy It Now option if available

### Information Displayed
- Item name and details
- Your current bid
- Minimum next bid amount
- Time remaining
- Number of other bidders
- Buy It Now price (if available)

## Understanding Your Bids

### Highest Bidder
- You are the current highest bidder
- You'll win the auction if no one outbids you
- Your money is held until auction ends or you're outbid

### Outbid Status
- If someone outbids you, you'll see this status
- Your money is returned (if bidding takes money)
- You can place a new bid to become highest again

### Winning Bids
- When auction ends, you win the item
- Item is delivered to you
- Payment is processed automatically

## When to Use

Use this command to:
- Monitor your active bids
- Check if you've been outbid
- Increase bids on items you want
- Cancel bids you no longer want
- Track bidding activity

## Related Commands

- `/ah` - Browse all auctions
- `/ah active` - View your listings (as seller)
- `/ah expired` - View expired items

## Notes

- Money may be held when you place a bid (if enabled in settings)
- Outbid money is returned automatically
- You can only see auctions where you're the highest bidder
- Bid cancellation may have restrictions
- Some auctions may not allow bid cancellation

