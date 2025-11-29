# Expired Items GUI

The expired items GUI shows all your expired auction listings that need to be collected.

## Accessing the GUI

Open expired items using:
```
/ah expired
```

## GUI Layout

### Expired Listings
- **Item Display**: Shows all expired items
- **Sale Status**: Indicates if item was sold or unsold
- **Payment Info**: Shows payment amount if sold
- **Time Expired**: When the item expired

### Actions Available
- **Collect Item**: Collect unsold items back
- **Collect Payment**: Collect payment for sold items
- **Delete**: Remove expired listings
- **Relist**: Quickly relist expired items

## Item Status

### Unsold Items
- Items that didn't sell
- Can be collected back to inventory
- Click item to collect
- Multiple items can be collected

### Sold Items
- Items that were sold
- Payment available for collection
- Use `/ah payments` for payments
- Shows buyer information

## Collecting Items

### Unsold Items
1. **Click Item**: Click the expired item
2. **Collect**: Item is returned to inventory
3. **Storage**: Items go to inventory (or storage if full)
4. **Multiple**: Can collect multiple items

### Sold Items
- Payment is separate from items
- Use `/ah payments` to collect
- Payment amount is shown
- Buyer information displayed

## GUI Information

### Displayed Information
- **Item Name**: Name of expired item
- **Sale Status**: Sold or unsold
- **Final Price**: Sale price if sold
- **Buyer**: Who bought it (if sold)
- **Time Expired**: When it expired

## Relisting Items

You can relist expired items:
1. **Select Item**: Click the expired item
2. **Relist Option**: Choose to relist
3. **Configure**: Set new price and time
4. **Confirm**: Item is relisted

## Collection Time Limits

Some servers have collection time limits:
- **Time Limit**: Must collect within time limit
- **Auto-Delete**: Items deleted after limit
- **Warning**: Warnings before deletion

## Tips

1. **Collect Regularly** - Don't let items expire from collection
2. **Check Payments** - Use `/ah payments` for sold items
3. **Relist Quickly** - Relist items that didn't sell
4. **Review Sales** - See what sold and what didn't
5. **Clean Up** - Delete collected items to keep GUI clean

## Related Commands

- `/ah expired` - View expired items
- `/ah payments` - Collect payments
- `/ah active` - View active listings
- `/ah sell` - Relist items

## Related GUIs

- [Active Auctions GUI](active-auctions.md) - Your active listings
- [Payments GUI](payments.md) - Collect payments
- [Main Auction House GUI](main-auction-house.md) - Browse auctions

