package ca.tweetzy.auctionhouse.commands;

import ca.tweetzy.auctionhouse.AuctionHouse;
import ca.tweetzy.auctionhouse.auction.AuctionItem;
import ca.tweetzy.auctionhouse.auction.AuctionItemCategory;
import ca.tweetzy.auctionhouse.helpers.MaterialCategorizer;
import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: March 29 2021
 * Time Created: 3:56 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandConvert extends AbstractCommand {

    public CommandConvert() {
        super(CommandType.CONSOLE_OK, "convert");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        AuctionHouse.getInstance().getLocale().newMessage(TextUtils.formatText("&4Beginning the ATTEMPTED conversion process, this may take some time.")).sendPrefixedMessage(sender);
        long start = System.currentTimeMillis();
        Bukkit.getServer().getScheduler().runTaskLater(AuctionHouse.getInstance(), () -> {
            ConfigurationSection activeSection = AuctionHouse.getInstance().getData().getConfigurationSection("active");
            ConfigurationSection expiredSection = AuctionHouse.getInstance().getData().getConfigurationSection("expired");

            if (activeSection != null && activeSection.getKeys(false).size() != 0) {
                activeSection.getKeys(false).forEach(key -> AuctionHouse.getInstance().getAuctionItemManager().addItem(new AuctionItem(
                        UUID.fromString(Objects.requireNonNull(AuctionHouse.getInstance().getData().getString("active." + key + ".owner"))),
                        UUID.fromString(Objects.requireNonNull(AuctionHouse.getInstance().getData().getString("active." + key + ".highestbidder"))),
                        AuctionHouse.getInstance().getData().getItemStack("active." + key + ".item"),
                        MaterialCategorizer.getMaterialCategory(Objects.requireNonNull(AuctionHouse.getInstance().getData().getItemStack("active." + key + ".item"))),
                        UUID.fromString(Objects.requireNonNull(AuctionHouse.getInstance().getData().getString("active." + key + ".key"))),
                        AuctionHouse.getInstance().getData().getDouble("active." + key + ".buynowprice"),
                        AuctionHouse.getInstance().getData().getDouble("active." + key + ".startprice"),
                        AuctionHouse.getInstance().getData().getDouble("active." + key + ".bidincrement"),
                        AuctionHouse.getInstance().getData().getDouble("active." + key + ".currentprice"),
                        AuctionHouse.getInstance().getData().getInt("active." + key + ".time"),
                        false
                )));
                AuctionHouse.getInstance().getData().set("active", null);
            }

            if (expiredSection != null && expiredSection.getKeys(false).size() != 0) {
                expiredSection.getKeys(false).forEach(expiredItemOwner -> {
                    if (AuctionHouse.getInstance().getData().getConfigurationSection("expired." + expiredItemOwner) != null && AuctionHouse.getInstance().getData().getConfigurationSection("expired." + expiredItemOwner).getKeys(false).size() != 0) {
                        AuctionHouse.getInstance().getData().getConfigurationSection("expired." + expiredItemOwner).getKeys(false).forEach(ownedItem -> AuctionHouse.getInstance().getAuctionItemManager().addItem(new AuctionItem(
                                UUID.fromString(expiredItemOwner),
                                UUID.fromString(expiredItemOwner),
                                AuctionHouse.getInstance().getData().getItemStack("expired." + expiredItemOwner + "." + ownedItem + ".item"),
                                AuctionItemCategory.ALL,
                                UUID.fromString(ownedItem),
                                1D,
                                1D,
                                1D,
                                1D,
                                0,
                                true
                        )));
                    }
                });
                AuctionHouse.getInstance().getData().set("expired", null);
            }

            AuctionHouse.getInstance().getLocale().newMessage("&aFinished Conversion Process (" + (System.currentTimeMillis() - start) + "ms)").sendPrefixedMessage(sender);
            AuctionHouse.getInstance().getData().save();

        }, 1L);

        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "auctionhouse.cmd.convert";
    }

    @Override
    public String getSyntax() {
        return "convert";
    }

    @Override
    public String getDescription() {
        return "Used to make an attempted conversion from < 2.0.0+";
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
