# Stats Command - `/ah stats`

The stats command allows you to view statistics about your auction house activity, including sales, purchases, earnings, and more.

## Syntax

```
/ah stats [player]
```

## Description

This command displays comprehensive statistics about auction house activity. You can view your own stats or (with permission) view other players' statistics.

## Usage

### View Your Stats

```
/ah stats
```

Opens the statistics selection GUI where you can choose to view:
- Your overall statistics
- Sales statistics
- Purchase statistics
- Earnings statistics
- Leaderboard positions

### View Another Player's Stats

```
/ah stats <player>
```

View statistics for a specific player (if you have permission).

## Permission

- **Permission**: `auctionhouse.cmd.stats`
- **Default**: All players (if granted by server admin)

## Statistics Displayed

### Overall Statistics
- Total items sold
- Total items purchased
- Total earnings
- Total spent
- Net profit/loss
- Average sale price
- Average purchase price

### Sales Statistics
- Number of sales
- Total sales value
- Highest sale price
- Lowest sale price
- Average sale price
- Most sold item

### Purchase Statistics
- Number of purchases
- Total purchase value
- Highest purchase price
- Lowest purchase price
- Average purchase price
- Most purchased item

### Earnings Statistics
- Total earnings
- Earnings from sales
- Earnings from auctions
- Earnings from BIN
- Payment collection status

## GUI Features

The Statistics GUI provides:

### Stat Display
- Visual statistics
- Charts and graphs (if available)
- Leaderboard positions
- Comparison with other players

### Navigation
- Switch between stat types
- View leaderboards
- Compare with other players
- Export statistics (if available)

### Information Displayed
- Numerical statistics
- Percentages and ratios
- Rankings and positions
- Historical data
- Trends and patterns

## Leaderboards

Statistics may include:
- Top sellers
- Top buyers
- Highest earners
- Most active users
- Best deals made

## When to Use

Use this command to:
- Track your auction house performance
- See your earnings and spending
- Compare with other players
- View leaderboard positions
- Analyze your auction activity
- Set personal goals

## Related Commands

- `/ah transactions` - View transaction history
- `/ah active` - View active listings
- `/ah expired` - View expired listings

## Notes

- Statistics are calculated from transaction history
- Some stats may require certain features to be enabled
- Leaderboards update in real-time
- Historical statistics may be limited
- Admin can view all player statistics

