# Bidding GUIs

The bidding GUIs allow you to place bids, view your active bids, and manage your bidding activity.

## Active Bids GUI

Accessed via `/ah bids`.

### Display
Shows all auctions where you are the highest bidder:
- **Item Display**: Visual representation of each auction
- **Your Bid**: Your current bid amount
- **Time Remaining**: Countdown until auction ends
- **Outbid Status**: Whether you've been outbid

### Actions
- **View Auction**: Click to see full auction details
- **Increase Bid**: Place a higher bid
- **Cancel Bid**: Cancel your bid (if allowed)
- **Buy It Now**: Use Buy It Now option if available

### Information
- Item name and details
- Your current bid
- Minimum next bid amount
- Time remaining
- Number of other bidders
- Buy It Now price (if available)

## Bid GUI

Opened when placing a bid on an auction.

### Features
- **Item Preview**: See the item you're bidding on
- **Current Bid**: Shows current highest bid
- **Your Bid**: Input field for your bid amount
- **Minimum Bid**: Shows minimum bid required
- **Buy It Now**: Option to buy instantly (if available)

### Bid Placement
1. **Enter Bid Amount**: Type your bid
2. **Validate**: System checks if bid is valid
3. **Confirm**: Confirm your bid
4. **Process**: Bid is placed and money held (if enabled)

### Bid Validation
- **Minimum Bid**: Must meet minimum bid requirement
- **Bid Increment**: Must increase by bid increment
- **Sufficient Funds**: Must have enough money
- **Not Outbid**: Must be higher than current bid

## Bid Confirmation GUI

Confirmation dialog before placing a bid.

### Information
- **Item**: Item you're bidding on
- **Your Bid**: Bid amount you're placing
- **Current Bid**: Current highest bid
- **Total Cost**: Total amount (if bidding takes money)

### Actions
- **Confirm**: Place the bid
- **Cancel**: Cancel bid placement
- **Edit**: Go back and change bid amount

## Understanding Bids

### Highest Bidder
- You are the current highest bidder
- You'll win if no one outbids you
- Your money may be held until auction ends

### Outbid Status
- If someone outbids you, you'll see this
- Your money is returned (if held)
- You can place a new bid

### Winning Bids
- When auction ends, you win the item
- Item is delivered automatically
- Payment is processed

## Bid Management

### Increasing Bids
- Place a higher bid to become highest bidder
- Must meet bid increment requirements
- Previous bid money is returned (if held)

### Canceling Bids
- Some servers allow bid cancellation
- Money is returned immediately
- Auction returns to previous state

### Buy It Now
- Skip bidding and buy instantly
- Pay the Buy It Now price
- Item is yours immediately

## Tips

1. **Monitor Your Bids** - Check `/ah bids` regularly
2. **Set Reasonable Bids** - Don't overbid
3. **Watch Time Remaining** - Bid before auction ends
4. **Use Buy It Now** - If available and price is good
5. **Check Bid History** - See who's bidding

## Related Commands

- `/ah bids` - View your active bids
- `/ah` - Browse auctions to bid on
- `/ah transactions` - View bid history

## Related GUIs

- [Main Auction House GUI](main-auction-house.md) - Browse auctions
- [Confirmation GUIs](confirmations.md) - Confirm bids
- [Transactions GUI](transactions.md) - View bid history

