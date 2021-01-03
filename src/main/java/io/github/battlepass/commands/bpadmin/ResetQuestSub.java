package io.github.battlepass.commands.bpadmin;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.controller.QuestController;
import io.github.battlepass.enums.Category;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.stream.Collectors;

public class ResetQuestSub extends BpSubCommand<CommandSender> {
    private final QuestCache questCache;
    private final QuestController controller;

    public ResetQuestSub(BattlePlugin plugin) {
        super(plugin);
        this.questCache = plugin.getQuestCache();
        this.controller = plugin.getQuestController();

        this.inheritPermission();
        this.addFlats("reset", "quest");
        this.addArgument(User.class, "player", sender -> Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList()));
        this.addArgument(Integer.class, "week");
        this.addArgument(String.class, "id");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Optional<User> maybeUser = this.parseArgument(args, 2);
        int week = this.parseArgument(args, 3);
        String id = this.parseArgument(args, 4);

        if (!maybeUser.isPresent()) {
            this.lang.external("could-not-find-user", replacer -> replacer.set("player", args[2])).to(sender);
            return;
        }
        User user = maybeUser.get();
        Quest quest = this.questCache.getQuest(Category.WEEKLY.id(week), id);
        if (quest == null) {
            this.lang.local("invalid-quest-id", args[4], args[3].toLowerCase()).to(sender);
            return;
        }
        boolean success = this.controller.resetQuest(user, quest);
        this.lang.local(success ? "successful-quest-reset" : "failed-quest-reset", quest.getName(), args[2]).to(sender);
    }
}