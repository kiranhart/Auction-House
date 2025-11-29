# Selling GUIs

The selling GUIs guide you through the process of listing items for sale on the auction house.

## GUI Flow

The selling process uses multiple GUIs:

1. **Listing Type Selection** - Choose Auction or BIN
2. **Item Placement** - Configure your listing
3. **Auction Configuration** - Set auction parameters (if auction)
4. **BIN Configuration** - Set BIN price (if BIN)
5. **Bundle Creation** - Create bundles (if enabled)
6. **Confirmation** - Confirm your listing

## Listing Type Selection GUI

Accessed via `/ah sell` (if type selection is enabled).

### Options
- **Auction Button**: Create an auction listing
- **BIN Button**: Create a Buy It Now listing
- **Return Button**: Go back to main auction house

### When Shown
- If both auction and BIN are enabled
- If type selection is not skipped in settings

## Item Placement GUI

The main GUI for configuring your listing.

### Features
- **Item Preview**: Shows the item you're listing
- **Price Input**: Set your listing price
- **Time Selection**: Choose listing duration
- **Options**: Configure listing options

### Configuration Options

#### For BIN Listings
- **Buy It Now Price**: Set the fixed price
- **Listing Time**: How long the listing lasts
- **Currency**: Select currency (if multiple enabled)

#### For Auction Listings
- **Starting Bid**: Minimum bid amount
- **Buy It Now Price**: Optional instant buy price
- **Bid Increment**: Minimum bid increase
- **Listing Time**: Auction duration

### Advanced Options
- **Bundle**: Create a bundle listing
- **Partial Buy**: Allow partial purchases
- **Single Item**: Sell one item from stack
- **Stack Price**: Price for entire stack
- **Infinite**: Never expires (admin only)
- **Server Auction**: Server-owned listing (admin only)

## Auction Configuration GUI

Specific GUI for auction listings (if separate GUI is used).

### Settings
- **Starting Bid**: Initial bid amount
- **Bid Increment**: Minimum bid increase
- **Buy It Now**: Optional instant buy price
- **Duration**: Auction length

## BIN Configuration GUI

Specific GUI for BIN listings (if separate GUI is used).

### Settings
- **Price**: Fixed purchase price
- **Duration**: Listing length
- **Currency**: Payment currency

## Bundle Creation GUI

Appears when creating bundle listings.

### Features
- **Bundle Preview**: See all items in bundle
- **Add Items**: Add more items to bundle
- **Remove Items**: Remove items from bundle
- **Bundle Price**: Set price for entire bundle
- **Confirm**: Create the bundle listing

## Confirmation GUI

Final step before listing your item.

### Information Shown
- **Item Details**: Full item information
- **Price Information**: All pricing details
- **Time Information**: Listing duration
- **Options**: All selected options

### Actions
- **Confirm**: Create the listing
- **Cancel**: Cancel and return item
- **Edit**: Go back and modify

## GUI Features

### Price Validation
- **Minimum Price**: Must meet minimum requirements
- **Maximum Price**: Cannot exceed maximum
- **Item-Specific Limits**: Must meet item price limits
- **Real-time Validation**: Errors shown immediately

### Time Selection
- **Preset Times**: Quick time selection buttons
- **Custom Time**: Set custom duration (if enabled)
- **Time Limits**: Maximum time restrictions apply

### Currency Selection
- **Default Currency**: Uses server default
- **Multiple Currencies**: Select from available currencies
- **Currency Display**: Shows currency in price

## Tips

1. **Check Price Limits** - Ensure your price is within limits
2. **Set Appropriate Time** - Don't set too short or too long
3. **Use Bundle Feature** - Bundle similar items together
4. **Preview Before Listing** - Review all details
5. **Consider Buy It Now** - Add BIN to auctions for faster sales

## Related Commands

- `/ah sell` - Open selling GUI
- `/ah active` - View your listings
- `/ah expired` - View expired listings

## Related GUIs

- [Main Auction House GUI](main-auction-house.md) - Browse auctions
- [Active Auctions GUI](active-auctions.md) - Your listings
- [Confirmation GUIs](confirmations.md) - Confirm actions

