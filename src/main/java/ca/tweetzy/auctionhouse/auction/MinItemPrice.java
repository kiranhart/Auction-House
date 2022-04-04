package ca.tweetzy.auctionhouse.auction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Date Created: April 04 2022
 * Time Created: 8:30 a.m.
 *
 * @author Kiran Hart
 */
@AllArgsConstructor
@Getter
public final class MinItemPrice {

	private UUID uuid;
	private ItemStack itemStack;
	private double price;

	public MinItemPrice(ItemStack itemStack, double price) {
		this(UUID.randomUUID(), itemStack, price);
	}
}
