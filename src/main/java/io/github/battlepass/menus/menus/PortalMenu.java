package io.github.battlepass.menus.menus;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.BattlePassApi;
import io.github.battlepass.loader.PassLoader;
import io.github.battlepass.menus.UserDependent;
import io.github.battlepass.menus.service.extensions.ConfigMenu;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.quests.workers.reset.DailyQuestReset;
import me.hyfe.simplespigot.config.Config;
import org.bukkit.entity.Player;

public class PortalMenu extends ConfigMenu implements UserDependent {
    private final BattlePassApi api;
    private final PassLoader passLoader;
    private final DailyQuestReset dailyQuestReset;
    private final User user;

    public PortalMenu(BattlePlugin plugin, Config config, Player player) {
        super(plugin, config, player);
        this.api = plugin.getLocalApi();
        this.passLoader = plugin.getPassLoader();
        this.dailyQuestReset = plugin.getDailyQuestReset();
        this.user = plugin.getUserCache().getOrThrow(player.getUniqueId());
    }

    @Override
    public void redraw() {
        this.drawConfigItems(replacer -> replacer
                .set("daily_time_left", this.dailyQuestReset.asString())
                .set("pass_type", this.passLoader.passTypeOfId(this.user.getPassId()).getName())
                .set("week", this.api.getWeekFormatted())
                .set("tier", this.user.getTier())
                .set("points", this.user.getPoints().toString())
                .set("required_points", this.api.getRequiredPoints(this.user.getTier(), this.user.getPassId()))
                .tryAddPapi(this.player)
        );
    }

    @Override
    public boolean isUserViable() {
        return this.user != null;
    }
}
