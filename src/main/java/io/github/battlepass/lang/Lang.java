package io.github.battlepass.lang;

import com.google.common.collect.Maps;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.logger.Zone;
import io.github.battlepass.objects.quests.Quest;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.text.Text;
import me.hyfe.simplespigot.text.replacer.Replace;
import org.bukkit.command.CommandSender;

import java.math.BigInteger;
import java.util.Map;

public class Lang {
    private final BattlePlugin plugin;
    private final Map<String, String> localLang = Maps.newHashMap();
    private final Map<String, Object> externalLang = Maps.newHashMap();

    public Lang(BattlePlugin plugin) {
        this.plugin = plugin;
        this.localLang.put("successful-refresh-daily", "&aSuccessfully&7 refreshed all the &adaily&7 quests.");
        this.localLang.put("invalid-quest-type", "&7Could not find the quest type &c%s&7.");
        this.localLang.put("quest-ids-title", "&eQuest id's and their names");
        this.localLang.put("quest-id", "&7   id: &e%s &7name: &e%s   &7&o(worth %s points)");
        this.localLang.put("invalid-quest-id", "&7Could not find the quest id &c%s&7 from the type &c%s&7. Use the command &c/bpa quest ids <type>&7.");
        this.localLang.put("successful-reload", "&aSuccessfully&7 reloaded all files in &a%d&7 milliseconds.");
        this.localLang.put("successful-quest-reset", "&aSuccessfully&7 reset the quest &a%s&7 for &a%s&7.");
        this.localLang.put("failed-quest-reset", "&cFailed&7 to reset the quest &c%s&7 for &c%s&7.");
        this.localLang.put("successful-quest-progress", "&aSuccessfully&7 progressed the quest &a%s&7.");
        this.localLang.put("failed-quest-progress", "&cFailed to progress the quest &a%s&c.");
        this.localLang.put("invalid-pass-id", "&7Could not find a pass type with the id &c%s&7.");
        this.localLang.put("successful-set-pass", "&aSuccessfully&7 set &a%s's&7 pass type to &a%s.");
        this.localLang.put("successful-set-pass-online", "&aSuccessfully&7 set &aall online player's&7 pass type to &a%s.");
        this.localLang.put("successful-set-pass-global", "&aSuccessfully&7 set &aevery user's&7 pass type to &a%s.");
        this.localLang.put("successful-give-points", "&aSuccessfully&7 gave %s &a%d&7 points.");
        this.localLang.put("successful-set-points", "&aSuccessfully&7 set &a%s's&7 points to &a%d&7.");
        this.localLang.put("failed-set-pass-require-permission", "&7You &ccan not&7 set the pass type to &c%s&7 because you have &crequired-permission&7 enabled. This value is set to &c%s&7.");
        this.localLang.put("successful-deleted-user", "&aSuccessfully&7 deleted the user &a%s&7.");
        this.localLang.put("quest-already-done", "&a%s has already completed this quest.");
        this.localLang.put("toggle-lock-bypass-on", "&c%s now bypasses week locks.");
        this.localLang.put("toggle-lock-bypass-off", "&c%s no longer bypasses week locks.");
        this.localLang.put("target-toggle-lock-bypass-on", "&cYou now bypass week locks");
        this.localLang.put("target-toggle-lock-bypass-off", "&cYou no longer bypass week locks.");
        this.localLang.put("user-data-deleted", "&aYou have successfully deleted %s's data.");
        this.localLang.put("target-user-data-deleted", "&cYour quest data has been wiped.");
        this.localLang.put("user-data-deleted-null-player", "&cUser data deleted.");
        this.localLang.put("debug-dumped", "&cDumped the debug information to a file called %s");
        this.localLang.put("debug-start", "&aDebugger started.");
        this.localLang.put("debug-stop", "&cDebugger stopped.");
        this.localLang.put("debug-not-running", "&cThe debugger is not running.");
        this.localLang.put("debug-already-running", "&cThe debugger is already running.");
        this.localLang.put("debug-cleared", "&aThe debugger has been cleared.");
        this.localLang.put("cooldown-has-seconds", "&cPlease wait %s seconds before using this again.");
        this.localLang.put("confirm-new-season", "&cType the command again within 30 seconds to confirm this action. This command can be destructive, read the wiki first.");
        this.localLang.put("new-season-reset", "&cReset user tiers, points, quest progress and pending rewards.");
        this.localLang.put("blocked-from-pass", "&c%s is blocked from the battlepass.");
        this.localLang.put("closed-player-menu", "&aClosed %s's open menu if they had one.");
        this.localLang.put("invalid-number-input", "&cCould not parse number amount.");
        this.localLang.put("given-user-balance", "&aGiven %s balance to %s.");
        this.localLang.put("removed-user-balance", "&aRemoved %s from %s's balance.");
        this.localLang.put("set-user-balance", "&aSet %s's balance to %s.");
        this.load();
    }

    public void load() {
        Config config = this.plugin.getConfig("lang");
        for (String key : config.keys("", true)) {
            String value = this.getCompatibleString(config, key);
            this.externalLang.put(key, value);
            this.plugin.log(Zone.START, "Loaded lang value '" + key + "' as " + value + ".");
        }
    }

    public void reload() {
        this.plugin.log(Zone.RELOAD, "Reloading Lang.");
        this.externalLang.clear();
        this.plugin.log(Zone.RELOAD, "Loading Lang.");
        this.load();
    }

    public boolean has(String section) {
        boolean contains = this.externalLang.containsKey(section);
        this.plugin.log("(LANG) Looked for " + section + " with " + contains + " output.");
        return contains;
    }

    public LangSub of(String section) {
        return new LangSub(this.plugin.getConfig("lang").string(section));
    }

    public LangSub local(String id, Object... args) {
        return new LangSub(String.format(this.localLang.get(id), args));
    }

    public LangSub external(String id) {
        return this.external(id, null);
    }

    public LangSub external(String id, Replace replace) {
        Object requested = this.externalLang.get(id);
        if (requested == null) {
            BattlePlugin.logger().info("^^^^^^^^^^ -[BattlePass]- ^^^^^^^^^^");
            BattlePlugin.logger().info(" ");
            BattlePlugin.logger().info("Missing the configuration value '".concat(id).concat("', located in the file 'lang.yml'"));
            BattlePlugin.logger().info(" ");
            BattlePlugin.logger().info("^^^^^^^^^^ -[BattlePass]- ^^^^^^^^^^");
            return null;
        }
        return new LangSub(Text.modify(String.valueOf(requested), replace));
    }

    public String questCompleteMessage(Quest quest) {
        return Text.modify(this.external(this.getCompletionPath(quest)).asString(), replacer -> replacer.set("quest_name", quest.getName()));
    }

    public String questBossCompleteMessage(Quest quest) {
        return Text.modify(this.external(this.getBossBarCompletionPath(quest)).asString(), replacer -> replacer.set("quest_name", quest.getName()));
    }

    public String questProgressedMessage(Quest quest, BigInteger progress) {
        return Text.modify(this.external(this.getProgressionPath(quest)).asString(), replacer -> replacer
                .set("quest_name", quest.getName())
                .set("progress", progress)
                .set("required_progress", quest.getRequiredProgress()));
    }

    public String questBossProgressedMessage(Quest quest, BigInteger progress) {
        return Text.modify(this.external(this.getBossBarProgressionPath(quest)).asString(), replacer -> replacer
                .set("quest_name", quest.getName())
                .set("progress", progress)
                .set("required_progress", quest.getRequiredProgress()));
    }

    private String getCompatibleString(Config config, String key) {
        Object object = config.get(key);
        if (object instanceof String) {
            return String.valueOf(object);
        }
        StringBuilder builder = new StringBuilder();
        for (String line : config.stringList(key)) {
            builder.append(line)
                    .append("\n");
        }
        return builder.toString();
    }

    private String getCompletionPath(Quest quest) {
        if (quest.getCategoryId().contains("daily")) {
            if (this.has("daily-quests.".concat(quest.getType()))) {
                return "daily-quests".concat(quest.getType());
            }
            if (this.has("daily-quests.base-message-completed")) {
                return "daily-quests.base-message-completed";
            }
        }
        if (this.has("quests.".concat(quest.getType()))) {
            return "quests.".concat(quest.getType());
        }
        return "quests.base-message-completed";
    }

    private String getBossBarCompletionPath(Quest quest) {
        return (quest.getCategoryId().contains("daily") ? "daily-" : "") + "quests.boss-bar-message-completed";
    }

    private String getProgressionPath(Quest quest) {
        if (quest.getCategoryId().contains("daily")) {
            if (this.has("daily-quests.".concat(quest.getType()))) {
                return "daily-quests".concat(quest.getType());
            }
            if (this.has("daily-quests.base-message-progressed")) {
                return "daily-quests.base-message-progressed";
            }
        }
        if (this.has("quests.".concat(quest.getType()))) {
            return "quests.".concat(quest.getType());
        }
        return "quests.base-message-progressed";
    }

    private String getBossBarProgressionPath(Quest quest) {
        return (quest.getCategoryId().contains("daily") ? "daily-" : "") + "quests.boss-bar-message-progressed";
    }

    public static class LangSub {
        private final String message;

        public LangSub(String message) {
            this.message = message;
        }

        public String asString() {
            return this.message;
        }

        @Override
        public String toString() {
            return this.message;
        }

        public void to(CommandSender commandSender) {
            Text.sendMessage(commandSender, this.message);
        }
    }
}