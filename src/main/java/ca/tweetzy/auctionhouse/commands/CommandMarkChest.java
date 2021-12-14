package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.AuctionAPI;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.ServerVersion;
import ca.tweetzy.core.compatibility.XMaterial;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: December 04 2021
 * Time Created: 11:30 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class CommandMarkChest extends AbstractCommand {

	public CommandMarkChest() {
		super(CommandType.PLAYER_ONLY, "markchest");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)) return ReturnType.FAILURE;
		final Player player = (Player) sender;

		if (CommandMiddleware.handle(player) == ReturnType.FAILURE) return ReturnType.FAILURE;

		final Block targetBlock = player.getTargetBlock(null, 10);
		if (targetBlock.getType() != XMaterial.CHEST.parseMaterial()) return ReturnType.FAILURE;

		final Chest chest = (Chest) targetBlock.getState();
		final NamespacedKey key = new NamespacedKey(AuctionHouse.getInstance(), "AuctionHouseMarkedChest");

		if (chest.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
			chest.getPersistentDataContainer().remove(key);
			chest.update(true);
			AuctionHouse.getInstance().getLocale().getMessage("general.unmarked chest").sendPrefixedMessage(player);
		} else {
			chest.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
			chest.update(true);
			AuctionHouse.getInstance().getLocale().getMessage("general.marked chest").sendPrefixedMessage(player);
		}

		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "auctionhouse.cmd.markchest";
	}

	@Override
	public String getSyntax() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.syntax.markchest").getMessage();
	}

	@Override
	public String getDescription() {
		return AuctionHouse.getInstance().getLocale().getMessage("commands.description.markchest").getMessage();
	}
}
