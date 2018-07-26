package com.shadebyte.auctionhouse.api.discordwebhook;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.util.Debugger;
import org.bukkit.Bukkit;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/21/2018
 * Time Created: 12:28 PM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class DiscordHook {

    public static final Gson gson = new Gson();
    public String url;

    public DiscordHook(String url) {
        this.url = url;
    }

    public void send(DiscordMessage dm) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(Core.getInstance(), ()-> {
            String strResponse = HttpRequest.post(url).acceptJson().contentType("application/json").header("User-Agent", "Mozilla/5.0 (X11; U; Linux i686) Gecko/20071127 Firefox/2.0.0.11").send(gson.toJson(dm)).body();
            if(!strResponse.isEmpty()) {
                Response response = gson.fromJson(strResponse, Response.class);
                try {
                    if (response.getMessage().equals("You are being rate limited.")) {
                        Bukkit.getConsoleSender().sendMessage("Error");
                    }
                } catch (Exception e) {
                    Debugger.report(e);
                }
            }
        });
    }
}
