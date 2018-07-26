package com.shadebyte.auctionhouse.api.discordwebhook;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/21/2018
 * Time Created: 12:20 PM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */

import com.shadebyte.auctionhouse.api.discordwebhook.embed.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A discord embed
 *
 * @author MrPowerGamerBR
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class DiscordEmbed {
    String title;
    String type;
    String description;
    String url;
    String timestamp;
    int color;
    ImageEmbed image;
    ThumbnailEmbed thumbnail;
    ProviderEmbed provider;
    AuthorEmbed author;
    List<FieldEmbed> fields = new ArrayList<FieldEmbed>();

    public DiscordEmbed() {

    }

    public DiscordEmbed(String title, String description) {
        this(title, description, null);
    }

    public DiscordEmbed(String title, String description, String url) {
        setTitle(title);
        setDescription(description);
        setUrl(url);
    }

    public static DiscordMessage toDiscordMessage(DiscordEmbed embed, String username, String avatarUrl) {
        DiscordMessage dm = DiscordMessage.builder()
                .username(username)
                .avatarUrl(avatarUrl)
                .content("")
                .embed(embed)
                .build();

        return dm;
    }

    public DiscordMessage toDiscordMessage(String username, String avatarUrl) {
        return DiscordEmbed.toDiscordMessage(this, username, avatarUrl);
    }

    public static class DiscordEmbedBuilder {
        List<FieldEmbed> fields = new ArrayList<FieldEmbed>();

        public DiscordEmbedBuilder field(FieldEmbed field) {
            fields.add(field);
            return this;
        }
    }
}