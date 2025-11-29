# Permissions

Complete reference of all permissions in Auction House. Permissions control access to commands, features, and functionality.

## Permission Structure

Auction House uses a hierarchical permission structure:
- Base permissions: `auctionhouse.cmd.*`
- Feature permissions: `auctionhouse.*`
- Admin permissions: `auctionhouse.cmd.admin.*`

## Command Permissions

### Main Commands

| Permission | Description | Default |
|------------|-------------|---------|
| `auctionhouse.cmd` | Base command permission (allows `/ah`) | Not granted by default |
| `auctionhouse.cmd.sell` | Use `/ah sell` command | Not granted by default |
| `auctionhouse.cmd.search` | Use `/ah search` command | Not granted by default |
| `auctionhouse.cmd.active` | Use `/ah active` command | Not granted by default |
| `auctionhouse.cmd.expired` | Use `/ah expired` command | Not granted by default |
| `auctionhouse.cmd.bids` | Use `/ah bids` command | Not granted by default |
| `auctionhouse.cmd.cart` | Use `/ah cart` command | Not granted by default |
| `auctionhouse.cmd.request` | Use `/ah request` command | Not granted by default |
| `auctionhouse.cmd.payments` | Use `/ah payments` command | Not granted by default |
| `auctionhouse.cmd.transactions` | Use `/ah transactions` command | Not granted by default |
| `auctionhouse.cmd.stats` | Use `/ah stats` command | Not granted by default |

### Admin Commands

| Permission | Description | Default |
|------------|-------------|---------|
| `auctionhouse.cmd.admin` | Base admin command permission | Operators only |
| `auctionhouse.cmd.admin.logs` | View admin logs (`/ah admin logs`) | Operators only |
| `auctionhouse.cmd.admin.viewexpired` | View expired items for players | Operators only |
| `auctionhouse.cmd.admin.endall` | End all auctions (`/ah admin endall`) | Operators only |
| `auctionhouse.cmd.admin.relistall` | Relist all expired auctions | Operators only |
| `auctionhouse.cmd.admin.clearall` | Clear all auctions (dangerous) | Operators only |
| `auctionhouse.cmd.admin.clear` | Clear player auctions | Operators only |
| `auctionhouse.cmd.admin.clearbids` | Clear player bids | Operators only |
| `auctionhouse.cmd.admin.opensell` | Open sell GUI for players | Operators only |
| `auctionhouse.cmd.admin.open` | Open auction house for players | Operators only |
| `auctionhouse.cmd.admin.bans` | Access ban management GUI | Operators only |

### Utility Commands

| Permission | Description | Default |
|------------|-------------|---------|
| `auctionhouse.cmd.ban` | Ban players (`/ah ban`) | Operators only |
| `auctionhouse.cmd.unban` | Unban players (`/ah unban`) | Operators only |
| `auctionhouse.cmd.filter` | Manage filters (`/ah filter`) | Not granted by default |
| `auctionhouse.cmds.reload` | Reload plugin (`/ah reload`) | Operators only |
| `auctionhouse.cmd.settings` | Open settings GUI (`/ah settings`) | Operators only |
| `auctionhouse.cmd.confirm` | Confirm actions (`/ah confirm`) | Not granted by default |
| `auctionhouse.cmds.togglelistinfo` | Toggle list info (`/ah togglelistinfo`) | Not granted by default |
| `auctionhouse.cmd.pricelimit` | Manage price limits | Operators only |
| `auctionhouse.cmd.markchest` | Mark auction chests | Not granted by default |
| `auctionhouse.cmd.migrate` | Migrate database | Operators only |
| `auctionhouse.cmd.pop` | Pop command (admin utility) | Operators only |
| `auctionhouse.cmd.upload` | Upload command (admin utility) | Operators only |
| `auctionhouse.cmds.debug` | Debug command | Operators only |

## Feature Permissions

### Admin Features

| Permission | Description | Default |
|------------|-------------|---------|
| `auctionhouse.admin` | Full admin access (bypasses many restrictions) | Operators only |
| `auctionhouse.admin.returnitem` | Return items from admin GUI | Operators only |
| `auctionhouse.admin.claimitem` | Claim items from admin GUI | Operators only |
| `auctionhouse.admin.deleteitem` | Delete items from admin GUI | Operators only |
| `auctionhouse.admin.copyitem` | Copy items from admin GUI | Operators only |

### Command Flags

| Permission | Description | Default |
|------------|-------------|---------|
| `auctionhouse.cmdflag.stack` | Use stack price flag in sell command | Not granted by default |

### Auction Chest Mode

| Permission | Description | Default |
|------------|-------------|---------|
| `auctionhouse.auctionchestbypass` | Bypass auction chest mode restrictions | Operators only |

### Transactions

| Permission | Description | Default |
|------------|-------------|---------|
| `auctionhouse.transactions.viewall` | View all players' transactions | Operators only |

### Inspect Features

| Permission | Description | Default |
|------------|-------------|---------|
| `auctionhouse.inspectshulker` | Inspect shulker boxes in auction house | Not granted by default |

## Listing Limits

### Unlimited Listings

| Permission | Description | Default |
|------------|-------------|---------|
| `auctionhouse.maxallowedlistings.*` | Unlimited listings (wildcard) | Not granted by default |

### Specific Listing Limits

You can set specific listing limits using:
```
auctionhouse.maxallowedlistings.<number>
```

For example:
- `auctionhouse.maxallowedlistings.5` - Maximum 5 listings
- `auctionhouse.maxallowedlistings.10` - Maximum 10 listings
- `auctionhouse.maxallowedlistings.20` - Maximum 20 listings

## Permission Examples

### Basic Player Setup

Give a player access to basic auction house features:
```yaml
permissions:
  - auctionhouse.cmd
  - auctionhouse.cmd.sell
  - auctionhouse.cmd.search
  - auctionhouse.cmd.active
  - auctionhouse.cmd.expired
  - auctionhouse.cmd.bids
  - auctionhouse.cmd.cart
  - auctionhouse.cmd.payments
  - auctionhouse.cmd.transactions
```

### Premium Player Setup

Give a player premium features:
```yaml
permissions:
  - auctionhouse.cmd.*
  - auctionhouse.maxallowedlistings.20
  - auctionhouse.cmdflag.stack
```

### Admin Setup

Full admin access:
```yaml
permissions:
  - auctionhouse.*
  - auctionhouse.cmd.admin.*
  - auctionhouse.admin.*
```

### Moderator Setup

Moderator with limited admin access:
```yaml
permissions:
  - auctionhouse.cmd.*
  - auctionhouse.cmd.admin.bans
  - auctionhouse.cmd.ban
  - auctionhouse.cmd.unban
  - auctionhouse.cmd.admin.viewexpired
```

## Permission Management

### Using Permission Plugins

Auction House works with all major permission plugins:
- **LuckPerms** - Recommended for modern servers
- **PermissionsEx** - Legacy support
- **GroupManager** - Legacy support
- **Vault** - Permission bridge

### Wildcard Permissions

You can use wildcards to grant multiple permissions:
- `auctionhouse.cmd.*` - All commands
- `auctionhouse.cmd.admin.*` - All admin commands
- `auctionhouse.*` - Everything (use with caution)

### Negative Permissions

Some permission plugins support negative permissions:
- `-auctionhouse.cmd.admin.clearall` - Deny specific admin command
- `-auctionhouse.admin` - Deny admin access even if parent grants it

## Default Behavior

By default, Auction House does **not** grant any permissions. Server administrators must explicitly grant permissions to players. This provides maximum security and control.

### Operator Override

Server operators (OP) have access to:
- All admin commands
- All admin features
- Bypass restrictions

This can be disabled in the configuration file.

## Permission Best Practices

1. **Principle of Least Privilege** - Only grant permissions players need
2. **Group-Based Permissions** - Use permission groups for easier management
3. **Regular Audits** - Review permissions periodically
4. **Documentation** - Document custom permission setups
5. **Testing** - Test permissions in a test environment first

## Troubleshooting

### Player Can't Use Commands

1. Check if player has `auctionhouse.cmd` permission
2. Check specific command permission (e.g., `auctionhouse.cmd.sell`)
3. Verify permission plugin is working
4. Check for negative permissions
5. Reload permissions plugin

### Admin Commands Not Working

1. Verify `auctionhouse.cmd.admin` permission
2. Check specific subcommand permission
3. Ensure player is operator (if operator override enabled)
4. Check server logs for permission errors

### Listing Limits Not Working

1. Check `auctionhouse.maxallowedlistings.*` permission
2. Verify specific limit permission format
3. Check configuration for default limits
4. Ensure permission plugin supports wildcards

## Related Documentation

- [Commands](commands.md) - Command documentation with permission requirements
- [Getting Started](getting-started.md) - Basic setup including permissions
- [Features](features.md) - Feature documentation

