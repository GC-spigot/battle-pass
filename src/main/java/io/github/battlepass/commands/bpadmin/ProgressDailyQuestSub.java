package io.github.battlepass.commands.bpadmin;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.QuestCache;
import io.github.battlepass.commands.BpSubCommand;
import io.github.battlepass.controller.QuestController;
import io.github.battlepass.enums.Category;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.quests.service.executor.QuestExecution;
import io.github.battlepass.quests.workers.pipeline.steps.QuestCompletionStep;
import io.github.battlepass.quests.workers.pipeline.steps.QuestValidationStep;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProgressDailyQuestSub extends BpSubCommand<CommandSender> {
    private final QuestCache questCache;
    private final QuestController controller;
    private final QuestValidationStep questValidationStep;
    private final QuestCompletionStep questCompletionStep;
    private final boolean blockPermissionEnabled;

    public ProgressDailyQuestSub(BattlePlugin plugin) {
        super(plugin);
        this.questCache = plugin.getQuestCache();
        this.controller = plugin.getQuestController();
        this.questValidationStep = new QuestValidationStep(plugin);
        this.questCompletionStep = new QuestCompletionStep(plugin);
        this.blockPermissionEnabled = plugin.getConfig("settings").bool("enable-ban-permission");

        this.inheritPermission();
        this.addFlats("progress", "daily", "quest");
        this.addArgument(User.class, "player", sender -> Bukkit.getOnlinePlayers()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList()));
        this.addArgument(String.class, "quest id");
        this.addArgument(BigInteger.class, "amount");
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        Optional<User> maybeUser = this.parseArgument(args, 3);
        Player player = maybeUser.map(value -> Bukkit.getPlayer(value.getUuid())).orElse(null);
        String id = this.parseArgument(args, 4);
        BigInteger amount = this.parseArgument(args, 5);

        if (!maybeUser.isPresent() || player == null) {
            this.lang.external("could-not-find-user", replacer -> replacer.set("player", args[2])).to(sender);
            return;
        }
        if (this.blockPermissionEnabled && player.hasPermission("battlepass.block") && !player.hasPermission("battlepass.admin")) {
            this.lang.local("blocked-from-pass", sender.getName()).to(sender);
            return;
        }
        User user = maybeUser.get();
        Quest quest = this.questCache.getQuest(Category.DAILY.id(), id);
        if (quest == null) {
            this.lang.local("invalid-quest-id", args[4], args[3].toLowerCase()).to(sender);
            return;
        }
        if (this.controller.isQuestDone(user, quest)) {
            this.lang.local("quest-already-done", args[2]);
            return;
        }
        if (this.questValidationStep.isQuestPrimarilyValid(user, quest, amount, false)) {
            QuestExecution questExecution = new QuestExecution(player, null, amount, false, null);
            questExecution.setUser(user);
            this.questCompletionStep.process(questExecution, quest, this.controller.getQuestProgress(user, quest), amount);
            this.lang.local("successful-quest-progress", quest.getName()).to(sender);
        } else {
            this.lang.local("failed-quest-progress", quest.getName()).to(sender);
        }
    }
}