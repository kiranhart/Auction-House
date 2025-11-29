# Payments GUI

The payments GUI allows you to collect payments from items you've sold.

## Accessing the GUI

Open payments using:
```
/ah payments
```

## GUI Layout

### Payment List
- **Payment Display**: Shows all pending payments
- **Payment Amount**: Amount of each payment
- **Item Information**: Item that was sold
- **Buyer Details**: Who bought the item
- **Payment Date**: When the sale occurred

### Actions
- **Collect Payment**: Collect individual payments
- **Collect All**: Collect all payments at once
- **View Details**: See full payment information

## Payment Types

### Item Sales
- Payments from BIN sales
- Instant payment (if auto-collect enabled)
- Manual collection (if manual mode)

### Auction Wins
- Payments from auction sales
- Received when auction ends
- Automatic or manual collection

### Bid Returns
- Money returned from outbid auctions
- Returned when you're outbid
- Automatic return (if enabled)

### Admin Actions
- Payments from admin-removed auctions
- Special payment types
- Various reasons

## Collecting Payments

### Individual Collection
1. **Click Payment**: Click a payment item
2. **Collect**: Payment is added to balance
3. **Removed**: Payment removed from list
4. **Repeat**: Collect other payments

### Bulk Collection
1. **Collect All Button**: Click to collect all
2. **Confirmation**: May require confirmation
3. **All Collected**: All payments collected
4. **Balance Updated**: Money added to account

## Payment Information

### Displayed Information
- **Amount**: Payment amount
- **Currency**: Currency type
- **Item**: Item that was sold
- **Buyer**: Who bought it
- **Reason**: Payment reason
- **Date**: When payment occurred

## Payment Collection Modes

### Automatic Collection
- Payments added directly to balance
- No manual collection needed
- Immediate payment
- No GUI needed

### Manual Collection
- Must use this GUI to collect
- Payments stored until collected
- Can collect individually or all
- More control over payments

## Payment Reasons

Payments can have different reasons:
- **Item Sale**: Item was sold
- **Auction Win**: Won an auction
- **Bid Return**: Money returned from bid
- **Admin Action**: Admin-related payment

## Tips

1. **Collect Regularly** - Don't let payments accumulate
2. **Check Often** - Monitor your payments
3. **Use Collect All** - Faster for multiple payments
4. **Review History** - Check payment details
5. **Track Earnings** - Monitor your sales

## Related Commands

- `/ah payments` - Open payments GUI
- `/ah expired` - View expired listings
- `/ah transactions` - View transaction history

## Related GUIs

- [Expired Items GUI](expired-items.md) - May show payment info
- [Transactions GUI](transactions.md) - Payment history
- [Active Auctions GUI](active-auctions.md) - Your listings

