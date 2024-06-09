package ca.tweetzy.auctionhouse.impl.category;

import ca.tweetzy.auctionhouse.api.auction.category.CategoryCondition;
import ca.tweetzy.auctionhouse.api.auction.category.CategoryConditionType;
import ca.tweetzy.auctionhouse.api.auction.category.CategoryStringComparison;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class AuctionCategoryCondition implements CategoryCondition {

	private final CategoryConditionType conditionType;
	private final CategoryStringComparison stringComparison;
	private final String value;

	@Override
	public CategoryConditionType getType() {
		return this.conditionType;
	}

	@Override
	public CategoryStringComparison getComparisonType() {
		return this.stringComparison;
	}

	@Override
	public String getValue() {
		return this.value;
	}
}
