# Commands

Auction House provides a comprehensive set of commands for managing auctions, viewing listings, and administering the auction system. All commands are prefixed with `/ah` (or your configured alias).

## Command Overview

### Main Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/ah` | Open the main auction house GUI | `auctionhouse.cmd` |
| `/ah sell` | Sell items on the auction house | `auctionhouse.cmd.sell` |
| `/ah search <keywords>` | Search for items in the auction house | `auctionhouse.cmd.search` |
| `/ah active` | View your active auction listings | `auctionhouse.cmd.active` |
| `/ah expired` | View your expired auction listings | `auctionhouse.cmd.expired` |
| `/ah bids` | View your active bids | `auctionhouse.cmd.bids` |
| `/ah cart` | Open your shopping cart | `auctionhouse.cmd.cart` |
| `/ah request` | Request items you want to buy | `auctionhouse.cmd.request` |
| `/ah payments` | Collect payments from sold items | `auctionhouse.cmd.payments` |
| `/ah transactions` | View transaction history | `auctionhouse.cmd.transactions` |
| `/ah stats` | View statistics | `auctionhouse.cmd.stats` |

### Admin Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/ah admin` | Access admin commands | `auctionhouse.cmd.admin` |
| `/ah ban <player>` | Ban a player from using auction house | `auctionhouse.cmd.ban` |
| `/ah unban <player>` | Unban a player | `auctionhouse.cmd.unban` |
| `/ah reload` | Reload plugin configuration | `auctionhouse.cmds.reload` |

### Utility Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/ah filter` | Manage filter whitelist | `auctionhouse.cmd.filter` |
| `/ah settings` | Open in-game settings editor | `auctionhouse.cmd.settings` |
| `/ah confirm` | Confirm pending actions | `auctionhouse.cmd.confirm` |
| `/ah togglelistinfo` | Toggle listing info display | `auctionhouse.cmds.togglelistinfo` |
| `/ah pricelimit` | Manage price limits for items | `auctionhouse.cmd.pricelimit` |
| `/ah markchest` | Mark/unmark a chest as auction chest | `auctionhouse.cmd.markchest` |

### Advanced Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/ah migrate` | Migrate data between databases | `auctionhouse.cmd.migrate` |
| `/ah pop` | Pop command (admin utility) | `auctionhouse.cmd.pop` |
| `/ah upload` | Upload command (admin utility) | `auctionhouse.cmd.upload` |
| `/ah debug` | Debug command (admin utility) | `auctionhouse.cmd.debug` |

## Detailed Command Documentation

### [Main Command](commands/main-command.md)
The main `/ah` command opens the auction house GUI. It can also be used to search for items directly.

### [Sell Command](commands/sell.md)
The `/ah sell` command allows you to list items for sale. It supports many flags for advanced listing options including bundles, infinite listings, server auctions, and more.

### [Search Command](commands/search.md)
Search for specific items in the auction house by keywords.

### [Active Command](commands/active.md)
View all your currently active auction listings.

### [Expired Command](commands/expired.md)
View and collect items from your expired listings.

### [Bids Command](commands/bids.md)
View all auctions where you are the highest bidder.

### [Cart Command](commands/cart.md)
Open your shopping cart to view items you've added for purchase.

### [Request Command](commands/request.md)
Request specific items you want to buy. Other players can fulfill your requests.

### [Payments Command](commands/payments.md)
Collect payments from items you've sold.

### [Transactions Command](commands/transactions.md)
View your complete transaction history, including purchases, sales, and bids.

### [Stats Command](commands/stats.md)
View statistics about your auction house activity, including sales, purchases, and earnings.

### [Admin Command](commands/admin.md)
Comprehensive admin commands for managing the auction house, including ending auctions, viewing logs, managing bans, and more.

### [Other Commands](commands/other-commands.md)
Additional utility commands including ban/unban, filter management, reload, settings, and more.

## Command Aliases

Command aliases can be configured in the plugin's configuration file. By default, commands use standard aliases, but server administrators can customize these to match their server's command style.

## Permission Structure

All commands follow a consistent permission structure:
- Base command permission: `auctionhouse.cmd`
- Individual command permissions: `auctionhouse.cmd.<command>`
- Admin permissions: `auctionhouse.cmd.admin` and `auctionhouse.cmd.admin.<subcommand>`

For a complete list of permissions, see [Permissions](permissions.md).

## Command Usage Tips

1. **Tab Completion** - Most commands support tab completion for easier usage
2. **GUI Integration** - Many commands open GUIs for easier interaction
3. **Command Flags** - The sell command supports many flags for advanced options
4. **Search Integration** - Use `/ah search` or `/ah <keywords>` to search directly
5. **Admin Tools** - Admin commands provide powerful tools for managing auctions

## Getting Help

If you need help with a specific command:
- Check the detailed documentation for that command
- Review the [Features](features.md) documentation
- Check server logs for error messages
- Join the Discord for community support

