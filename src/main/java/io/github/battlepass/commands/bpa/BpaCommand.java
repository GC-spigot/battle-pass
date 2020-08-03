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

    public BpaCommand(BattlePlugin plugin) {
        super(plugin, "battlepassadmin", "battlepass.admin", true);
        this.noPermissionLang(sender -> plugin.getLang().external("no-permission").asString());
        this.setSubCommands(
                new DebugDumpSub(plugin),
                new MaterialBlockSub(plugin),
                new MaterialItemSub(plugin),
                new SetPassAllSub(plugin),
                new SetPassOnlineSub(plugin),
                new SetPassSub(plugin),
                new PlayerDebugDumpSub(plugin),
                new BypassLockedQuestsSub(plugin),
                new ReloadSub(plugin),
                new QuestIdsSub(plugin),
                new DailyQuestIdsSub(plugin),
                new ProgressQuestSub(plugin),
                new ProgressDailyQuestSub(plugin),
                new DeleteUserSub(plugin),
                new RefreshDailyQuestsSub(plugin),
                new ResetQuestSub(plugin),
                new GivePointsSub(plugin),
                new SetPointsSub(plugin)
        );
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Text.sendMessage(sender, "\n"
                .concat("\n&bBattlePass Admin Help:\n")
                .concat("/bpa - This page.")
                .concat("/bpa reload - Reloads all the reloadable files.")
                .concat("/bpa debug dump - Dumps a debug log with lots of information.")
                .concat("/bpa debug dump <player> - Dumps a debug log with player pertinent information.")
                .concat("/bpa set pass <player> <pass type> - Sets a players pass type.")
                .concat("/bpa set pass online/all <pass type> - Mass set lots of players' pass types.")
                .concat("/bpa set points <player> <points> - Set a players points.")
                .concat("/bpa give points <player> <points> - Give a player points.")
                .concat("/bpa delete user <player> - Delete all data of a user.")
                .concat("/bpa quest ids <week> - List all of the weeks quest ids and their names.")
                .concat("/bpa daily quest ids - List all of the daily quest ids and their names.")
                .concat("/bpa reset quest <player> <week> <id> - Resets a specific quest of a player.")
                .concat("/bpa progress quest <player> <week> <quest id> <amount> - Progress a specific quest of a player.")
                .concat("/bpa progress daily quest <player> <quest id> <amount> - Progress a specific quest of a player.")
                .concat("/bpa refresh daily quests - Refresh daily quests.")
                .concat("/bpa material <block/item> - Get the config name of the item you're holding or block you're looking at.")
                .concat("/bpa bypass locked quests <player> - Allows the player to bypass week locks and complete quests anyway.")
                .replace("- ", "&8- &7")
                .replace("/bp", "&e/bp")
                .replace(".", ".\n"));
    }
}
