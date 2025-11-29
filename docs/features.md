# Features

Auction House provides a comprehensive set of features for managing auctions on your Minecraft server. This document explains all major features in detail.

## Bidding System

The bidding system allows players to bid on auction listings, creating competitive auctions.

### How It Works
1. **Seller Creates Auction** - Seller lists item with starting bid
2. **Players Bid** - Players place bids on the item
3. **Highest Bidder Wins** - Player with highest bid when auction ends wins
4. **Automatic Delivery** - Item and payment are processed automatically

### Features
- **Starting Bid** - Minimum initial bid amount
- **Bid Increment** - Minimum amount bids must increase
- **Buy It Now** - Optional instant purchase price
- **Bid History** - View all bids placed
- **Outbid Notifications** - Notified when outbid
- **Automatic Processing** - Automatic winner selection

### Money Handling
- **Bid Holding** - Money may be held when bidding (configurable)
- **Outbid Returns** - Money returned when outbid
- **Winner Payment** - Payment processed when auction ends
- **Seller Payment** - Seller receives payment automatically

## Buy It Now (BIN) System

Buy It Now allows instant purchase of items without bidding.

### How It Works
1. **Seller Sets Price** - Seller sets fixed price
2. **Player Buys** - Player purchases immediately
3. **Instant Transfer** - Item and payment transfer instantly
4. **No Bidding** - No bidding required

### Features
- **Fixed Price** - Set price, no negotiation
- **Instant Purchase** - Buy immediately
- **No Waiting** - No need to wait for auction end
- **Simple Process** - Straightforward buying process

### Use Cases
- Quick sales for sellers
- Immediate purchases for buyers
- Fixed price items
- Common items

## Item Bundles

Item bundles allow selling multiple items together as a single listing.

### How It Works
1. **Hold Base Item** - Hold one item of the type
2. **Use Bundle Flag** - Use bundle flag in sell command
3. **Auto-Bundling** - Similar items from inventory are bundled
4. **List as One** - Bundle listed as single item

### Features
- **Automatic Bundling** - Finds similar items automatically
- **Bundle Preview** - Preview bundle before listing
- **Single Price** - One price for entire bundle
- **Bundle Limits** - Separate limits for bundles

### Benefits
- Sell multiple items at once
- Convenient for bulk sales
- Attractive to buyers
- Efficient listing

## Item Requests

Item requests allow players to request specific items they want to buy.

### How It Works
1. **Create Request** - Player creates request with item and price
2. **Request Display** - Request appears in auction house
3. **Fulfillment** - Other players list items to fulfill request
4. **Purchase** - Requester can buy fulfilled items

### Features
- **Item Specification** - Request specific items
- **Price Setting** - Set maximum price willing to pay
- **Fulfillment System** - Players can fulfill requests
- **Notifications** - Notified when request fulfilled

### Use Cases
- Finding rare items
- Setting price expectations
- Encouraging sellers
- Market demand indication

## Shopping Cart

The shopping cart allows adding multiple items before purchasing.

### How It Works
1. **Add Items** - Add items to cart while browsing
2. **Review Cart** - View all items in cart
3. **Checkout** - Purchase all items at once
4. **Receive Items** - All items delivered together

### Features
- **Multiple Items** - Add many items to cart
- **Total Cost** - See total cost of all items
- **Bulk Purchase** - Buy everything at once
- **Cart Persistence** - Cart saves across sessions

### Benefits
- Convenient bulk buying
- See total cost before buying
- Organize purchases
- Efficient shopping

## Price Limits

Price limits control minimum and maximum prices for items.

### How It Works
1. **Admin Sets Limits** - Admin sets limits for item types
2. **Validation** - System validates prices when listing
3. **Enforcement** - Prices must be within limits
4. **Item-Specific** - Different limits for different items

### Features
- **Minimum Prices** - Prevent underpricing
- **Maximum Prices** - Prevent overpricing
- **Item-Specific** - Limits per item type
- **Admin Control** - Admin manages limits

### Benefits
- Economy control
- Prevent price manipulation
- Fair pricing
- Market stability

## Filtering System

The filtering system allows players to filter auction listings.

### Filter Types
- **Category Filters** - Filter by item category
- **Price Filters** - Filter by price range
- **Seller Filters** - Filter by seller
- **Material Filters** - Filter by material
- **Enchantment Filters** - Filter by enchantments
- **Custom Filters** - User-created filters

### Features
- **Multiple Filters** - Combine multiple filters
- **Filter Persistence** - Filters may persist
- **Quick Filters** - Quick filter buttons
- **Filter Whitelist** - Whitelist specific items

### Benefits
- Find items faster
- Narrow down results
- Personalized views
- Efficient browsing

## Statistics Tracking

Statistics track all auction house activity.

### Tracked Statistics
- **Sales** - Items sold, sale prices
- **Purchases** - Items bought, purchase prices
- **Bids** - Bids placed, bid amounts
- **Earnings** - Money earned from sales
- **Spending** - Money spent on purchases
- **Activity** - Overall activity levels

### Features
- **Player Statistics** - Individual player stats
- **Server Statistics** - Overall server stats
- **Leaderboards** - Rankings and leaderboards
- **Historical Data** - Track over time

### Benefits
- Track performance
- Monitor activity
- Compare with others
- Set goals

## Payment Collection

Payment collection manages payments from sold items.

### Collection Modes
- **Automatic** - Payments added directly to balance
- **Manual** - Must collect payments manually
- **Hybrid** - Combination of both

### Features
- **Payment Storage** - Payments stored until collected
- **Payment History** - Track all payments
- **Bulk Collection** - Collect all at once
- **Payment Reasons** - Different payment types

### Benefits
- Control over payments
- Payment tracking
- Flexible collection
- Payment history

## Transaction History

Transaction history records all auction house activity.

### Transaction Types
- **Purchases** - Items purchased
- **Sales** - Items sold
- **Bids** - Bids placed
- **Payments** - Payments received
- **Admin Actions** - Admin modifications

### Features
- **Complete History** - All activity recorded
- **Filtering** - Filter by type, date, player
- **Search** - Search transactions
- **Export** - Export transaction data

### Benefits
- Complete records
- Audit trail
- Activity tracking
- Data analysis

## Admin Features

Comprehensive admin tools for managing the auction house.

### Admin Tools
- **View Logs** - See all admin actions
- **Manage Auctions** - End, relist, clear auctions
- **Player Management** - Clear player auctions/bids
- **Ban Management** - Ban/unban players
- **Price Limits** - Set price limits
- **System Control** - System-wide controls

### Features
- **Action Logging** - All actions logged
- **Player Tools** - Manage individual players
- **Bulk Actions** - Actions on multiple items
- **Safety Features** - Confirmations and warnings

### Benefits
- Full control
- Easy management
- Security
- System stability

## Multi-Currency Support

Support for multiple economy systems and currencies.

### Supported Currencies
- **Vault** - Standard economy integration
- **PlayerPoints** - PlayerPoints currency
- **UltraEconomy** - UltraEconomy integration
- **Custom Currencies** - Custom currency support

### Features
- **Currency Selection** - Choose currency per listing
- **Currency Display** - Shows currency type
- **Currency Conversion** - May support conversion
- **Default Currency** - Server default currency

### Benefits
- Flexible economy
- Multiple payment methods
- Server customization
- Player choice

## Multi-World Support

Different auction houses per world.

### Features
- **World Separation** - Separate auctions per world
- **World-Specific** - Items listed in specific world
- **Cross-World** - May support cross-world (configurable)
- **World Settings** - Different settings per world

### Benefits
- World organization
- Server structure
- Player organization
- Flexible setup

## Auto-Refresh

Automatic GUI updates for real-time information.

### Features
- **Real-Time Updates** - GUIs update automatically
- **Price Updates** - Bid prices update in real-time
- **New Listings** - New listings appear automatically
- **Status Updates** - Status changes update immediately

### Benefits
- Current information
- No manual refresh
- Better user experience
- Real-time data

## Search Functionality

Comprehensive search for finding items.

### Search Features
- **Keyword Search** - Search by keywords
- **Item Name Search** - Search item names
- **Description Search** - Search descriptions
- **Real-Time Filtering** - Results update as you type

### Benefits
- Find items quickly
- Efficient searching
- Better user experience
- Time saving

## Confirmation System

Safety confirmations for important actions.

### Confirmation Types
- **Purchase Confirmations** - Confirm purchases
- **Bid Confirmations** - Confirm bids
- **Listing Confirmations** - Confirm listings
- **Cancel Confirmations** - Confirm cancellations

### Benefits
- Prevent mistakes
- Safety checks
- Review before action
- User confidence

## Related Documentation

- [Commands](commands.md) - Commands for using features
- [GUIs](guis.md) - GUI interfaces
- [Permissions](permissions.md) - Feature permissions
- [Getting Started](getting-started.md) - Basic usage

