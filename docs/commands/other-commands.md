# Other Commands

This document covers additional utility and administrative commands in Auction House.

## Ban Commands

### Ban - `/ah ban`

Ban a player from using the auction house.

**Syntax:**
```
/ah ban [player]
```

**Permission:** `auctionhouse.cmd.ban`

**Usage:**
- Without arguments: Opens player selector GUI, then ban type selection
- With player: Opens ban GUI for the specified player

**Features:**
- Select ban type (selling, buying, requests, everything)
- Set ban duration
- Add ban reason
- Ban management through GUI

### Unban - `/ah unban`

Unban a player from the auction house.

**Syntax:**
```
/ah unban <player>
```

**Permission:** `auctionhouse.cmd.unban`

**Usage:**
- Removes ban from specified player
- Player can use auction house again
- Ban history is maintained

## Filter Command - `/ah filter`

Manage filter whitelist for item categories.

**Syntax:**
```
/ah filter
/ah filter additem <category>
```

**Permission:** `auctionhouse.cmd.filter`

**Usage:**
- Without arguments: Opens filter whitelist GUI
- With `additem`: Adds held item to filter whitelist for specified category

**Categories:**
- Various item categories (configured in settings)
- Only whitelist-allowed categories can be used

**Example:**
```
/ah filter additem WEAPONS
```

## Reload Command - `/ah reload`

Reload plugin configuration files.

**Syntax:**
```
/ah reload
```

**Permission:** `auctionhouse.cmds.reload`

**Usage:**
- Reloads config.yml
- Applies new settings
- Useful after configuration changes
- Console and players can use (with permission)

## Settings Command - `/ah settings`

Open in-game configuration editor (if enabled).

**Syntax:**
```
/ah settings
```

**Permission:** `auctionhouse.cmd.settings`

**Usage:**
- Opens GUI-based config editor
- Edit settings without file access
- Must be enabled in config
- Changes apply immediately

**Note:** This feature must be enabled in the configuration file.

## Confirm Command - `/ah confirm`

Confirm pending actions (like canceling all listings).

**Syntax:**
```
/ah confirm
```

**Permission:** `auctionhouse.cmd.confirm`

**Usage:**
- Confirms pending cancellation requests
- Used when canceling all listings
- Has time limit for confirmation
- Prevents accidental mass cancellations

## Toggle List Info - `/ah togglelistinfo`

Toggle listing information display.

**Syntax:**
```
/ah togglelistinfo
```

**Permission:** `auctionhouse.cmds.togglelistinfo`

**Usage:**
- Toggles display of listing information
- Affects how listings are shown in GUI
- Personal preference setting
- Persists across sessions

## Price Limit Command - `/ah pricelimit`

Manage price limits for specific items.

**Syntax:**
```
/ah pricelimit
/ah pricelimit set <min|max> <price>
```

**Permission:** `auctionhouse.cmd.pricelimit`

**Usage:**
- Without arguments: Opens price limits GUI
- With `set`: Sets min/max price for held item

**Examples:**
```
/ah pricelimit set min 100
/ah pricelimit set max 10000
```

**Features:**
- Set minimum prices for items
- Set maximum prices for items
- Prevents overpricing/underpricing
- Item-specific price controls

## Mark Chest Command - `/ah markchest`

Mark or unmark a chest as an auction chest (1.14+).

**Syntax:**
```
/ah markchest
```

**Permission:** `auctionhouse.cmd.markchest`

**Usage:**
- Look at a chest within 10 blocks
- Execute command to mark/unmark
- Toggles chest marking status
- Used for auction chest mode

**Requirements:**
- Minecraft 1.14 or higher
- Must be looking at a chest
- Chest must be within 10 blocks

## Migrate Command - `/ah migrate`

Migrate data between databases.

**Syntax:**
```
/ah migrate
```

**Permission:** `auctionhouse.cmd.migrate`

**Usage:**
- Migrates data between database types
- SQLite to MySQL or vice versa
- Useful for server upgrades
- **Warning**: Backup data first

**Note:** This is an advanced command. Use with caution and always backup first.

## Pop Command - `/ah pop`

Admin utility command (specific functionality varies).

**Syntax:**
```
/ah pop
```

**Permission:** `auctionhouse.cmd.pop`

**Usage:**
- Admin utility command
- Functionality depends on implementation
- Check with server admin for details

## Upload Command - `/ah upload`

Admin utility command for data upload.

**Syntax:**
```
/ah upload
```

**Permission:** `auctionhouse.cmd.upload`

**Usage:**
- Admin utility command
- Used for data management
- Check with server admin for details

## Debug Command - `/ah debug`

Debug command for troubleshooting.

**Syntax:**
```
/ah debug
```

**Permission:** `auctionhouse.cmd.debug`

**Usage:**
- Debug and troubleshooting tool
- Provides system information
- Useful for support requests
- Admin/developer use

## Command Summary

| Command | Permission | Description |
|---------|------------|-------------|
| `/ah ban` | `auctionhouse.cmd.ban` | Ban a player |
| `/ah unban` | `auctionhouse.cmd.unban` | Unban a player |
| `/ah filter` | `auctionhouse.cmd.filter` | Manage filters |
| `/ah reload` | `auctionhouse.cmds.reload` | Reload config |
| `/ah settings` | `auctionhouse.cmd.settings` | Open settings GUI |
| `/ah confirm` | `auctionhouse.cmd.confirm` | Confirm actions |
| `/ah togglelistinfo` | `auctionhouse.cmds.togglelistinfo` | Toggle list info |
| `/ah pricelimit` | `auctionhouse.cmd.pricelimit` | Manage price limits |
| `/ah markchest` | `auctionhouse.cmd.markchest` | Mark auction chest |
| `/ah migrate` | `auctionhouse.cmd.migrate` | Migrate database |
| `/ah pop` | `auctionhouse.cmd.pop` | Admin utility |
| `/ah upload` | `auctionhouse.cmd.upload` | Admin utility |
| `/ah debug` | `auctionhouse.cmd.debug` | Debug tool |

## Notes

- Most utility commands require specific permissions
- Some commands are admin-only
- Always backup before using migration commands
- Debug commands provide technical information
- Filter commands help manage item categories
- Price limits help control auction economy

