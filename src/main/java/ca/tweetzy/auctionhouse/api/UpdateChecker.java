package ca.tweetzy.auctionhouse.api;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 17 2021
 * Time Created: 9:02 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class UpdateChecker {

    public enum UpdateStatus {
        UNKNOWN, ERROR, UP_TO_DATE, UPDATE_AVAILABLE, UNRELEASED_VERSION
    }

    private UpdateStatus status = UpdateStatus.UNKNOWN;
    final String API_URL = "https://api.spigotmc.org/legacy/update.php?resource=%d";

    final JavaPlugin plugin;
    final int SPIGOT_ID;
    final CommandSender[] to;
    private String latestVersion = "0.0.0";

    private String getLatestVersionFromSpigot() {
        String version = "0.0.0";
        try {
            URL url = new URL(String.format(API_URL, SPIGOT_ID));
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "text/plain");
            connection.setRequestProperty("User-Agent", "Tweetzy Plugin Update Checker");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            StringBuilder content = null;

            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                content = new StringBuilder();
                String input;
                while ((input = reader.readLine()) != null) {
                    content.append(input);
                }
                reader.close();
            }

            connection.disconnect();

            if (content != null) {
                version = content.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return version;
    }

    public UpdateChecker check() {
        latestVersion = getLatestVersionFromSpigot();
        int[] latest = Arrays.stream(latestVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
        int[] current = Arrays.stream(plugin.getDescription().getVersion().split("\\.")).mapToInt(Integer::parseInt).toArray();

        if (latest.length != 3 || current.length != 3) {
            status = UpdateStatus.ERROR;
            return this;
        }

        if (latest[0] == current[0] || latest[1] == current[1] || latest[2] == current[2])
            status = UpdateStatus.UP_TO_DATE;
        if (latest[0] > current[0] || latest[1] > current[1] || latest[2] > current[2])
            status = UpdateStatus.UPDATE_AVAILABLE;
        if (latest[0] < current[0] || latest[1] < current[1] || latest[2] < current[2])
            status = UpdateStatus.UNRELEASED_VERSION;

        for (CommandSender sender : to) {
            switch (status) {
                case UP_TO_DATE:
                    AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&aIs running the latest version!")).sendPrefixedMessage(sender);
                    break;
                case UPDATE_AVAILABLE:
                    AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&dA new update is available for Auction House")).sendPrefixedMessage(sender);
                    break;
                case UNRELEASED_VERSION:
                    AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText(String.format("&dYou're running an unreleased version of Auction House &f(&c%s&f)", AuctionHouse.getInstance().getDescription().getVersion()))).sendPrefixedMessage(sender);
                    break;
                case UNKNOWN:
                    AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cUnknown version is in use")).sendPrefixedMessage(sender);
                    break;
                case ERROR:
                    AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&cAn error has occurred while trying to get the latest version")).sendPrefixedMessage(sender);
                    break;
            }
        }

        return this;
    }

    public UpdateChecker(JavaPlugin plugin, int spigotID, CommandSender... to) {
        this.plugin = plugin;
        this.SPIGOT_ID = spigotID;
        this.to = to;
    }

    public UpdateStatus getStatus() {
        return status;
    }

    public boolean isUpdateToDate() {
        return getStatus() == UpdateStatus.UP_TO_DATE;
    }

    public String getLatestVersion() {
        return latestVersion;
    }
}
