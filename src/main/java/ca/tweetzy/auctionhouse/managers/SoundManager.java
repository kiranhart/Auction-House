package ca.tweetzy.auctionhouse.managers;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.core.compatibility.XSound;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 11 2021
 * Time Created: 3:30 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class SoundManager {

	private static SoundManager instance;

	public static SoundManager getInstance() {
		if (instance == null) {
			instance = new SoundManager();
		}
		return instance;
	}

	private SoundManager() {
	}

	public void playSound(Player player, String sound, float volume, float pitch) {
		player.playSound(player.getLocation(), XSound.matchXSound(sound).get().parseSound(), volume, pitch);
	}

	public void playSound(Player[] players, String sound, float volume, float pitch) {
		final Sound xsound = XSound.matchXSound(sound).get().parseSound();
		Arrays.stream(players).forEach(p -> p.playSound(p.getLocation(), xsound, volume, pitch));
	}

	public void playSound(Player player, String sound, float volume, float pitch, int delay) {
		Bukkit.getServer().getScheduler().runTaskLater(AuctionHouse.getInstance(), () -> playSound(player, sound, volume, pitch), delay);
	}
}
