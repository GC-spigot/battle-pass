package io.github.battlepass.quests.workers.pipeline.steps;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.controller.QuestController;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.text.Text;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

public class CompletionStep {
    private final Config settingsConfig;
    private final RewardStep rewardStep;
    private final QuestController controller;
    private final Lang lang;

    public CompletionStep(BattlePlugin plugin) {
        this.settingsConfig = plugin.getConfig("settings");
        this.rewardStep = new RewardStep(plugin);
        this.controller = plugin.getQuestController();
        this.lang = plugin.getLang();
    }

    public void process(User user, Quest quest, int originalProgress, int progressIncrement, boolean overrideUpdate) {
        Optional<Player> maybePlayer = Optional.ofNullable(Bukkit.getPlayer(user.getUuid()));
        int newTotalProgress = Math.min(progressIncrement, quest.getRequiredProgress());
        int updatedProgress = overrideUpdate ? this.controller.setQuestProgress(user, quest, newTotalProgress) : this.controller.addQuestProgress(user, quest, newTotalProgress);
        String methodType = this.settingsConfig.string("current-season.notification-method");
        for (int notifyAt : quest.getNotifyAt()) {
            if (updatedProgress == notifyAt || (notifyAt > originalProgress && notifyAt < updatedProgress)) {
                String message = this.lang.questProgressedMessage(quest, updatedProgress);
                maybePlayer.ifPresent(player -> {
                    if (methodType.contains("chat")) {
                        Text.sendMessage(player, message);
                    }
                    if (methodType.contains("action bar")) {
                        this.sendActionBar(player, message);
                    }
                });
                break;
            }
        }
        if (this.controller.isQuestDone(user, quest)) {
            if (updatedProgress >= quest.getRequiredProgress()) {
                String message = this.lang.questCompleteMessage(quest);
                maybePlayer.ifPresent(player -> {
                    if (methodType.contains("chat")) {
                        Text.sendMessage(player, message);
                    }
                    if (methodType.contains("action bar")) {
                        this.sendActionBar(player, message);
                    }
                });
            }
            this.rewardStep.process(user, quest);
        }
    }

    // Credits to Benz
    private void sendActionBar(Player player, String message) {
        if (player == null || message == null) {
            return;
        }
        String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);
        if (!nmsVersion.startsWith("v1_9_R") && !nmsVersion.startsWith("v1_8_R")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            return;
        }
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);

            Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
            Class<?> packet = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
            Object packetPlayOutChat;
            Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion + (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
            Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");

            Method method = null;
            if (nmsVersion.equalsIgnoreCase("v1_8_R1")) method = chat.getDeclaredMethod("a", String.class);

            Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[]{String.class}).newInstance(message);
            packetPlayOutChat = ppoc.getConstructor(new Class[]{chatBaseComponent, Byte.TYPE}).newInstance(object, (byte) 2);

            Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
            Object iCraftPlayer = handle.invoke(craftPlayer);
            Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(iCraftPlayer);
            Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packet);
            sendPacket.invoke(playerConnection, packetPlayOutChat);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}