# Cart Command - `/ah cart`

The cart command opens your shopping cart where you can view items you've added for purchase.

## Syntax

```
/ah cart
```

## Description

The shopping cart allows you to add multiple items before purchasing them all at once. This is useful for buying multiple items efficiently without going through the purchase process for each item individually.

## Usage

Simply type:
```
/ah cart
```

This opens the Shopping Cart GUI showing:
- All items you've added to your cart
- Total cost of all items
- Individual item prices
- Options to purchase all items or remove items

## Permission

- **Permission**: `auctionhouse.cmd.cart`
- **Default**: All players (if granted by server admin)

## GUI Features

The Shopping Cart GUI provides:

### Cart Display
- Visual list of all cart items
- Individual item prices
- Total cart value
- Item quantities

### Actions Available
- **Purchase All** - Buy all items in cart at once
- **Remove Item** - Remove items from cart
- **View Item** - View details of a cart item
- **Clear Cart** - Remove all items from cart

### Information Displayed
- Item name and details
- Individual prices
- Total cost
- Seller information
- Quantity of each item

## Adding Items to Cart

To add items to your cart:
1. Browse the auction house with `/ah`
2. Click on an item you want to buy
3. Instead of "Buy Now", click "Add to Cart"
4. Item is added to your cart
5. Repeat for more items
6. Use `/ah cart` to view and purchase all items

## Purchasing from Cart

When you're ready to purchase:
1. Open your cart with `/ah cart`
2. Review all items and total cost
3. Ensure you have enough money
4. Click "Purchase All"
5. All items are purchased at once
6. Items are delivered to your inventory

## When to Use

Use the cart feature when:
- Buying multiple items at once
- Comparing prices before purchasing
- Organizing purchases
- Ensuring you have enough money for all items

## Related Commands

- `/ah` - Browse auctions and add items to cart
- `/ah active` - View your purchases

## Notes

- Cart items may expire if the auction expires
- You must have enough money for all items
- Some items may sell before you purchase (if someone else buys them)
- Cart is cleared after purchase
- Cart persists across sessions

