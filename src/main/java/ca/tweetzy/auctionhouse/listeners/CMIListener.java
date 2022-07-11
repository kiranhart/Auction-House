package ca.tweetzy.auctionhouse.listeners;

import ca.tweetzy.core.utils.nms.NBTEditor;
import com.Zrips.CMI.events.CMIAnvilItemRepairEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * The current file has been created by Kiran Hart
 * Date Created: January 11 2022
 * Time Created: 11:39 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class CMIListener implements Listener {

	@EventHandler
	public void onCMIRepair(CMIAnvilItemRepairEvent event) {
		ItemStack stack = event.getItemTo();
		if (stack == null) return;

		stack = NBTEditor.set(stack, "AUCTION_REPAIRED", "AuctionHouseRepaired");
		event.setItemTo(stack);
	}
}
