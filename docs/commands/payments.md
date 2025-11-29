# Payments Command - `/ah payments`

The payments command opens a GUI where you can collect payments from items you've sold.

## Syntax

```
/ah payments
```

## Description

When you sell items on the auction house, payments are stored and can be collected using this command. This is especially useful if manual payment collection is enabled in the server settings.

## Usage

Simply type:
```
/ah payments
```

This opens the Payment Collection GUI showing:
- All pending payments from sold items
- Payment amounts
- Item information
- Buyer details
- Options to collect payments

## Permission

- **Permission**: `auctionhouse.cmd.payments`
- **Default**: All players (if granted by server admin)

## GUI Features

The Payment Collection GUI provides:

### Payment Display
- List of all pending payments
- Payment amounts
- Item that was sold
- Buyer information
- Payment date

### Actions Available
- **Collect Payment** - Collect individual payments
- **Collect All** - Collect all payments at once
- **View Details** - See full payment information

### Information Displayed
- Payment amount
- Currency type
- Item sold
- Buyer name
- Payment reason
- Date of sale

## Payment Collection

### Automatic Collection
- If automatic collection is enabled, payments are added directly to your balance
- No manual collection needed
- Payments appear immediately after sale

### Manual Collection
- If manual collection is enabled, you must use this command
- Payments are stored until collected
- You can collect individual payments or all at once

## Payment Types

Payments can come from:
- **Item Sales** - Items sold via Buy It Now
- **Auction Wins** - Items sold through auctions
- **Bid Returns** - Money returned from outbid auctions
- **Admin Actions** - Payments from admin-removed auctions

## When to Use

Use this command to:
- Collect payments from sales
- Check payment status
- Review sales history
- Manage your earnings

## Related Commands

- `/ah expired` - View expired listings (may show payment info)
- `/ah transactions` - View complete transaction history
- `/ah active` - View your active listings

## Notes

- Payments may expire if not collected (if configured)
- Some servers auto-collect payments
- Payment collection may have limits
- Different currencies are handled separately
- Payment history is tracked in transactions

