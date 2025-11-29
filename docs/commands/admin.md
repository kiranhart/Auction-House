# Admin Command - `/ah admin`

The admin command provides comprehensive administrative tools for managing the auction house system.

## Syntax

```
/ah admin <subcommand> [arguments]
```

## Description

This command provides various administrative functions including viewing logs, managing expired items, ending auctions, managing bans, and more.

## Permission

- **Base Permission**: `auctionhouse.cmd.admin`
- **Subcommand Permissions**: `auctionhouse.cmd.admin.<subcommand>`
- **Default**: Operators and admins only

## Subcommands

### Logs

View admin action logs:
```
/ah admin logs
```

**Permission**: `auctionhouse.cmd.admin.logs`

Opens a GUI showing all admin actions including:
- Commands executed
- Auctions modified
- Players banned/unbanned
- System changes

### View Expired

View expired items for a specific player:
```
/ah admin viewexpired <player>
```

**Permission**: `auctionhouse.cmd.admin.viewexpired`

Opens a GUI showing all expired items for the specified player.

### End All

End all active auctions immediately:
```
/ah admin endall
```

**Permission**: `auctionhouse.cmd.admin.endall`

- Ends all active auctions
- Items are marked as expired
- Buyers receive items
- Sellers receive payments

### Relist All

Relist all expired auctions:
```
/ah admin relistall [time]
```

**Permission**: `auctionhouse.cmd.admin.relistall`

- Relists all expired auctions
- Optional time parameter sets new expiration
- Default uses original listing time settings

### Clear All

Clear all auctions (use with caution):
```
/ah admin clearall
```

**Permission**: `auctionhouse.cmd.admin.clearall`

- Removes all auctions from the system
- **Warning**: This action cannot be undone
- Items and payments are not returned
- Use only in emergencies

### Clear Player

Clear all auctions for a specific player:
```
/ah admin clear <player> <returnItems> <returnMoney>
```

**Permission**: `auctionhouse.cmd.admin.clear`

- Clears all auctions for the specified player
- `returnItems`: true/false - Return items to player
- `returnMoney`: true/false - Return money to bidders
- Useful for player management

### Clear Bids

Clear all bids for a specific player:
```
/ah admin clearbids <player> <returnMoney>
```

**Permission**: `auctionhouse.cmd.admin.clearbids`

- Removes all bids from the specified player
- `returnMoney`: true/false - Return bid money
- Resets auctions to original state

### Open Sell

Open the sell GUI for another player:
```
/ah admin opensell <player>
```

**Permission**: `auctionhouse.cmd.admin.opensell`

- Opens the sell GUI for the specified player
- Useful for helping players list items
- Player must be online

### Open Auction House

Open the auction house GUI for another player:
```
/ah admin open <player>
```

**Permission**: `auctionhouse.cmd.admin.open`

- Opens the main auction house GUI for the specified player
- Useful for demonstrations or assistance
- Player must be online

### Bans

Open the ban management GUI:
```
/ah admin bans
```

**Permission**: `auctionhouse.cmd.admin.bans`

Opens a GUI for managing player bans:
- View all banned players
- Ban types and reasons
- Ban durations
- Unban options

## Usage Examples

### View Admin Logs
```
/ah admin logs
```

### View Player's Expired Items
```
/ah admin viewexpired Notch
```

### End All Auctions
```
/ah admin endall
```

### Relist Expired Auctions
```
/ah admin relistall
/ah admin relistall 86400
```

### Clear Player's Auctions
```
/ah admin clear Notch true true
```

### Clear Player's Bids
```
/ah admin clearbids Notch true
```

### Open Sell GUI for Player
```
/ah admin opensell Notch
```

### Open Auction House for Player
```
/ah admin open Notch
```

## Admin Logs

All admin actions are logged including:
- Command executed
- Admin name
- Target player (if applicable)
- Action details
- Timestamp

Logs can be viewed via `/ah admin logs` and are useful for:
- Auditing admin actions
- Tracking system changes
- Debugging issues
- Security monitoring

## Safety Considerations

### End All
- Ends all auctions immediately
- Players receive items/payments
- Use during maintenance or emergencies

### Clear All
- **DANGEROUS**: Removes all auctions
- Items and money are NOT returned
- Use only in extreme circumstances
- Consider backup first

### Clear Player
- Can disrupt player's auctions
- Use with caution
- Consider returning items/money

## Related Commands

- `/ah ban` - Ban a player
- `/ah unban` - Unban a player
- `/ah reload` - Reload plugin

## Notes

- All admin actions are logged
- Some commands require the player to be online
- Clear commands cannot be undone
- Always backup data before major operations
- Use clear commands sparingly

