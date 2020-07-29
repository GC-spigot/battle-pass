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
import me.hyfe.simplespigot.version.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.logging.Level;

public class QuestRegistry implements Registry {
    private final BattlePlugin plugin;
    private final Set<String> registeredHooks = Sets.newHashSet();
    private final Map<String, ImmutablePair<AtomicInteger, Integer>> attempts = Maps.newHashMap();

    public QuestRegistry(BattlePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        this.registerQuests(
                BlockBreakQuest::new,
                BlockPlaceQuest::new,
                ChatQuest::new,
                ClickQuest::new,
                ConsumeQuest::new,
                CraftQuest::new,
                DamageQuest::new,
                EnchantQuest::new,
                ExecuteCommandQuest::new,
                FishingQuest::new,
                GainExpQuest::new,
                MovementQuests::new,
                ItemBreakQuest::new,
                KillMobQuest::new,
                KillPlayerQuest::new,
                LoginQuest::new,
                MilkQuest::new,
                PlayTimeQuest::new,
                RegenerateQuest::new,
                RideMobQuest::new,
                ShearSheepQuest::new,
                SmeltQuest::new,
                TameQuest::new
        );
        if (ServerVersion.getVersion().getVersionId() >= 1161) {

        }
        this.registerHook("AdvancedEnchantments", AdvancedEnchantmentsQuests::new);
        this.registerHook("ASkyblock", ASkyblockQuests::new);
        this.registerHook("AuctionHouse", AuctionHouseKludgeQuests::new, "klugemonkey");
        this.registerHook("AutoSell", AutoSellQuests::new, "extended_clip");
        this.registerHook("BedWars1058", BedWars1058Quests::new);
        this.registerHook("KOTH", BenzimmerKothQuests::new, "benzimmer123");
        this.registerHook("BuildBattle", BuildBattleTigerQuests::new, "Tigerpanzer");
        this.registerHook("ChatReaction", ChatReactionQuests::new);
        this.registerHook("ChestShop", ChestShopQuests::new, "https://github.com/ChestShop-authors/ChestShop-3/contributors", version -> version > 3.92);
        this.registerHook("ChestShop", LegacyChestShopQuests::new, "https://github.com/ChestShop-authors/ChestShop-3/contributors", version -> version <= 3.92);
        this.registerHook("Citizens", CitizensQuests::new);
        this.registerHook("Clans", ClansQuests::new);
        this.registerHook("ClueScrolls", ClueScrollsQuests::new);
        this.registerHook("CrateReloaded", CrateReloadedQuests::new);
        this.registerHook("CratesPlus", CratesPlusQuests::new);
        this.registerHook("CrazyCrates", CrazyCratesQuests::new);
        this.registerHook("DiscordMinecraft", DiscordMinecraftQuests::new);
        // this.registerHook("Factions", FactionsUuidQuests::new, "com.massivecraft.factions.event.FactionPlayerEvent");
        this.registerHook("Jobs", JobsQuests::new);
        this.registerHook("Lands", LandsQuests::new);
        this.registerHook("LobbyPresents", LobbyPresentsPoompkQuests::new, "poompk");
        this.registerHook("KoTH", SubsideKothQuests::new, "SubSide");
        this.registerHook("MoneyHunters", MoneyHuntersQuests::new);
        this.registerHook("MythicMobs", MythicMobsQuests::new);
        this.registerHook("PlaceholderApi", PlaceholderApiQuests::new);
        this.registerHook("PlotSquared", PlotSquaredQuests::new);
        this.registerHook("ProCosmetics", ProCosmeticsQuests::new);
        this.registerHook("CrazyEnvoy", CrazyEnvoyQuests::new);
        this.registerHook("Shopkeepers", ShopkeeperQuests::new, "nisovin");
        this.registerHook("SkillAPI", SkillApiQuests::new);
        this.registerHook("StrikePractice", StrikePracticeQuests::new);
        this.registerHook("SuperiorSkyblock2", SuperiorSkyblockQuests::new);
        this.registerHook("TheLab", TheLabQuests::new);
        this.registerHook("TokenEnchant", TokenEnchantQuests::new);
        this.registerHook("UltraSkyWars", UltraSkyWarsQuests::new, "Leonardo0013YT");
        this.registerHook("uSkyBlock", USkyBlockQuests::new);
        this.registerHook("Votifier", VotifierQuests::new);
    }

    @SafeVarargs
    public final void registerQuests(Function<BattlePlugin, QuestExecutor>... functions) {
        for (Function<BattlePlugin, QuestExecutor> function : functions) {
            Bukkit.getPluginManager().registerEvents(function.apply(this.plugin), this.plugin);
        }
    }

    public boolean registerHook(String plugin, Function<BattlePlugin, ExternalQuestExecutor> function) {
        if (Bukkit.getPluginManager().isPluginEnabled(plugin)) {
            Bukkit.getPluginManager().registerEvents(function.apply(this.plugin), this.plugin);
            Bukkit.getLogger().log(Level.INFO, "Hooked into ".concat(plugin));
            this.registeredHooks.add(plugin);
            return true;
        }
        this.runRepeatingCheck(plugin, () -> {
            if (this.registerHook(plugin, function)) {
                Bukkit.getScheduler().cancelTask(this.attempts.get(plugin).getValue());
            }
        });
        return false;
    }

    public boolean registerHook(String plugin, Function<BattlePlugin, ExternalQuestExecutor> function, String author) {
        if (Bukkit.getPluginManager().isPluginEnabled(plugin)) {
            if (Bukkit.getPluginManager().getPlugin(plugin).getDescription().getAuthors().contains(author)) {
                Bukkit.getPluginManager().registerEvents(function.apply(this.plugin), this.plugin);
                Bukkit.getLogger().log(Level.INFO, "Hooked into ".concat(plugin));
                this.registeredHooks.add(plugin);
                return true;
            }
        }
        this.runRepeatingCheck(plugin, () -> {
            if (this.registerHook(plugin, function, author)) {
                Bukkit.getScheduler().cancelTask(this.attempts.get(plugin).getValue());
            }
        });
        return false;
    }

    public boolean registerHook(String plugin, Function<BattlePlugin, ExternalQuestExecutor> function, Function<Double, Boolean> versionCheck) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled(plugin)) {
            double version = this.getFormattedVersion(plugin);
            if (versionCheck.apply(version)) {
                pluginManager.registerEvents(function.apply(this.plugin), this.plugin);
                Bukkit.getLogger().log(Level.INFO, "Hooked into ".concat(plugin));
                this.registeredHooks.add(plugin);
            } else {
                Bukkit.getLogger().log(Level.INFO, plugin.concat(" was present but its version is not supported."));
            }
            return true;
        }
        this.runRepeatingCheck(plugin, () -> {
            if (this.registerHook(plugin, function, versionCheck)) {
                Bukkit.getScheduler().cancelTask(this.attempts.get(plugin).getValue());
            }
        });
        return false;
    }

    public boolean registerHook(String plugin, Function<BattlePlugin, ExternalQuestExecutor> function, String author, Function<Double, Boolean> versionCheck) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        if (pluginManager.isPluginEnabled(plugin)) {
            double version = this.getFormattedVersion(plugin);
            if (pluginManager.getPlugin(plugin).getDescription().getAuthors().contains(author)) {
                Bukkit.getLogger().log(Level.INFO, "Using internal version as ".concat(String.valueOf(version)).concat(" for loading ").concat(plugin).concat("."));
                if (versionCheck.apply(version)) {
                    pluginManager.registerEvents(function.apply(this.plugin), this.plugin);
                    Bukkit.getLogger().log(Level.INFO, "Hooked into ".concat(plugin));
                    this.registeredHooks.add(plugin);
                } else {
                    Bukkit.getLogger().log(Level.INFO, plugin.concat(" was present but its version is not supported."));
                }
            }
            return true;
        }
        this.runRepeatingCheck(plugin, () -> {
            if (this.registerHook(plugin, function, author, versionCheck)) {
                Bukkit.getScheduler().cancelTask(this.attempts.get(plugin).getValue());
            }
        });
        return false;
    }

    private void runRepeatingCheck(String plugin, Runnable runnable) {
        if (this.attempts.containsKey(plugin)) {
            return;
        }
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            AtomicInteger value = this.attempts.get(plugin).getKey();
            if (value.intValue() > 18) {
                Bukkit.getScheduler().cancelTask(this.attempts.get(plugin).getValue());
            }
            runnable.run();
        }, 200, 200);

        this.attempts.put(plugin, ImmutablePair.of(new AtomicInteger(), taskId));
    }

    private double getFormattedVersion(String plugin) {
        String pluginVersion = Bukkit.getPluginManager().getPlugin(plugin).getDescription().getVersion().replace("-", ".");
        if (pluginVersion.contains(".")) {
            String[] split = pluginVersion.split("\\.");
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (String part : split) {
                builder.append(part.replace("[^0-9]", ""));
                if (first) {
                    builder.append(".");
                    first = false;
                }
            }
            return Double.parseDouble(builder.toString().split(" ")[0]);
        }
        return Double.parseDouble(pluginVersion);
    }

    public Set<String> getRegisteredHooks() {
        return this.registeredHooks;
    }
}