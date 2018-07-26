package com.shadebyte.auctionhouse.api.discordwebhook.embed;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProviderEmbed {
	String name;
	String url;
}