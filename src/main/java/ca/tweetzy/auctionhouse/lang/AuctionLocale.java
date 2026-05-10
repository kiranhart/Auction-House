package ca.tweetzy.auctionhouse.lang;

import ca.tweetzy.auctionhouse.settings.Translations;
import ca.tweetzy.flight.settings.TranslationEntry;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves Auction House locale strings via Flight {@link TranslationManager} and entries in {@link Translations}.
 */
public final class AuctionLocale {

	private static final ConcurrentHashMap<String, TranslationEntry> BY_KEY = new ConcurrentHashMap<>();

	static {
		for (Field field : Translations.class.getFields()) {
			if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			if (!TranslationEntry.class.isAssignableFrom(field.getType())) {
				continue;
			}
			try {
				TranslationEntry entry = (TranslationEntry) field.get(null);
				if (entry != null) {
					BY_KEY.put(entry.getKey().toLowerCase(Locale.ROOT), entry);
				}
			} catch (ReflectiveOperationException ignored) {
			}
		}
	}

	private AuctionLocale() {
	}

	private static TranslationEntry resolve(@NonNull String key) {
		TranslationEntry entry = BY_KEY.get(key.toLowerCase(Locale.ROOT));
		if (entry == null) {
			throw new IllegalStateException("Missing Translations registry for locale key: " + key);
		}
		return entry;
	}

	public static String msg(@Nullable Player player, @NonNull String key, Object... pairs) {
		return TranslationManager.string(player, resolve(key), pairs);
	}

	public static String msg(@Nullable Player player, @NonNull String key, @NonNull Map<String, String> placeholders) {
		Object[] pairs = new Object[placeholders.size() * 2];
		int ix = 0;
		for (Map.Entry<String, String> e : placeholders.entrySet()) {
			pairs[ix++] = e.getKey();
			pairs[ix++] = e.getValue();
		}
		return TranslationManager.string(player, resolve(key), pairs);
	}

	public static List<String> msgList(@Nullable Player player, @NonNull String key, Object... pairs) {
		return TranslationManager.list(player, resolve(key), pairs);
	}

	public static void tell(@NonNull CommandSender sender, @NonNull String key, Object... pairs) {
		Player p = sender instanceof Player player ? player : null;
		Common.tell(sender, msg(p, key, pairs));
	}

	public static void tell(@NonNull CommandSender sender, @NonNull String key, @NonNull Map<String, String> placeholders) {
		Player p = sender instanceof Player player ? player : null;
		Common.tell(sender, msg(p, key, placeholders));
	}
}
