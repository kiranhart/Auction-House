package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.auctionhouse.guis.GUISellItem;
import ca.tweetzy.auctionhouse.guis.admin.GUIAdminLogs;
import ca.tweetzy.auctionhouse.helpers.PlayerHelper;
import ca.tweetzy.auctionhouse.settings.Settings;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.CompatibleHand;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 23 2021
 * Time Created: 12:14 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandAdmin extends AbstractCommand {

	public CommandAdmin() {
		super(CommandType.CONSOLE_OK, "admin");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		if (args.length < 1) return ReturnType.FAILURE;
		if (AuctionAPI.tellMigrationStatus(sender)) return ReturnType.FAILURE;

		switch (args[0].toLowerCase()) {
			case "logs":
				if (!(sender instanceof Player)) break;
				Player player = (Player) sender;

				AuctionHouse.getInstance().getDataManager().getAdminLogs((error, logs) -> {
					if (error == null)
						AuctionHouse.newChain().sync(() -> AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUIAdminLogs(player, logs))).execute();
					else
						error.printStackTrace();
				});
				break;
			case "endall":
				for (UUID id : AuctionHouse.getInstance().getAuctionItemManager().getItems().keySet()) {
					AuctionHouse.getInstance().getAuctionItemManager().getItems().get(id).setExpired(true);
				}
				AuctionHouse.getInstance().getLocale().getMessage("general.endedallauctions").sendPrefixedMessage(sender);
				break;
			case "relistall":
				for (UUID id : AuctionHouse.getInstance().getAuctionItemManager().getItems().keySet()) {
					if (AuctionHouse.getInstance().getAuctionItemManager().getItems().get(id).isExpired()) {
						int relistTime = args.length == 1 ? AuctionHouse.getInstance().getAuctionItemManager().getItems().get(id).isBidItem() ? Settings.DEFAULT_AUCTION_LISTING_TIME.getInt() : Settings.DEFAULT_BIN_LISTING_TIME.getInt() : Integer.parseInt(args[1]);

						AuctionHouse.getInstance().getAuctionItemManager().getItems().get(id).setExpiresAt(System.currentTimeMillis() + 1000L * relistTime);
						AuctionHouse.getInstance().getAuctionItemManager().getItems().get(id).setExpired(false);
					}
				}
				AuctionHouse.getInstance().getLocale().getMessage("general.relisteditems").sendPrefixedMessage(sender);
				break;
			case "clearall":
				// Don't tell ppl that this exists
				AuctionHouse.getInstance().getAuctionItemManager().getItems().clear();
			case "durabilitystatus":
				Bukkit.broadcastMessage("damaged: " + AuctionAPI.getInstance().isDamaged(PlayerHelper.getHeldItem((Player) sender)));
				break;
			case "repairstatus":
				Bukkit.broadcastMessage("repair: " + AuctionAPI.getInstance().isRepaired(PlayerHelper.getHeldItem((Player) sender)));
				break;
			case "opensell":
				if (args.length < 2) return ReturnType.FAILURE;
				player = PlayerUtils.findPlayer(args[1]);
				if (player == null) return ReturnType.FAILURE;

				ItemStack itemToSell = PlayerHelper.getHeldItem(player).clone();

				if (itemToSell.getType() == XMaterial.AIR.parseMaterial() && Settings.SELL_MENU_REQUIRES_USER_TO_HOLD_ITEM.getBoolean()) {
					AuctionHouse.getInstance().getLocale().getMessage("general.air").sendPrefixedMessage(player);
					return ReturnType.FAILURE;
				} else {
					AuctionHouse.getInstance().getGuiManager().showGUI(player, new GUISellItem(AuctionHouse.getInstance().getAuctionPlayerManager().getPlayer(player.getUniqueId()), itemToSell));
					AuctionHouse.getInstance().getAuctionPlayerManager().addItemToSellHolding(player.getUniqueId(), itemToSell);
					PlayerUtils.takeActiveItem(player, CompatibleHand.MAIN_HAND, itemToSell.getAmount());
				}
				break;
		}

		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		if (args.length == 1) return Arrays.asList("endall", "relistall", "logs");
		if (args.length == 2 && args[0].equalsIgnoreCase("relistAll")) return Arrays.asList("1", "2", "3", "4", "5");
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.admin";
	}

	@Override
	public String getSyntax() {
		return "admin <endall|relistAll> [value]";
	}

	@Override
	public String getDescription() {
		return "Admin options for auction house.";
	}
}
