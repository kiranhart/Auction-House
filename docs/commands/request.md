# Request Command - `/ah request`

The request command allows you to request specific items you want to buy. Other players can then fulfill your requests by listing those items.

## Syntax

```
/ah request [price]
```

## Description

Item requests let you specify items you want to buy. When you create a request, other players can see it and list items to fulfill your request. This is useful for finding specific items that may not be currently listed.

## Usage

### With GUI

```
/ah request
```

Opens the request GUI where you can:
- Select the item you're holding
- Set the price you're willing to pay
- Configure request details
- Create the request

### With Price

```
/ah request 1000
```

Creates a request for the item in your hand at the price of 1000.

## Permission

- **Permission**: `auctionhouse.cmd.request`
- **Default**: All players (if granted by server admin)

## Creating a Request

1. **Hold the Item** - Hold the item you want to request (or a similar item)
2. **Set Price** - Specify the maximum price you're willing to pay
3. **Create Request** - Confirm the request creation
4. **Wait for Fulfillment** - Other players can now fulfill your request

## Request Requirements

Before creating a request:
- You must be holding an item
- Price must meet minimum/maximum requirements
- You must not exceed request limits
- You must not be banned from requests
- Item must meet listing requirements

## Fulfilling Requests

Other players can:
- See your request in the auction house
- List items to fulfill your request
- You'll be notified when someone fulfills it
- You can purchase the item immediately

## Request GUI Features

The request GUI shows:
- Item preview
- Price input
- Quantity selection
- Request duration
- Create/Cancel buttons

## Managing Requests

You can:
- View your active requests
- Cancel requests (if allowed)
- See if requests have been fulfilled
- Purchase items from fulfilled requests

## When to Use

Use requests when:
- Looking for specific items not currently listed
- Wanting to set a maximum price
- Seeking rare or hard-to-find items
- Wanting to encourage sellers to list specific items

## Related Commands

- `/ah` - Browse requests and fulfill them
- `/ah active` - View your active requests
- `/ah expired` - View expired requests

## Notes

- Requests have expiration times
- Multiple players can fulfill the same request
- You can create multiple requests
- Request limits apply (separate from listing limits)
- Filled shulker boxes cannot be requested (if enabled)

