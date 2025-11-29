# Transactions Command - `/ah transactions`

The transactions command allows you to view your complete transaction history, including purchases, sales, bids, and payments.

## Syntax

```
/ah transactions [search <player>]
```

## Description

This command provides a comprehensive view of all your auction house activity. You can see purchases, sales, bids placed, payments received, and more. This is useful for tracking your auction house activity and financial history.

## Usage

### View Your Transactions

```
/ah transactions
```

Opens the transaction type selection GUI where you can choose to view:
- All transactions
- Purchases only
- Sales only
- Bids only
- Payments only

### Search Player Transactions

```
/ah transactions search <player>
```

View transactions with a specific player (if you have permission).

## Permission

- **Base Permission**: `auctionhouse.cmd.transactions`
- **View All Permission**: `auctionhouse.transactions.viewall` (to view all players' transactions)
- **Default**: All players (if granted by server admin)

## Transaction Types

### Purchases
- Items you've bought
- Purchase price
- Seller information
- Purchase date

### Sales
- Items you've sold
- Sale price
- Buyer information
- Sale date

### Bids
- Bids you've placed
- Bid amounts
- Auction information
- Bid status (won/lost/active)

### Payments
- Payments received
- Payment amounts
- Payment reasons
- Payment dates

## GUI Features

The Transactions GUI provides:

### Transaction Display
- Chronological list of transactions
- Transaction type indicators
- Amounts and values
- Other party information
- Dates and times

### Filtering Options
- Filter by transaction type
- Filter by date range
- Filter by player
- Search functionality

### Information Displayed
- Transaction type
- Amount/value
- Item information
- Other party (buyer/seller)
- Date and time
- Transaction status

## When to Use

Use this command to:
- Track your auction house activity
- Review financial history
- See all purchases and sales
- Monitor bidding activity
- Check payment history
- Audit transactions

## Related Commands

- `/ah payments` - Collect payments
- `/ah stats` - View statistics
- `/ah active` - View active listings

## Notes

- Transaction history may be limited (if configured)
- Some transaction types may be filtered
- Admin can view all transactions
- Transactions are permanent records
- Export functionality may be available (if configured)

