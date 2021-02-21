package io.github.battlepass.registry.quest;

import io.github.battlepass.quests.service.base.ExternalQuestContainer;
import io.github.battlepass.quests.service.base.QuestContainer;
import io.github.battlepass.registry.quest.object.PluginVersion;
import me.hyfe.simplespigot.annotations.NotNull;
import me.hyfe.simplespigot.registry.Registry;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public interface QuestRegistry extends Registry {

    /**
     * This will return the hooks that are <b>currently</b> enabled. Due to the late hook system,
     * this should not be depended upon as a full list at start.
     *
     * @return The hooks currently enabled
     */
    @NotNull
    Set<String> getRegisteredHooks();

    /**
     * @return The hooks explicitly disabled via the settings.yml value
     */
    @NotNull
    List<String> getDisabledHooks();

    /**
     * This does not check simply if the hook is not registered, it checks whether it's explicitly
     * disabled via the settings.yml. To see if a hook is registered, see {@link QuestRegistry#getRegisteredHooks}
     *
     * @param plugin The identifier name of the plugin - should be what it is called in game.
     * @return Whether the hook is specified disabled
     */
    default boolean isHookDisabled(String plugin) {
        return this.getDisabledHooks().contains(plugin.toLowerCase());
    }

    /**
     * Initializes a quest as well as registers it as a Bukkit listener.
     * If your class is not a bukkit listener, please initialize the class manually
     */
    void quest(Instantiator<QuestContainer>... instantiators);

    /**
     * Attempts to hook into a plugin and will automatically retry if the plugin is not found (might not yet be loaded).
     * This should be used to register external plugin hooks.
     *
     * @param name         The name of the plugin
     * @param instantiator The instantiator to initialize the quest if appropriate
     * @param author       The author to filter by. If empty it will not filter by the author
     * @return Whether the hook was successful. Bear in mind it may not be successful initially but BattlePass will continue to try register it.
     */
    boolean hook(String name, Instantiator<ExternalQuestContainer> instantiator, String author);

    /**
     * Same as {@link QuestRegistry#hook(String, Instantiator, String)} without the author filter.
     */
    default void hook(String name, Instantiator<ExternalQuestContainer> instantiator) {
        this.hook(name, instantiator, "");
    }

    /**
     * Same as {@link QuestRegistry#hook(String, Instantiator, String)} but it also checks the version of the plugin.
     *
     * @param versionPredicate A tester to check if the version is x. Whatever you want with it.
     */
    boolean hook(String name, Instantiator<ExternalQuestContainer> instantiator, String author, Predicate<PluginVersion> versionPredicate);


    /**
     * Same as {@link QuestRegistry#hook(String, Instantiator, String, Predicate)} but without the author check.
     */
    default void hook(String name, Instantiator<ExternalQuestContainer> instantiator, Predicate<PluginVersion> versionPredicate) {
        this.hook(name, instantiator, "", versionPredicate);
    }
}
