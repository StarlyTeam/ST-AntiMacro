package net.starly.antimacro.command;

import net.starly.antimacro.AntiMacroMain;
import net.starly.antimacro.context.MessageContent;
import net.starly.antimacro.context.MessageType;
import net.starly.antimacro.util.MacroUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MacroExecutor implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MessageContent content = MessageContent.getInstance();
        AntiMacroMain plugin = AntiMacroMain.getInstance();

        if (!sender.hasPermission("starly.antimacro.reload") && !sender.hasPermission("starly.antimacro.check")) {
            content.getMessageAfterPrefix(MessageType.ERROR, "permissionDenieed").ifPresent(sender::sendMessage);
            return false;
        }

        if (args.length < 1) {
            content.getMessages(MessageType.NORMAL, "helpMessage").forEach(sender::sendMessage);
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("리로드")) {
            if (isPermissionDenied(sender, "starly.antimacro.reload")) return false;

            plugin.reloadConfig();
            content.initialize(plugin.getConfig());
            content.getMessageAfterPrefix(MessageType.NORMAL, "reloadCompleted").ifPresent(sender::sendMessage);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("확인")) {
            if (isPermissionDenied(sender, "starly.antimacro.check")) return false;

            Player target = plugin.getServer().getPlayer(args[1]);
            if (target == null) {
                content.getMessageAfterPrefix(MessageType.ERROR, "noExistPlayer").ifPresent(sender::sendMessage);
                return false;
            }

            MacroUtil.checkMacro(target);
            content.getMessageAfterPrefix(MessageType.NORMAL, "macroCommand").ifPresent(sender::sendMessage);
        } else {
            content.getMessages(MessageType.NORMAL, "helpMessage").forEach(sender::sendMessage);
        }

        return false;
    }

    private boolean isPermissionDenied(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) {
            return false;
        }

        MessageContent.getInstance().getMessageAfterPrefix(MessageType.ERROR, "permissionDenied").ifPresent(sender::sendMessage);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("starly.antimacro.reload") && !sender.hasPermission("starly.antimacro.check")) return Collections.emptyList();

        if (args.length == 1) return Arrays.asList("리로드","확인");
        if (args.length == 2 && args[0].equalsIgnoreCase("확인")) return null;

        return Collections.emptyList();
    }
}
