package ca.tweetzy.auctionhouse.model.manager;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.auction.category.CategoryFieldCondition;
import ca.tweetzy.auctionhouse.api.auction.category.ItemCategory;
import ca.tweetzy.auctionhouse.api.manager.KeyValueManager;
import ca.tweetzy.auctionhouse.impl.category.AuctionItemCategory;
import ca.tweetzy.flight.utils.ChatUtil;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.stream.Collectors;

public final class CategoryManager extends KeyValueManager<String, ItemCategory> {

	private File CATEGORIES_FOLDER;

	public CategoryManager() {
		super("Category");
	}

	@Override
	public void load() {
		clear();
		this.CATEGORIES_FOLDER = new File(AuctionHouse.getInstance().getDataFolder() + "/categories");

		// create category folder
		if (!CATEGORIES_FOLDER.exists())
			CATEGORIES_FOLDER.mkdir();

		File[] categoryFiles = CATEGORIES_FOLDER.listFiles();
		if (categoryFiles == null) return;

		for (File categoryFile : categoryFiles) {
			if (!(categoryFile.isFile() && categoryFile.getName().endsWith(".yml"))) continue;

			final String categoryId = categoryFile.getName().replace(".yml", "").toLowerCase();

			// load yaml file
			final YamlConfiguration ymlFile = YamlConfiguration.loadConfiguration(categoryFile);

			final ItemCategory category = new AuctionItemCategory(categoryId);

			// apply the icon
			if (ymlFile.contains("icon") && ymlFile.isString("icon"))
				category.setIcon(QuickItem.of(ymlFile.getString("icon", "PAPER")).make());

			// apply name
			if (ymlFile.contains("name") && ymlFile.isString("name"))
				category.setName(ymlFile.getString("name", ChatUtil.capitalizeFully(categoryId)));

			// field conditions
			if (ymlFile.contains("field-conditions") && ymlFile.isList("field-conditions")) {
				ymlFile.getStringList("field-conditions").forEach(condition -> {
					final String formattedCondition = condition.toUpperCase();

					try {
						final CategoryFieldCondition fieldCondition = Enum.valueOf(CategoryFieldCondition.class, formattedCondition);
						category.getFieldConditions().add(fieldCondition);

					} catch (IllegalArgumentException ignored) {}
				});
			}

			if (ymlFile.contains("attribute-conditions") && ymlFile.isConfigurationSection("attribute-conditions")) {

			}

			AuctionHouse.getInstance().getLogger().info(String.format(
					"Category: %s, icon: %s, name: %s, Field Conditions: %s",
					categoryId,
					category.getIcon().getType().name(),
					category.getName(),
					category.getFieldConditions().stream().map(Enum::name).collect(Collectors.joining(", "))
			));
		}
	}
}
