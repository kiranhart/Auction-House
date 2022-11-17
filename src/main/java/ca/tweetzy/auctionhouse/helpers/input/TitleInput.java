package ca.tweetzy.auctionhouse.helpers.input;

import ca.tweetzy.flight.utils.Common;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * The current file has been created by Kiran Hart
 * Date Created: November 08 2021
 * Time Created: 4:56 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public abstract class TitleInput extends Input {

	private final Player player;
    private final String title;
    private final String subTitle;
    private final String actionbar;

    public TitleInput(@NonNull final Player player, final String title, final String subTitle, final String actionbar) {
        super(player);
        this.player = player;
        this.title = title;
        this.subTitle = subTitle;
        this.actionbar = actionbar;
    }

    public TitleInput(@NonNull final Player player, final String title, final String subTitle) {
        this(player, Common.colorize(title), Common.colorize(subTitle), Common.colorize(""));
    }

    public abstract boolean onResult(String string);

    public boolean onInput(String text) {
        if (this.onResult(text)) {
            this.close(true);
        }
        return true;
    }

    @EventHandler
    public void close(PlayerInteractEvent e) {
        if (e.getPlayer().equals(this.player) && (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)) {
            this.close(false);
        }
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getSubtitle() {
        return this.subTitle;
    }

    @Override
    public String getActionBar() {
        return this.actionbar;
    }
}
