package io.github.battlepass.api;

import io.github.battlepass.objects.pass.Tier;
import io.github.battlepass.objects.reward.Reward;
import io.github.battlepass.objects.user.User;
import io.github.battlepass.registry.quest.QuestRegistry;
import me.hyfe.simplespigot.annotations.NotNull;
import me.hyfe.simplespigot.annotations.Nullable;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public interface BattlePassApi {

    /**
     * @return the {@link QuestRegistry} which should be used to register internal & external quests.
     */
    @NotNull
    QuestRegistry getQuestRegistry();

    /**
     * This will only get a user from the cache (they must be online and in the cache).
     *
     * @param uuid The UUID of the user to get
     * @return A {@link CompletableFuture} containing an optional User. The user will not be present if they are not loaded in the cache.
     */
    @NotNull
    CompletableFuture<Optional<User>> getUser(UUID uuid);

    /**
     * This will get, load or create a user. Use this method if you may also be getting an offline user.
     *
     * @return A {@link CompletableFuture} containing the user. This user will only be null in erroneous circumstances.
     * @since 3.13
     */
    @NotNull
    CompletableFuture<User> getOrLoadUser(UUID uuid);

    /**
     * Gets a reward by its specified ID in the config file
     *
     * @param rewardId The ID in the rewards config file
     * @return A {@link Reward<String>} if it is a command or {@link Reward<org.bukkit.inventory.ItemStack>} if it is an item.
     */
    @NotNull
    Optional<Reward<?>> getReward(String rewardId);

    /**
     * @return Whether the weeks since the start date is greater than the amount of weeks.
     */
    boolean hasSeasonEnded();

    /**
     * @return Whether the season start date is after the current date.
     */
    boolean hasSeasonStarted();

    /**
     * If the season has finished and the season-finished-message is present in lang, it will use that value.
     * If it is not present it will display the output of {@link BattlePassApi#getCurrentDisplayWeek}.
     * {@link BattlePassApi#getCurrentDisplayWeek} will also be displayed if the season has not yet finished.
     */
    @NotNull
    String getWeekFormatted();

    /**
     * @return The ZoneID parsed from the specified season timezone in the settings.yml
     */
    @NotNull
    ZoneId getZone();

    /**
     * Replaced with {@link BattlePassApi#getCurrentWeek}
     */
    @Deprecated
    default long currentWeek() {
        return this.getCurrentDisplayWeek();
    }

    /**
     * @return The amount of weeks since the start of the season date.
     */
    long getCurrentWeek();

    /**
     * Replaced with {@link BattlePassApi#getCurrentDisplayWeek}
     */
    @Deprecated
    default long currentDisplayWeek() {
        return this.getCurrentDisplayWeek();
    }

    /**
     * @return {@link BattlePassApi#getCurrentWeek} but accounting for the max possible week. It cannot be larger than the number of configured weeks
     */
    long getCurrentDisplayWeek();

    /**
     * @return The end date of the season calculated as the number of weeks configured since {@link BattlePassApi#getSeasonStartDate}
     */
    ZonedDateTime getSeasonEndDate();

    /**
     * @return The start date of the season set in the settings.yml
     */
    ZonedDateTime getSeasonStartDate();

    /**
     * Fetches the pass from the pass id and sets the user's pass to it.
     *
     * @param user   The {@link User} to set the pass for
     * @param passId The pass ID - free or premium
     * @throws io.github.battlepass.exceptions.PassNotFoundException if the ID is not 'free' or 'premium'
     */
    void setPassId(@NotNull User user, @NotNull String passId);

    /**
     * @param tier   The tier number to get
     * @param passId The passId to get the tier for.
     * @return The {@link Tier} model for the specified tier and passId. Returns null if there is no specific tier specified.
     * @throws NullPointerException if the pass ID could not be found
     */
    @Nullable
    Tier getTier(@NotNull int tier, @NotNull String passId);

    /**
     * If the tier is not present it will return the default points required for the pass type
     *
     * @param tier   The tier number to get
     * @param passId The passId to get the amount of points for
     * @return The required points for the specified tier
     * @throws NullPointerException if the pass ID could not be found
     */
    int getRequiredPoints(@NotNull int tier, @NotNull String passId);

    /**
     * @param user   The {@link User} to give points to
     * @param points The amount of points to give the user
     */
    void givePoints(@NotNull User user, @NotNull int points);

    /**
     * Updates the tier of a user / fixes it if they're over the points of that tier.
     * Say they have 100/50 points, it will tier them up and they will have 50 points
     *
     * @param user The user to update their tier for.
     */
    void updateUserTier(@NotNull User user);

    /**
     * Gives rewards to a user or queues if they are offline/rewards arent auto given
     *
     * @param user               The {@link User} to give rewards to
     * @param tier               The tier number to give rewards for (will do both free and premium)
     * @param ignoreRestrictions If true, rewards will be forcefully given even if users do not automatically receive rewards (as long as the player is online)
     */
    void reward(@NotNull User user, @NotNull int tier, @NotNull boolean ignoreRestrictions);


    /**
     * Gives rewards to a user or queues if they are offline/rewards auto given
     * The reward will only be given for the specified pass type with this method.
     *
     * @param user               The {@link User} to give rewards to
     * @param passId             The pass type to give rewards for (free or premium)
     * @param tier               The tier number to give rewards for (will do both free and premium)
     * @param ignoreRestrictions If true, rewards will be forcefully given even if users do not automatically receive rewards (as long as the player is online)
     */
    void reward(@NotNull User user, @NotNull String passId, @NotNull int tier, @NotNull boolean ignoreRestrictions);

    /**
     * Calculates and rewards the player with currency if enabled in the settings.yml
     * Will either give internal currency or external currency based on the user's settings.
     *
     * @param user   The {@link User} to give currency to
     * @param points The amount of points to convert into currency. These will not be automatically removed from the player.
     */
    void rewardCurrency(@NotNull User user, @NotNull int points);
}
