package io.github.battlepass.commands.bpadmin;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.enums.Category;
import io.github.battlepass.objects.quests.Quest;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class QuestIdsSub extends BpSubCommand<CommandSender> {
    private final QuestCache questCache;

    public QuestIdsSub(BattlePlugin plugin) {
        super(plugin);

        this.inheritPermission();
        this.addFlats("quest", "ids");
        this.addArgument(Integer.class, "week");
        this.questCache = plugin.getQuestCache();
    }

    @Override
    public void onExecute(CommandSender commandSender, String[] args) {
        int week = this.parseArgument(args, 2);

        this.lang.local("quest-ids-title").to(commandSender);
        for (Map.Entry<String, Quest> entry : this.questCache.getQuests(Category.WEEKLY.id(week)).entrySet()) {
            this.lang.local("quest-id", entry.getKey(), entry.getValue().getName(), entry.getValue().getPoints()).to(commandSender);
        }
    }
}