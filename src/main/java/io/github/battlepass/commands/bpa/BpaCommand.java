package io.github.battlepass.commands.bpa;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.commands.bpa.debugger.DebugDumpSub;
import io.github.battlepass.commands.bpa.debugger.PlayerDebugDumpSub;
import io.github.battlepass.commands.bpa.materialsub.MaterialBlockSub;
import io.github.battlepass.commands.bpa.materialsub.MaterialItemSub;
import io.github.battlepass.commands.bpa.setpass.SetPassAllSub;
import io.github.battlepass.commands.bpa.setpass.SetPassOnlineSub;
import io.github.battlepass.commands.bpa.setpass.SetPassSub;
import me.hyfe.simplespigot.command.command.SimpleCommand;
import me.hyfe.simplespigot.text.Text;
import org.bukkit.command.CommandSender;

public class BpaCommand extends SimpleCommand<CommandSender> {
    private String message;

    public BpaCommand(BattlePlugin plugin) {
        super(plugin, "battlepassadmin", "battlepass.admin", true);
        this.noPermissionLang(sender -> plugin.getLang().external("no-permission").asString());
        this.setSubCommands(
                new DebugDumpSub(plugin),
                new PlayerDebugDumpSub(plugin),
                new MaterialBlockSub(plugin),
                new MaterialItemSub(plugin),
                new SetPassAllSub(plugin),
                new SetPassOnlineSub(plugin),
                new SetPassSub(plugin),
                new BypassLockedQuestsSub(plugin),
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
        this.setupMessage();
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Text.sendMessage(sender, this.message);
    }

    private void setupMessage() {
        this.message = "\n"
                + "\n&bBattlePass Admin Help:\n"
                + "/bpa - This page."
                + "/bpa reload - Reloads all the reloadable files."
                + "/bpa debug dump - Dumps a debug log with lots of information."
                + "/bpa debug dump <player> - Dumps a debug log with player pertinent information."
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
                + "/bpa give balance <player> <amount> - Give a certain amount of internal balance to a player"
                + "/bpa set balance <player> <amount> - Set the internal balance of a player"
                + "/bpa remove balance <player> <amount> - Remove a certain amount of internal balance from a player"
                .replace("- ", "&8- &7")
                .replace("/bpa", "&e/bpa")
                .replace(".", ".\n");
    }
}
