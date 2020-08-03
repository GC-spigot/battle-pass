package io.github.battlepass.placeholders;


import io.github.battlepass.BattlePlugin;
import io.github.battlepass.cache.UserCache;
import io.github.battlepass.loader.PassLoader;
import io.github.battlepass.objects.user.User;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hyfe.simplespigot.uuid.FastUuid;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
    public String onPlaceholderRequest(Player player, String placeholder) {
        if (placeholder.equals("test")) {
            return "successful";
        }
        if (player == null) {
            Bukkit.getLogger().log(Level.WARNING, "Could not get placeholder ".concat(placeholder).concat(" for user ").concat(FastUuid.toString(player.getUniqueId())).concat(" (player null)"));
            return "???";
        }
        Optional<User> optionalUser = this.userCache.getSync(player.getUniqueId());
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
    public String getIdentifier() {
        return "battlepass";
    }

    @Override
    public String getAuthor() {
        return "Hyfe/Zak";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    public void reload(BattlePlugin plugin) {
        this.userCache = plugin.getUserCache();
        this.passLoader = plugin.getPassLoader();
    }
}
