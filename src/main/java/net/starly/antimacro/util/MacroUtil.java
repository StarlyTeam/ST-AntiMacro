package net.starly.antimacro.util;

import net.starly.antimacro.AntiMacroMain;
import net.starly.antimacro.context.MessageContent;
import net.starly.antimacro.context.MessageType;
import net.starly.antimacro.manager.StackManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class MacroUtil {

    public static void checkMacro(Player p) {
        if (!p.isOnline()) return;

        final String checkNumber = String.valueOf(new Random().nextInt(88888) + 11111);
        MessageContent.getInstance().getMessageAfterPrefix(MessageType.NORMAL, "macroMessage")
                .ifPresent(message -> p.sendMessage(message.replaceAll("%number%",checkNumber)));
        MessageContent.getInstance().getMessage(MessageType.CONFIG, "sound")
                .ifPresent(string -> {
                    String[] splitedString = string.split("/",2);
                    Sound sound;
                    float pitch;

                    try {
                        if (splitedString.length == 1) pitch = 1f;
                        else pitch = Float.parseFloat(splitedString[1]);
                    } catch (NumberFormatException exception) {
                        pitch = 1f;
                    }

                    try {
                        sound = Sound.valueOf(splitedString[0]);
                    } catch (IllegalArgumentException exception) {
                        return;
                    }

                    p.playSound(p.getLocation(), sound, Float.MAX_VALUE, pitch);
                });

        final AtomicReference<Listener> chatListener = new AtomicReference<>(new Listener() {});
        AntiMacroMain.getInstance().getServer().getPluginManager().registerEvent(AsyncPlayerChatEvent.class, chatListener.get(), EventPriority.LOWEST, (listener, event) -> {
            if (!(event instanceof AsyncPlayerChatEvent)) return;
            AsyncPlayerChatEvent chatEvent = (AsyncPlayerChatEvent) event;
            chatEvent.setCancelled(true);

            if (checkNumber.equalsIgnoreCase(chatEvent.getMessage())) {
                MessageContent.getInstance().getMessageAfterPrefix(MessageType.NORMAL, "verifiedMessage").ifPresent(p::sendMessage);
                HandlerList.unregisterAll(chatListener.get());
                chatListener.set(null);
            }
            else {
                MessageContent.getInstance().getMessageAfterPrefix(MessageType.ERROR, "wrongCode").ifPresent(chatEvent.getPlayer()::sendMessage);
            }
        }, AntiMacroMain.getInstance());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (chatListener.get() == null) return;
                MessageContent.getInstance().getMessage(MessageType.NORMAL, "kickMessage").ifPresent(p::kickPlayer);
                HandlerList.unregisterAll(chatListener.get());
                chatListener.set(null);
            }
        }.runTaskLater(AntiMacroMain.getInstance(), MessageContent.getInstance().getInt(MessageType.CONFIG, "responseTimeOut") * 20L);
    }
}
