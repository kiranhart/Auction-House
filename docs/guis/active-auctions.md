# Active Auctions GUI

The active auctions GUI shows all your currently active auction listings.

## Accessing the GUI

Open active auctions using:
```
/ah active
```

## GUI Layout

### Your Listings
- **Item Display**: Shows all your active listings
- **Current Status**: Price, bids, time remaining
- **Quick Actions**: Cancel, view details, etc.
- **Pagination**: Multiple pages if many listings

### Information Displayed
- **Item Name**: Name of listed item
- **Listing Type**: Auction or BIN
- **Current Price**: Current price or highest bid
- **Time Remaining**: Countdown until expiration
- **Bid Count**: Number of bids (for auctions)
- **Buyer Info**: Buyer information (if sold)

## Item Actions

### View Details
- **Click Item**: View full item details
- **Bid History**: See all bids (for auctions)
- **Buyer Info**: See buyer information
- **Full Information**: Complete listing details

### Cancel Listing
- **Cancel Button**: Cancel a listing
- **Restrictions**: May have restrictions (bids, time)
- **Confirmation**: May require confirmation
- **Item Return**: Item returned to inventory

### Collect Early
Some settings allow early collection:
- **Collect Button**: Collect before expiration
- **Restrictions**: May have restrictions
- **Payment**: Receive payment immediately

### Relist
- **Relist Button**: Quickly relist item
- **New Price**: Set new price
- **New Time**: Set new duration
- **Quick Relist**: Fast relisting process

## Listing Status

### Active Listings
- Currently listed and available
- Can receive bids or purchases
- Time remaining shown
- Can be canceled (if allowed)

### Sold Listings
- Items that have been sold
- Show buyer information
- Payment available
- Can be collected

### Pending Listings
- Listings being processed
- May show processing status
- Will become active soon

## Real-Time Updates

If auto-refresh is enabled:
- **Price Updates**: Bid prices update in real-time
- **Bid Count**: Bid counts update automatically
- **Time Remaining**: Countdown updates
- **Status Changes**: Status updates immediately

## Monitoring Your Listings

### Check Status
- See if items have received bids
- Monitor time remaining
- Check if items have sold
- View buyer information

### Manage Listings
- Cancel unwanted listings
- Adjust prices (if allowed)
- Extend time (if allowed)
- Collect early (if allowed)

## Tips

1. **Monitor Regularly** - Check your listings often
2. **Respond to Bids** - Adjust if needed
3. **Cancel Unwanted** - Remove listings you don't want
4. **Check Time** - Ensure listings don't expire unexpectedly
5. **Review Sales** - See what's selling and what's not

## Related Commands

- `/ah active` - View active listings
- `/ah expired` - View expired listings
- `/ah sell` - Create new listings

## Related GUIs

- [Expired Items GUI](expired-items.md) - Expired listings
- [Main Auction House GUI](main-auction-house.md) - Browse all auctions
- [Selling GUIs](selling.md) - Create new listings

