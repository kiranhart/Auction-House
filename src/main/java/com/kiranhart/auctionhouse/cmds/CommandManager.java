package com.kiranhart.auctionhouse.cmds;

import com.kiranhart.auctionhouse.Core;
import com.kiranhart.auctionhouse.api.statics.AuctionLang;
import com.kiranhart.auctionhouse.api.statics.AuctionPermissions;
import com.kiranhart.auctionhouse.cmds.subcommands.*;
import com.kiranhart.auctionhouse.inventory.inventories.AuctionGUI;
import com.kiranhart.auctionhouse.util.Debugger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 7/6/2018
 * Time Created: 11:50 AM
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise.
 */
public class CommandManager implements CommandExecutor {

    private List<SubCommand> commands = new ArrayList<>();

    public CommandManager() {
    }

    public final String main = "auctionhouse";

    public final String expired = "expired";
    public final String help = "help";
    public final String listed = "listed";
    public final String reload = "reload";
    public final String sell = "sell";
    public final String transactions = "transactions";
    public final String uploadtransactions = "uploadtransactions";
    public final String lock = "lock";
    public final String endall = "endall";

    public void initialize() {
        Core.getInstance().getCommand(main).setExecutor(this);
        this.commands.add(new SellCommand());
        this.commands.add(new ReloadCommand());
        this.commands.add(new ListedCommand());
        this.commands.add(new ExpiredCommand());
        this.commands.add(new TransactionsCommand());
        this.commands.add(new HelpCommand());
        this.commands.add(new UploadTransactionsCommand());
        this.commands.add(new LockCommand());
        this.commands.add(new EndAllCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission(AuctionPermissions.BASE)) {
            Core.getInstance().getLocale().getMessage(AuctionLang.NO_PERMISSION).sendPrefixedMessage(sender);
            return true;
        }

        //Main command text
        if (command.getName().equalsIgnoreCase(main)) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    p.openInventory(new AuctionGUI(p).getInventory());
                }
                return true;
            }

            //Handle Sub commands
            SubCommand target = this.getSubcommand(args[0]);

            if (target == null) {
                Core.getInstance().getLocale().getMessage(AuctionLang.COMMAND_INVALID).sendPrefixedMessage(sender);
                return true;
            }

            List<String> list = new ArrayList<>();
            list.addAll(Arrays.asList(args));
            list.remove(0);

            try {
                target.onCommand(sender, args);
            } catch (Exception e) {
                Debugger.report(e);
            }
        }

        return true;
    }

    private SubCommand getSubcommand(String name) {
        Iterator<SubCommand> subcommands = this.commands.iterator();
        while (subcommands.hasNext()) {
            SubCommand sc = subcommands.next();

            if (sc.name().equalsIgnoreCase(name)) {
                return sc;
            }

            String[] aliases;
            int length = (aliases = sc.aliases()).length;

            for (int i = 0; i < length; ++i) {
                String alias = aliases[i];
                if (name.equalsIgnoreCase(alias)) {
                    return sc;
                }

            }
        }
        return null;
    }
}