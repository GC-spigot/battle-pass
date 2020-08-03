package io.github.battlepass.placeholders;


import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.loader.PassLoader;
import io.github.battlepass.objects.user.User;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hyfe.simplespigot.uuid.FastUuid;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.logging.Level;

public class PlaceholderApiHook extends PlaceholderExpansion {
    private UserCache userCache;
    private PassLoader passLoader;

    public PlaceholderApiHook(BattlePlugin plugin) {
        this.userCache = plugin.getUserCache();
        this.passLoader = plugin.getPassLoader();
        Bukkit.getLogger().log(Level.FINE, "[BattlePass] Register PlaceholderAPI placeholders");
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String placeholder) {
        if (placeholder.equals("test")) {
            return "successful";
        }
        if (offlinePlayer == null) {
            Bukkit.getLogger().log(Level.WARNING, "Could not get placeholder ".concat(placeholder).concat(" for user ").concat(FastUuid.toString(offlinePlayer.getUniqueId())).concat(" (player null)"));
            return "???";
        }
        Optional<User> optionalUser = this.userCache.getSync(offlinePlayer.getUniqueId());
        if (!optionalUser.isPresent()) {
            return "??? User not present";
        }
        User user = optionalUser.get();
        switch (placeholder) {
            case "points":
            case "experience":
                return user.getPoints().toString();
            case "tier":
                return String.valueOf(user.getTier());
            case "pass_type":
                return this.passLoader.passTypeOfId(user.getPassId()).getName();
            case "pass_id":
                return user.getPassId();
        }
        return "Invalid Placeholder";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "battlepass";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Hyfe/Zak";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.1";
    }

    public void reload(BattlePlugin plugin) {
        this.userCache = plugin.getUserCache();
        this.passLoader = plugin.getPassLoader();
    }
}
