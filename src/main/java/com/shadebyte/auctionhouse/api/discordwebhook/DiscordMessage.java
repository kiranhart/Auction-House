package com.shadebyte.auctionhouse.api.discordwebhook;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/21/2018
 * Time Created: 12:26 PM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import lombok.*;

/**
 * A discord message
 *
 * @author MrPowerGamerBR
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DiscordMessage {
    String username;
    String content;
    @SerializedName("avatar_url")
    String avatarUrl;
    @SerializedName("tts")
    boolean textToSpeech;
    List<DiscordEmbed> embeds = new ArrayList<DiscordEmbed>();

    public DiscordMessage() {

    }

    public DiscordMessage(String username, String content, String avatar_url) {
        this(username, content, avatar_url, false);
    }

    public DiscordMessage(String username, String content, String avatar_url, boolean tts) {
        setUsername(username);
        setContent(content);
        setAvatarUrl(avatar_url);
        setTextToSpeech(tts);
    }

    public void setUsername(String username) {
        if (username != null) {
            this.username = username.substring(0, Math.min(31, username.length()));
        } else {
            this.username = null;
        }
    }

    public static class DiscordMessageBuilder {
        List<DiscordEmbed> embeds = new ArrayList<DiscordEmbed>();

        public DiscordMessageBuilder embed(DiscordEmbed embed) {
            embeds.add(embed);
            return this;
        }
    }
}