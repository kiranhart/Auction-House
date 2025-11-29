# Admin GUIs

Admin GUIs provide administrative tools for managing the auction house system.

## Admin Logs GUI

Accessed via `/ah admin logs`.

### Display
Shows all admin actions:
- **Action Type**: Type of action performed
- **Admin Name**: Who performed the action
- **Target**: Target player or item
- **Details**: Action details
- **Timestamp**: When it occurred

### Information
- Commands executed
- Auctions modified
- Players banned/unbanned
- System changes

## Admin Expired Items GUI

Accessed via `/ah admin viewexpired <player>`.

### Display
Shows expired items for a specific player:
- **Player's Items**: All expired items
- **Sale Status**: Sold or unsold
- **Payment Info**: Payment information
- **Admin Actions**: Admin action options

## Ban Management GUI

Accessed via `/ah admin bans`.

### Features
- **View Bans**: See all banned players
- **Ban Types**: Different ban types
- **Ban Reasons**: Why players are banned
- **Ban Durations**: How long bans last
- **Unban Options**: Unban players

### Ban Types
- **Selling**: Banned from selling
- **Buying**: Banned from buying
- **Requests**: Banned from requests
- **Everything**: Banned from all features

## Price Limits GUI

Accessed via `/ah pricelimit`.

### Features
- **View Limits**: See all price limits
- **Item Limits**: Limits for specific items
- **Min/Max Prices**: Minimum and maximum prices
- **Edit Limits**: Modify price limits
- **Add Limits**: Add new price limits

## Admin Item GUI

Opened when viewing items as admin.

### Actions
- **Return Item**: Return item to seller
- **Claim Item**: Claim item for admin
- **Delete Item**: Delete the listing
- **Copy Item**: Copy item to inventory
- **View Details**: Full item information

### Permissions Required
- `auctionhouse.admin.returnitem`
- `auctionhouse.admin.claimitem`
- `auctionhouse.admin.deleteitem`
- `auctionhouse.admin.copyitem`

## Admin Features

### Item Management
- View any item
- Modify listings
- Remove items
- Return items to players

### Player Management
- View player listings
- Clear player auctions
- Clear player bids
- View player transactions

### System Management
- View system logs
- Manage bans
- Set price limits
- System configuration

## Security

### Admin Actions
- All actions are logged
- Cannot be undone easily
- Require confirmation
- Tracked for auditing

### Permissions
- Specific permissions for each action
- Cannot bypass without permission
- Operator override available
- Permission-based access

## Tips

1. **Use Logs** - Check logs regularly
2. **Be Careful** - Admin actions are powerful
3. **Document Actions** - Keep records
4. **Use Permissions** - Grant specific permissions
5. **Audit Regularly** - Review admin activity

## Related Commands

- `/ah admin` - Admin commands
- `/ah ban` - Ban players
- `/ah pricelimit` - Manage price limits

## Related GUIs

- [Main Auction House GUI](main-auction-house.md) - Browse as admin
- [Transactions GUI](transactions.md) - View all transactions

