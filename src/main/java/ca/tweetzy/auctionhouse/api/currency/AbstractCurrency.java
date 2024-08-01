package ca.tweetzy.auctionhouse.api.currency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public abstract class AbstractCurrency implements Chargeable {

	protected String owningPlugin;
	protected String currencyName;

	@Setter
	protected String displayName;

	public String getStoreableName() {
		return this.owningPlugin + "/" + this.currencyName + "/" + this.displayName;
	}
}
