package io.github.battlepass.service;

import io.github.battlepass.lang.Lang;
import lombok.experimental.UtilityClass;
import me.hyfe.simplespigot.text.Text;
import me.hyfe.simplespigot.version.ServerVersion;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

@UtilityClass
public class Services {

    public static void sendActionBar(Player player, String message) {
        String serverVersion = ServerVersion.getVersion().toString().replace("MC", "v");
        if (!serverVersion.startsWith("v1_9_R") && !serverVersion.startsWith("v1_8_R")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            return;
        }
        try {
            Class<?> playerClass = Class.forName("org.bukkit.craftbukkit." + serverVersion + ".entity.CraftPlayer");
            Object craftPlayer = playerClass.cast(player);

            Class<?> packetPlayOutChatClass = Class.forName("net.minecraft.server." + serverVersion + ".PacketPlayOutChat");
            Class<?> packetClass = Class.forName("net.minecraft.server." + serverVersion + ".Packet");
            Class<?> chat = Class.forName("net.minecraft.server." + serverVersion + (serverVersion.contains("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
            Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + serverVersion + ".IChatBaseComponent");

            Method method = null;
            if (serverVersion.contains("v1_8_R1")) {
                method = chat.getDeclaredMethod("a", String.class);
            }
            Object object = serverVersion.contains("v1_8_R1") ? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[]{String.class}).newInstance(message);
            Object packetPlayOutChat = packetPlayOutChatClass.getConstructor(new Class[]{chatBaseComponent, Byte.TYPE}).newInstance(object, (byte) 2);
            Method handle = playerClass.getDeclaredMethod("getHandle");
            Object iCraftPlayer = handle.invoke(craftPlayer);
            Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(iCraftPlayer);
            Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packetClass);
            sendPacket.invoke(playerConnection, packetPlayOutChat);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getPercentage(BigInteger progress, BigInteger requiredProgress) {
        return new BigDecimal(progress).divide(new BigDecimal(requiredProgress), MathContext.DECIMAL32).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_EVEN).toString(); // TODO probs broken
    }

    public static String getProgressBar(BigInteger progress, BigInteger requiredProgress, Lang lang) {
        float progressFloat = new BigDecimal(progress).divide(new BigDecimal(requiredProgress), MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_EVEN).floatValue();
        float complete = 30 * progressFloat;
        float incomplete = 30 - complete;
        String progressBar = Text.modify(lang.external("progress-bar.complete-color").asString());
        for (int i = 0; i < complete; i++) {
            progressBar = progressBar.concat(lang.external("progress-bar.symbol").asString());
        }
        progressBar = progressBar.concat(lang.external("progress-bar.incomplete-color").asString());
        for (int i = 0; i < incomplete; i++) {
            progressBar = progressBar.concat(lang.external("progress-bar.symbol").asString());
        }
        return progressBar;
    }
}
