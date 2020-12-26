package io.github.battlepass.registry;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.QuestExecutor;
import io.github.battlepass.quests.quests.external.*;
import io.github.battlepass.quests.quests.external.chestshop.ChestShopQuests;
import io.github.battlepass.quests.quests.external.chestshop.LegacyChestShopQuests;
import io.github.battlepass.quests.quests.external.executor.ExternalQuestExecutor;
import io.github.battlepass.quests.quests.internal.*;
import me.hyfe.simplespigot.registry.Registry;
import me.hyfe.simplespigot.tuple.ImmutablePair;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.logging.Level;

public class QuestRegistry implements Registry {
    private final BattlePlugin plugin;
    private final PluginManager manager;
    private final Set<String> disabledHooks = Sets.newHashSet();
    private final Set<String> registeredHooks = Sets.newHashSet();
    private final Map<String, ImmutablePair<AtomicInteger, BukkitTask>> attempts = Maps.newHashMap();

    public QuestRegistry(BattlePlugin plugin) {
        this.plugin = plugin;
        this.manager = Bukkit.getPluginManager();

        for (String disabledHook : plugin.getConfig("settings").stringList("disabled-plugin-hooks")) {
            this.disabledHooks.add(disabledHook.toLowerCase());
        }
    }

    @Override
    public void register() {
        this.quest(
                BlockBreakQuest::new,
                BlockPlaceQuest::new,
                ChatQuest::new,
                ClickQuest::new,
                ConsumeQuest::new,
                CraftQuest::new,
                DamageQuest::new,
                EnchantQuests::new,
                ExecuteCommandQuest::new,
                FishingQuest::new,
                GainExpQuest::new,
                ItemBreakQuest::new,
                KillMobQuest::new,
                KillPlayerQuest::new,
                LoginQuest::new,
                MilkQuest::new,
                MovementQuests::new,
                ProjectileQuest::new,
                RegenerateQuest::new,
                RideMobQuest::new,
                ShearSheepQuest::new,
                SmeltQuest::new,
                TameQuest::new
        );
        if (this.plugin.getConfig("settings").bool("enable-play-time")) {
            new PlayTimeQuest(this.plugin);
        }
        this.hook("AdvancedEnchantments", AdvancedEnchantmentsQuests::new);
        this.hook("ASkyblock", ASkyblockQuests::new);
        this.hook("AuctionHouse", AuctionHouseKludgeQuests::new, "klugemonkey");
        this.hook("AutoSell", AutoSellQuests::new, "extended_clip");
        this.hook("BedWars1058", BedWars1058Quests::new);
        this.hook("KOTH", BenzimmerKothQuests::new, "benzimmer123");
        this.hook("BuildBattle", BuildBattleTigerQuests::new, "Tigerpanzer");
        this.hook("ChatReaction", ChatReactionQuests::new);
        this.hook("ChestShop", ChestShopQuests::new, version -> version > 3.92);
        this.hook("ChestShop", ChestShopQuests::new, "https://github.com/ChestShop-authors/ChestShop-3/contributors", version -> version > 3.92);
        this.hook("ChestShop", LegacyChestShopQuests::new, "https://github.com/ChestShop-authors/ChestShop-3/contributors", version -> version <= 3.92);
        this.hook("Citizens", CitizensQuests::new);
        this.hook("Clans", ClansQuests::new);
        this.hook("ClueScrolls", ClueScrollsQuests::new);
        this.hook("CrateReloaded", CrateReloadedQuests::new);
        this.hook("CratesPlus", CratesPlusQuests::new);
        this.hook("CrazyCrates", CrazyCratesQuests::new);
        this.hook("DiscordMinecraft", DiscordMinecraftQuests::new);
        this.hook("Jobs", JobsQuests::new);
        this.hook("Lands", LandsQuests::new);
        this.hook("LobbyPresents", LobbyPresentsPoompkQuests::new, "poompk");
        this.hook("KoTH", SubsideKothQuests::new, "SubSide");
        this.hook("MoneyHunters", MoneyHuntersQuests::new);
        this.hook("MythicMobs", MythicMobsQuests::new);
        this.hook("PlotSquared", PlotSquaredQuests::new);
        this.hook("ProCosmetics", ProCosmeticsQuests::new);
        this.hook("CrazyEnvoy", CrazyEnvoyQuests::new);
        this.hook("Shopkeepers", ShopkeepersQuests::new, "nisovin");
        this.hook("SkillAPI", SkillApiQuests::new);
        this.hook("StrikePractice", StrikePracticeQuests::new);
        this.hook("SuperiorSkyblock2", SuperiorSkyblockQuests::new);
        this.hook("TheLab", TheLabQuests::new);
        this.hook("TokenEnchant", TokenEnchantQuests::new);
        this.hook("UltraSkyWars", UltraSkyWarsQuests::new, "Leonardo0013YT");
        this.hook("uSkyBlock", USkyBlockQuests::new);
        this.hook("Votifier", VotifierQuests::new);
        this.placeholderAPI(this.plugin.getQuestCache().getPlaceholderTypes());
    }

    public Set<String> getRegisteredHooks() {
        return this.registeredHooks;
    }

    public boolean isHookDisabled(String plugin) {
        return this.disabledHooks.contains(plugin.toLowerCase());
    }

    @FunctionalInterface
    public interface Instantiator<T extends QuestExecutor> {
        T init(BattlePlugin plugin);
    }

    @SafeVarargs
    public final void quest(Instantiator<QuestExecutor>... instantiators) {
        for (Instantiator<?> instantiator : instantiators) {
            Bukkit.getPluginManager().registerEvents(instantiator.init(this.plugin), this.plugin);
        }
    }

    public void hook(String name, Instantiator<ExternalQuestExecutor> instantiator) {
        this.hook(name, instantiator, "");
    }

    public boolean hook(String name, Instantiator<ExternalQuestExecutor> instantiator, String author) {
        if (this.isHookDisabled(name)) {
            return false;
        }
        Plugin plugin = this.manager.getPlugin(name);
        if (plugin != null && plugin.isEnabled()) {
            if (!this.isHookDisabled(name) && (author.isEmpty() || plugin.getDescription().getAuthors().contains(author))) {
                this.manager.registerEvents(instantiator.init(this.plugin), this.plugin);
                BattlePlugin.logger().log(Level.INFO, "Hooked into ".concat(name));
                this.registeredHooks.add(name);
            }
            return true;
        }
        this.runRepeatingCheck(name, () -> {
            if (this.hook(name, instantiator, author)) {
                this.attempts.get(name).getValue().cancel();
            }
        });
        return false;
    }

    public void hook(String name, Instantiator<ExternalQuestExecutor> instantiator, Predicate<Double> versionPredicate) {
        this.hook(name, instantiator, "", versionPredicate);
    }

    public boolean hook(String name, Instantiator<ExternalQuestExecutor> instantiator, String author, Predicate<Double> versionPredicate) {
        if (this.isHookDisabled(name)) {
            return false;
        }
        Plugin plugin = this.manager.getPlugin(name);
        if (plugin != null && plugin.isEnabled()) {
            if (!this.isHookDisabled(name) && (author.isEmpty() || plugin.getDescription().getAuthors().contains(author))) {
                double version = this.extractVersion(plugin);
                BattlePlugin.logger().log(Level.INFO, "Using internal version as " + version + " for loading " + name + ".");
                if (versionPredicate.test(version)) {
                    this.manager.registerEvents(instantiator.init(this.plugin), this.plugin);
                    BattlePlugin.logger().log(Level.INFO, "Hooked into ".concat(name));
                    this.registeredHooks.add(name);
                } else {
                    BattlePlugin.logger().log(Level.INFO, name.concat(" was present but its version is not supported."));
                }
            }
            return true;
        }
        this.runRepeatingCheck(name, () -> {
            if (this.hook(name, instantiator, author, versionPredicate)) {
                this.attempts.get(name).getValue().cancel();
            }
        });
        return false;
    }

    private boolean placeholderAPI(Set<String> placeholderTypes) {
        if (this.isHookDisabled("placeholderapi")) {
            return false;
        }
        if (Bukkit.getPluginManager().isPluginEnabled(this.plugin)) {
            this.registeredHooks.add("PlaceholderAPI");
            new PlaceholderApiQuests(this.plugin, placeholderTypes);
            return true;
        }
        this.runRepeatingCheck("PlaceholderAPI", () -> {
            if (this.placeholderAPI(placeholderTypes)) {
                this.attempts.get("PlaceholderAPI").getValue().cancel();
            }
        });
        return false;
    }

    private void runRepeatingCheck(String name, Runnable runnable) {
        if (this.attempts.containsKey(name)) {
            return;
        }
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            int value = this.attempts.get(name).getKey().incrementAndGet();
            if (value > 60) {
                this.attempts.get(name).getValue().cancel();
            }
            runnable.run();
        }, 200, 200);
        this.attempts.put(name, ImmutablePair.of(new AtomicInteger(), bukkitTask));
    }

    private double extractVersion(Plugin plugin) {
        String version = plugin.getDescription().getVersion();
        StringBuilder extractedVersion = new StringBuilder();
        for (int i = 0; i < version.length(); i++) {
            char character = version.charAt(i);
            if (Character.isDigit(character)) {
                if (extractedVersion.length() == 0) {
                    extractedVersion.append(character).append(".");
                } else {
                    extractedVersion.append(character);
                }
            }
        }
        return extractedVersion.length() == 0 ? 0.0 : Double.parseDouble(extractedVersion.toString());
    }
}