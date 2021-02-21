package io.github.battlepass.registry.quest;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.quests.external.ASkyblockQuests;
import io.github.battlepass.quests.quests.external.AdvancedEnchantmentsQuests;
import io.github.battlepass.quests.quests.external.AuctionHouseKludgeQuests;
import io.github.battlepass.quests.quests.external.AutoSellQuests;
import io.github.battlepass.quests.quests.external.BedWars1058Quests;
import io.github.battlepass.quests.quests.external.BenzimmerKothQuests;
import io.github.battlepass.quests.quests.external.BuildBattleTigerQuests;
import io.github.battlepass.quests.quests.external.ChatReactionQuests;
import io.github.battlepass.quests.quests.external.CitizensQuests;
import io.github.battlepass.quests.quests.external.ClansQuests;
import io.github.battlepass.quests.quests.external.ClueScrollsQuests;
import io.github.battlepass.quests.quests.external.CrateReloadedQuests;
import io.github.battlepass.quests.quests.external.CratesPlusQuests;
import io.github.battlepass.quests.quests.external.CrazyCratesQuests;
import io.github.battlepass.quests.quests.external.CrazyEnvoyQuests;
import io.github.battlepass.quests.quests.external.DiscordMinecraftQuests;
import io.github.battlepass.quests.quests.external.JobsQuests;
import io.github.battlepass.quests.quests.external.LandsQuests;
import io.github.battlepass.quests.quests.external.LobbyPresentsPoompkQuests;
import io.github.battlepass.quests.quests.external.MoneyHuntersQuests;
import io.github.battlepass.quests.quests.external.MythicMobsQuests;
import io.github.battlepass.quests.quests.external.PlaceholderApiQuests;
import io.github.battlepass.quests.quests.external.PlotSquaredQuests;
import io.github.battlepass.quests.quests.external.ProCosmeticsQuests;
import io.github.battlepass.quests.quests.external.ShopGuiPlusQuests;
import io.github.battlepass.quests.quests.external.ShopkeepersQuests;
import io.github.battlepass.quests.quests.external.SkillApiQuests;
import io.github.battlepass.quests.quests.external.StrikePracticeQuests;
import io.github.battlepass.quests.quests.external.SubsideKothQuests;
import io.github.battlepass.quests.quests.external.SuperiorSkyblockQuests;
import io.github.battlepass.quests.quests.external.TheLabQuests;
import io.github.battlepass.quests.quests.external.TokenEnchantQuests;
import io.github.battlepass.quests.quests.external.USkyBlockQuests;
import io.github.battlepass.quests.quests.external.UltraSkyWarsQuests;
import io.github.battlepass.quests.quests.external.VotifierQuests;
import io.github.battlepass.quests.quests.external.chestshop.ChestShopQuests;
import io.github.battlepass.quests.quests.external.chestshop.LegacyChestShopQuests;
import io.github.battlepass.quests.quests.internal.BlockBreakQuest;
import io.github.battlepass.quests.quests.internal.BlockPlaceQuest;
import io.github.battlepass.quests.quests.internal.ChatQuest;
import io.github.battlepass.quests.quests.internal.ClickQuest;
import io.github.battlepass.quests.quests.internal.ConsumeQuest;
import io.github.battlepass.quests.quests.internal.CraftQuest;
import io.github.battlepass.quests.quests.internal.DamageQuest;
import io.github.battlepass.quests.quests.internal.EnchantQuests;
import io.github.battlepass.quests.quests.internal.ExecuteCommandQuest;
import io.github.battlepass.quests.quests.internal.FishingQuest;
import io.github.battlepass.quests.quests.internal.GainExpQuest;
import io.github.battlepass.quests.quests.internal.ItemBreakQuest;
import io.github.battlepass.quests.quests.internal.KillMobQuest;
import io.github.battlepass.quests.quests.internal.KillPlayerQuest;
import io.github.battlepass.quests.quests.internal.LoginQuest;
import io.github.battlepass.quests.quests.internal.MilkQuest;
import io.github.battlepass.quests.quests.internal.MovementQuests;
import io.github.battlepass.quests.quests.internal.PlayTimeQuest;
import io.github.battlepass.quests.quests.internal.ProjectileQuest;
import io.github.battlepass.quests.quests.internal.RegenerateQuest;
import io.github.battlepass.quests.quests.internal.RideMobQuest;
import io.github.battlepass.quests.quests.internal.ShearSheepQuest;
import io.github.battlepass.quests.quests.internal.SmeltQuest;
import io.github.battlepass.quests.quests.internal.TameQuest;
import io.github.battlepass.quests.service.base.ExternalQuestContainer;
import io.github.battlepass.quests.service.base.QuestContainer;
import io.github.battlepass.registry.quest.object.PluginVersion;
import me.hyfe.simplespigot.tuple.ImmutablePair;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class QuestRegistryImpl implements QuestRegistry {
    private final BattlePlugin plugin;
    private final PluginManager manager;
    private final List<String> disabledHooks;
    private final Set<String> registeredHooks = Sets.newHashSet();
    private final Map<String, ImmutablePair<AtomicInteger, BukkitTask>> attempts = Maps.newHashMap();

    public QuestRegistryImpl(BattlePlugin plugin) {
        this.plugin = plugin;
        this.manager = Bukkit.getPluginManager();

        this.disabledHooks = plugin.getConfig("settings").stringList("disabled-plugin-hooks");
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
        this.hook("ChestShop", ChestShopQuests::new, "https://github.com/ChestShop-authors/ChestShop-3/contributors", version ->
                version.getMajor() >= 3 && version.getMinor() > 9);
        this.hook("ChestShop", LegacyChestShopQuests::new, "https://github.com/ChestShop-authors/ChestShop-3/contributors", version ->
                version.getMajor() == 3 && version.getMinor() <= 9);
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
        this.hook("ShopGuiPlus", ShopGuiPlusQuests::new);
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

    @Override
    public Set<String> getRegisteredHooks() {
        return this.registeredHooks;
    }

    @Override
    public List<String> getDisabledHooks() {
        return this.disabledHooks;
    }

    @SafeVarargs
    @Override
    public final void quest(Instantiator<QuestContainer>... instantiators) {
        for (Instantiator<?> instantiator : instantiators) {
            Bukkit.getPluginManager().registerEvents(instantiator.init(this.plugin), this.plugin);
        }
    }

    @Override
    public boolean hook(String name, Instantiator<ExternalQuestContainer> instantiator, String author) {
        if (this.isHookDisabled(name)) {
            return false;
        }
        Plugin plugin = this.manager.getPlugin(name);
        if (plugin != null && plugin.isEnabled()) {
            if (!this.isHookDisabled(name) && (author.isEmpty() || plugin.getDescription().getAuthors().contains(author))) {
                this.manager.registerEvents(instantiator.init(this.plugin), this.plugin);
                BattlePlugin.logger().info("Hooked into ".concat(name));
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

    @Override
    public boolean hook(String name, Instantiator<ExternalQuestContainer> instantiator, String author, Predicate<PluginVersion> versionPredicate) {
        if (this.isHookDisabled(name)) {
            return false;
        }
        Plugin plugin = this.manager.getPlugin(name);
        if (plugin != null && plugin.isEnabled()) {
            if (!this.isHookDisabled(name) && (author.isEmpty() || plugin.getDescription().getAuthors().contains(author))) {
                PluginVersion version = this.extractVersion(plugin);
                BattlePlugin.logger().info("Using internal version as " + version.toString() + " for loading " + name + ".");
                if (versionPredicate.test(version)) {
                    this.manager.registerEvents(instantiator.init(this.plugin), this.plugin);
                    BattlePlugin.logger().info("Hooked into ".concat(name));
                    this.registeredHooks.add(name);
                } else {
                    BattlePlugin.logger().info(name.concat(" was present but its version is not supported."));
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

    private PluginVersion extractVersion(Plugin plugin) {
        String version = plugin.getDescription().getVersion().split(" ")[0]; // 3.2.6 Build 20 -> 3.2.6
        String[] splitVersion = version.split("\\.");
        int major = 0;
        int minor = 0;
        int bugfix = 0;
        if (splitVersion.length == 3) {
            major = this.entryToVersionNumber(splitVersion[0]);
            minor = this.entryToVersionNumber(splitVersion[1]);
            bugfix = this.entryToVersionNumber(splitVersion[2]);
        } else if (splitVersion.length == 2) {
            major = this.entryToVersionNumber(splitVersion[0]);
            minor = this.entryToVersionNumber(splitVersion[1]);
        } else if (splitVersion.length == 1) {
            major = this.entryToVersionNumber(splitVersion[0]);
        }
        return new PluginVersion()
                .setMajor(major)
                .setMinor(minor)
                .setBugfix(bugfix);
    }

    private int entryToVersionNumber(String section) {
        StringBuilder sanitizedSection = new StringBuilder();
        for (char character : section.toCharArray()) {
            if (Character.isDigit(character)) {
                sanitizedSection.append(character);
            }
        }
        return Integer.parseInt(sanitizedSection.toString());
    }
}