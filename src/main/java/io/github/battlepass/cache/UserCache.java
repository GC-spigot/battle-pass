package io.github.battlepass.cache;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.events.user.UserLoadEvent;
import io.github.battlepass.exceptions.NoOnlineUserException;
import io.github.battlepass.loader.PassLoader;
import io.github.battlepass.objects.pass.PassType;
import io.github.battlepass.objects.user.User;
import me.hyfe.simplespigot.cache.FutureCache;
import me.hyfe.simplespigot.save.Savable;
import me.hyfe.simplespigot.storage.storage.Storage;
import me.hyfe.simplespigot.text.Text;
import me.hyfe.simplespigot.uuid.FastUuid;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class UserCache extends FutureCache<UUID, User> implements Savable {
    private final BattlePlugin plugin;
    private final Storage<User> storage;
    private final PassLoader passLoader;

    public UserCache(BattlePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        this.storage = plugin.getUserStorage();
        this.passLoader = plugin.getPassLoader();
    }

    public User getOrThrow(UUID uuid) throws NoOnlineUserException {
        User user = this.getSync(uuid).orElse(null);
        if (user == null) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                throw new NoOnlineUserException("Could not find an online user with the uuid ".concat(FastUuid.toString(uuid)));
            } else {
                Text.sendMessage(player, "&ePlease re-log for this feature to work. We're sorry for the inconvenience.");
            }
            return null;
        }
        this.applyPermissionComputation(user);
        return user;
    }

    public CompletableFuture<User> load(UUID uuid) {
        return this.get(uuid).thenApply(maybeUser -> {
            if (!maybeUser.isPresent()) {
                User user = this.storage.load(FastUuid.toString(uuid));
                boolean newUser = false;
                if (user == null) {
                    user = new User(uuid);
                    newUser = true;
                }
                this.set(uuid, user);
                UserLoadEvent event = new UserLoadEvent(user, newUser);
                this.plugin.runSync(() -> Bukkit.getPluginManager().callEvent(event));
                return user;
            }
            return maybeUser.get();
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    public void asyncModifyMultiple(Consumer<User> consumer, Set<UUID> uuids) {
        for (UUID uuid : uuids) {
            this.load(uuid).thenAccept(consumer);
        }
    }

    public void asyncModifyAll(Consumer<User> consumer) {
        this.plugin.runAsync(() -> {
            for (User user : this.values()) {
                consumer.accept(user);
            }
            for (User user : this.storage.loadAll()) {
                if (user == null) {
                    continue;
                }
                if (!this.keySet().contains(user.getUuid())) {
                    consumer.accept(user);
                    this.storage.save(user.getUuid().toString(), user);
                }
            }
        });
    }

    public void unload(UUID uuid, boolean invalidate) {
        this.get(uuid).thenAccept(maybeUser -> {
            maybeUser.ifPresent(user -> this.storage.save(FastUuid.toString(user.getUuid()), user));
            if (invalidate) {
                this.invalidate(uuid);
            }
        });
    }

    public void loadOnline() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.load(player.getUniqueId());
        }
    }

    @Override
    public void save() {
        if (this.plugin.isEnabled()) {
            this.plugin.runAsync(() -> {
                for (User user : this.values()) {
                    this.storage.save(FastUuid.toString(user.getUuid()), user);
                }
            });
        } else {
            for (User user : this.values()) {
                this.storage.save(FastUuid.toString(user.getUuid()), user);
            }
        }
    }

    private void applyPermissionComputation(User user) {
        Player player = Bukkit.getPlayer(user.getUuid());
        if (player == null) {
            return;
        }
        PassType passType = this.passLoader.passTypeOfId("premium");
        if (passType.getRequiredPermission() == null) {
            return;
        }
        if (!user.getPassId().equals(passType.getId()) && player.hasPermission(passType.getRequiredPermission())) {
            this.plugin.getLocalApi().setPassId(user, "premium");
        } else if (user.getPassId().equals(passType.getId()) && !player.hasPermission(passType.getRequiredPermission())) {
            user.setPassId("free");
        }
    }
}
