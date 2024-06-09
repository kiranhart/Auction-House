package ca.tweetzy.auctionhouse.api.auction.category;

public interface CategoryCondition {

	CategoryConditionType getType();

	CategoryStringComparison getComparisonType();

	String getValue();
}
