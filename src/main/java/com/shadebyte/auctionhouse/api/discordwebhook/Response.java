package com.shadebyte.auctionhouse.api.discordwebhook;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

/**
 * Discord response
 * 
 * @author MrPowerGamerBR
 */
@Getter
public class Response {
	boolean global;
	String message;
	@SerializedName("retry_after")
	int retryAfter;
}