package com.shadebyte.auctionhouse.cmds;

import com.shadebyte.auctionhouse.Core;
import com.shadebyte.auctionhouse.api.enums.Lang;
import com.shadebyte.auctionhouse.api.enums.Permissions;
import com.shadebyte.auctionhouse.cmds.subcmds.*;
import com.shadebyte.auctionhouse.inventory.inventories.AuctionGUI;
import com.shadebyte.auctionhouse.util.Debugger;
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

    public void initialize() {
        Core.getInstance().getCommand(main).setExecutor(this);
        this.commands.add(new SellCommand());
        this.commands.add(new ReloadCommand());
        this.commands.add(new ListedCommand());
        this.commands.add(new ExpiredCommand());
        this.commands.add(new TransactionsCommand());
        this.commands.add(new HelpCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission(Permissions.BASE.getNode())) {
            sender.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.NO_PERMISSION.getNode()));
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
                sender.sendMessage(Core.getInstance().getSettings().getPrefix() + Core.getInstance().getLocale().getMessage(Lang.INVALID_SUBCOMMAND.getNode()));
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

            for (int var5 = 0; var5 < length; ++var5) {
                String alias = aliases[var5];
                if (name.equalsIgnoreCase(alias)) {
                    return sc;
                }

            }
        }
        return null;
    }
}