# Search Command - `/ah search`

The search command allows you to search for specific items in the auction house by keywords.

## Syntax

```
/ah search <keywords>
```

## Description

The search command opens the auction house GUI filtered to show only items matching your search keywords. This is useful for quickly finding specific items without browsing through all listings.

## Usage

### Basic Search

```
/ah search diamond
```

Searches for all items containing "diamond" in their name or description.

### Multi-Word Search

```
/ah search enchanted sword
```

Searches for items containing both "enchanted" and "sword".

### Specific Item Search

```
/ah search diamond pickaxe efficiency
```

Searches for diamond pickaxes with efficiency enchantment.

## Permission

- **Permission**: `auctionhouse.cmd.search`
- **Default**: All players (if granted by server admin)

## Examples

```
/ah search wood
```
Finds all wood-related items (oak wood, birch wood, etc.).

```
/ah search mending
```
Finds all items with the mending enchantment.

```
/ah search netherite
```
Finds all netherite items.

## How Search Works

- **Case Insensitive** - Search is not case-sensitive
- **Partial Matching** - Matches partial words (e.g., "diam" matches "diamond")
- **Multiple Keywords** - All keywords must be present in the item
- **Item Name** - Searches item display names
- **Description** - May search item descriptions if enabled

## Search Results

After executing the search command:
- The auction house GUI opens
- Only matching items are displayed
- You can still use filters and sorting
- Navigation works normally for paginated results

## Alternative Methods

### Main Command Search

You can also search using the main command:
```
/ah diamond sword
```

This is equivalent to:
```
/ah search diamond sword
```

### GUI Search

The auction house GUI also has a search feature:
1. Open the auction house with `/ah`
2. Use the search bar in the GUI
3. Type your keywords
4. Results filter automatically

## Tips

1. **Be Specific** - More specific keywords yield better results
2. **Use Multiple Words** - Combine words for precise searches
3. **Try Variations** - If one search doesn't work, try synonyms
4. **Check Spelling** - Ensure keywords are spelled correctly

## Related Commands

- `/ah` - Main command (also supports search)
- `/ah filter` - Manage filter whitelist for categories

## Notes

- Search results are filtered in real-time
- You can combine search with GUI filters
- Search persists until you clear it or open a new GUI
- Search works across all active listings

