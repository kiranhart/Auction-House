package ca.tweetzy.auctionhouse.model.manager;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.api.auction.category.*;
import ca.tweetzy.auctionhouse.api.manager.KeyValueManager;
import ca.tweetzy.auctionhouse.impl.category.AuctionCategoryCondition;
import ca.tweetzy.auctionhouse.impl.category.AuctionItemCategory;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.utils.ChatUtil;
import ca.tweetzy.flight.utils.QuickItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

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

			// material list
			if (ymlFile.contains("materials") && ymlFile.isList("materials")) {
				ymlFile.getStringList("materials").forEach(material -> {
					final CompMaterial matchedMaterial = CompMaterial.matchCompMaterial(material).orElse(null);
					if (matchedMaterial == null) return;

					category.getMaterialList().add(matchedMaterial);
				});
			}

			// field conditions
			if (ymlFile.contains("field-conditions") && ymlFile.isList("field-conditions")) {
				ymlFile.getStringList("field-conditions").forEach(condition -> {
					final String formattedCondition = condition.toUpperCase();

					try {
						final CategoryFieldCondition fieldCondition = Enum.valueOf(CategoryFieldCondition.class, formattedCondition);
						category.getFieldConditions().add(fieldCondition);

					} catch (IllegalArgumentException ignored) {
					}
				});
			}

			if (ymlFile.contains("attribute-conditions") && ymlFile.isConfigurationSection("attribute-conditions")) {

				// grab the attrib node
				final ConfigurationSection attributeConditionsNode = ymlFile.getConfigurationSection("attribute-conditions");

				if (attributeConditionsNode != null) {
					// loop through each of the first layer children since those are the condition type -> CategoryConditionType
					attributeConditionsNode.getKeys(false).forEach(attributeNode -> {

						try {
							// if the children name doesn't match any enumeration value skip
							final String formattedAttribute = attributeNode.toUpperCase();
							final CategoryConditionType categoryCondition = Enum.valueOf(CategoryConditionType.class, formattedAttribute);

							// grab the children node then check their children to see the comparisons
							final ConfigurationSection conditionNode = attributeConditionsNode.getConfigurationSection(attributeNode);
							if (conditionNode != null) {

								conditionNode.getKeys(false).forEach(comparisonTypes -> {
									final CategoryStringComparison stringComparison = Enum.valueOf(CategoryStringComparison.class, comparisonTypes.toUpperCase());

									final String valuePath = String.format("attribute-conditions.%s.%s", categoryCondition.name(), stringComparison.name());
									if (!ymlFile.isSet(valuePath)) return;

									// add the values now
									if (ymlFile.isList(valuePath)) {

										ymlFile.getStringList(valuePath).forEach(value -> category.getConditions().add(new AuctionCategoryCondition(
												categoryCondition,
												stringComparison,
												value
										)));

									} else if (ymlFile.isString(valuePath)) {
										category.getConditions().add(new AuctionCategoryCondition(
												categoryCondition,
												stringComparison,
												ymlFile.getString(valuePath)
										));
									}
								});

							}

						} catch (IllegalArgumentException ignored) {
						}
					});
				}
			}
		}
	}
}
