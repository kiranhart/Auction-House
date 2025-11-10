package ca.tweetzy.auctionhouse.model;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Prevents double confirmation actions and spam across GUIs.
 * Thread-safe, self-cleaning, and lightweight.
 */
public final class ConfirmLock {

	// Holds active locks with their expiry times (millis)
	private static final Map<UUID, Long> LOCKS = new ConcurrentHashMap<>();

	private ConfirmLock() {
	}

	/**
	 * Attempts to acquire a temporary lock for the given player.
	 *
	 * @param uuid       The player's UUID.
	 * @param durationMs Lock duration in milliseconds.
	 * @return True if the lock was successfully acquired, false if already locked.
	 */
	public static boolean acquire(UUID uuid, long durationMs) {
		cleanup();

		long now = System.currentTimeMillis();
		long expiry = now + durationMs;

		return LOCKS.compute(uuid, (key, existingExpiry) -> {
			// If an existing lock exists and hasn't expired, reject acquisition
			if (existingExpiry != null && existingExpiry > now)
				return existingExpiry;

			// Otherwise, replace with new expiry
			return expiry;
		}) == expiry;
	}

	/**
	 * Clears the lock for the given player immediately.
	 *
	 * @param uuid The player's UUID.
	 */
	public static void clear(UUID uuid) {
		LOCKS.remove(uuid);
	}

	/**
	 * Simple cleanup method that removes expired locks.
	 * Called automatically on each acquire(), but can also be called manually if desired.
	 */
	public static void cleanup() {
		long now = System.currentTimeMillis();
		LOCKS.entrySet().removeIf(entry -> entry.getValue() <= now);
	}

	/**
	 * Returns whether a player is currently locked.
	 *
	 * @param uuid The player's UUID.
	 * @return True if the player is still under a lock.
	 */
	public static boolean isLocked(UUID uuid) {
		Long expiry = LOCKS.get(uuid);
		return expiry != null && expiry > System.currentTimeMillis();
	}
}
