package io.github.battlepass.commands.bpadmin;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.bpadmin.balance.GiveBalanceSub;
import io.github.battlepass.commands.bpadmin.balance.RemoveBalanceSub;
import io.github.battlepass.commands.bpadmin.balance.SetBalanceSub;
import io.github.battlepass.commands.bpadmin.materialsub.MaterialBlockSub;
import io.github.battlepass.commands.bpadmin.materialsub.MaterialItemSub;
import io.github.battlepass.commands.bpadmin.setpass.SetPassAllSub;
import io.github.battlepass.commands.bpadmin.setpass.SetPassOnlineSub;
import io.github.battlepass.commands.bpadmin.setpass.SetPassSub;
import me.hyfe.simplespigot.command.command.SimpleCommand;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.command.CommandSender;

public class BpaCommand extends SimpleCommand<CommandSender> {
    private final boolean useInternalBalance;

    public BpaCommand(BattlePlugin plugin) {
        super(plugin, "battlepassadmin", "battlepass.admin", true);
        Config settings = plugin.getConfig("settings");
        this.useInternalBalance = settings.has("reward-excess-points.method") && settings.string("reward-excess-points.method").equalsIgnoreCase("internal");

        this.noPermissionLang(sender -> plugin.getLang().external("no-permission").asString());
        this.setSubCommands(
                new GiveBalanceSub(plugin),
                new RemoveBalanceSub(plugin),
                new SetBalanceSub(plugin),
                new MaterialBlockSub(plugin),
                new MaterialItemSub(plugin),
                new SetPassAllSub(plugin),
                new SetPassOnlineSub(plugin),
                new SetPassSub(plugin),
                new BypassLockedQuestsSub(plugin),
                new CloseMenuSub(plugin),
                new DailyQuestIdsSub(plugin),
                new DeleteUserSub(plugin),
                new GivePointsSub(plugin),
                new NewSeasonSub(plugin),
                new ProgressDailyQuestSub(plugin),
                new ProgressQuestSub(plugin),
                new QuestIdsSub(plugin),
                new RefreshDailyQuestsSub(plugin),
                new ReloadSub(plugin),
                new ResetQuestSub(plugin),
                new SetPointsSub(plugin)
        );
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Text.sendMessage(sender, ("\n"
                + "\n&bBattlePass Admin Help:\n"
                + "/bpa - This page."
                + "/bpa reload - Reloads all the reloadable files."
                + (this.useInternalBalance ? (
                "/bpa give balance <player> <amount> - Gives internal balance"
                        + "/bpa remove balance <player> <amount> - Takes away internal balance"
                        + "/bpa set balance <player> <amount> - Sets a player's internal balance")
                : "")
                + "/bpa set pass <player/online/all> <pass type> - Sets a player/group of players' pass type."
                + "/bpa set points <player> <points> - Set a players points."
                + "/bpa give points <player> <points> - Give a player points."
                + "/bpa delete user <player> - Delete all data of a user."
                + "/bpa quest ids <week> - List all of the weeks quest ids and their names."
                + "/bpa daily quest ids - List all of the daily quest ids and their names."
                + "/bpa reset quest <player> <week> <id> - Resets a specific quest of a player."
                + "/bpa progress quest <player> <week> <quest id> <amount> - Progress a specific quest of a player."
                + "/bpa progress daily quest <player> <quest id> <amount> - Progress a specific quest of a player."
                + "/bpa refresh daily quests - Refresh daily quests."
                + "/bpa new season - Resets user tiers, pending rewards and points."
                + "/bpa material <block/item> - Get the config name of the item you're holding or block you're looking at."
                + "/bpa bypass locked quests <player> - Allows the player to bypass week locks and complete quests anyway."
                + "/bpa close menu <player> - Closes a player's open menu ")
                .replace("- ", "&8- &7")
                .replace("/bpa", "&e/bpa")
                .replace(".", ".\n"));
    }
}
