package io.github.battlepass.quests.workers.pipeline.steps;

import com.google.common.collect.Lists;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.actions.Action;
import io.github.battlepass.controller.QuestController;
import io.github.battlepass.lang.Lang;
import io.github.battlepass.objects.quests.Quest;
import io.github.battlepass.objects.user.User;
import me.hyfe.simplespigot.config.Config;
import me.hyfe.simplespigot.text.Replacer;
import me.hyfe.simplespigot.text.Text;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CompletionStep {
    private final List<Action> completionActions = Lists.newArrayList();
    private final BattlePlugin plugin;
    private final Config settingsConfig;
    private final RewardStep rewardStep;
    private final QuestController controller;
    private final Lang lang;

    public CompletionStep(BattlePlugin plugin) {
        this.completionActions.addAll(plugin.getConfig("settings").stringList("quest-completed-actions").stream().map(Action::parse).collect(Collectors.toList()));
        this.plugin = plugin;
        this.settingsConfig = plugin.getConfig("settings");
        this.rewardStep = new RewardStep(plugin);
        this.controller = plugin.getQuestController();
        this.lang = plugin.getLang();
    }

    public void process(User user, Quest quest, BigInteger originalProgress, BigInteger progressIncrement, boolean overrideUpdate) {
        Optional<Player> maybePlayer = Optional.ofNullable(Bukkit.getPlayer(user.getUuid()));
        BigInteger newTotalProgress = progressIncrement.min(quest.getRequiredProgress());
        BigInteger updatedProgress = overrideUpdate ? this.controller.setQuestProgress(user, quest, newTotalProgress) : this.controller.addQuestProgress(user, quest, newTotalProgress);
        String methodType = this.settingsConfig.string("current-season.notification-method");
        maybePlayer.ifPresent(player -> {
            Action.executeSimple(player, this.completionActions, this.plugin, new Replacer().set("player", player.getName()).set("quest_name", quest.getName()).set("quest_category", quest.getCategoryId()));
            for (BigInteger notifyAt : quest.getNotifyAt()) {
                int compared = updatedProgress.compareTo(notifyAt);
                if (compared == 0 || (notifyAt.compareTo(originalProgress) > 0 && compared > -1)) {
                    String message = this.lang.questProgressedMessage(quest, updatedProgress);
                    if (methodType.contains("chat")) {
                        Text.sendMessage(player, message);
                    }
                    if (methodType.contains("action bar")) {
                        this.sendActionBar(player, message);
                    }
                    break;
                }
            }
        });
        //this.questValidationStep.notifyPipelineCompletion(user.getUuid(), quest.getId());

        if (this.controller.isQuestDone(user, quest)) {
            if (updatedProgress.compareTo(quest.getRequiredProgress()) > -1) {
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