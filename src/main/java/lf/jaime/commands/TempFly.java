package lf.jaime.commands;

import lf.jaime.LightFly;
import lf.jaime.files.MessagesManager;
import lf.jaime.utils.MessageUtils;
import lf.jaime.utils.PlayerTimeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TempFly implements CommandExecutor, TabCompleter {
    private final LightFly plugin;

    public TempFly(LightFly plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        MessagesManager messages = plugin.getMessagesManager();

        // Comprobar si el jugador tiene permiso global
        if (!sender.hasPermission("lf.tempfly.*")) {
            if (!sender.hasPermission("lf.admin") &&
                    !sender.hasPermission("lf.fly.*") &&
                    !sender.hasPermission("lf.fly.temp")) {

                sender.sendMessage(MessageUtils.getMessage(messages.getNoPermission()
                        .replace("%prefix%", messages.getPrefix())));
                return true;
            }
        }

        if (args.length < 2) {
            sender.sendMessage(MessageUtils.getMessage(messages.getBadUsageTempfly()
                    .replace("%prefix%", messages.getPrefix())));
            return true;
        }

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage(MessageUtils.getMessage(messages.getUnknownPlayer()
                    .replace("%player%", args[1])
                    .replace("%prefix%", messages.getPrefix())));
            return true;
        }

        PlayerTimeManager playerTimeManager = plugin.getPlayerTimeManager();
        String action = args[0].toLowerCase();

        switch (action) {
            case "add":
                // Comprobar si el jugador tiene permiso para 'add'
                if (!sender.hasPermission("lf.tempfly.add") && !sender.hasPermission("lf.tempfly.*")) {
                    sender.sendMessage(MessageUtils.getMessage(messages.getNoPermission()
                    .replace("%prefix%", messages.getPrefix())));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(MessageUtils.getMessage(messages.getBadUsageTempfly()
                    .replace("%prefix%", messages.getPrefix())));
                    return true;
                }
                int timeToAdd = parseTime(args[2]);
                playerTimeManager.addPlayer(player, playerTimeManager.getRemainingTime(player) + timeToAdd);
                sender.sendMessage(MessageUtils.getMessage(messages.getTempFlyAdd()
                        .replace("%player%", player.getName())
                        .replace("%time%", args[2])
                        .replace("%prefix%", messages.getPrefix())));
                break;

            case "set":
                // Comprobar si el jugador tiene permiso para 'set'
                if (!sender.hasPermission("lf.tempfly.set") && !sender.hasPermission("lf.tempfly.*")) {
                    sender.sendMessage(MessageUtils.getMessage(messages.getNoPermission()
                    .replace("%prefix%", messages.getPrefix())));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(MessageUtils.getMessage(messages.getBadUsageTempfly()
                    .replace("%prefix%", messages.getPrefix())));
                    return true;
                }
                int timeToSet = parseTime(args[2]);
                playerTimeManager.addPlayer(player, timeToSet);
                sender.sendMessage(MessageUtils.getMessage(messages.getTempFlySet()
                        .replace("%player%", player.getName())
                        .replace("%time%", args[2])
                        .replace("%prefix%", messages.getPrefix())));
                break;

            case "remove":
                // Comprobar si el jugador tiene permiso para 'remove'
                if (!sender.hasPermission("lf.tempfly.remove") && !sender.hasPermission("lf.tempfly.*")) {
                    sender.sendMessage(MessageUtils.getMessage(messages.getNoPermission()
                    .replace("%prefix%", messages.getPrefix())));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(MessageUtils.getMessage(messages.getBadUsageTempfly()
                    .replace("%prefix%", messages.getPrefix())));
                    return true;
                }
                int timeToRemove = parseTime(args[2]);
                int currentTime = playerTimeManager.getRemainingTime(player);
                playerTimeManager.addPlayer(player, Math.max(currentTime - timeToRemove, 0));
                sender.sendMessage(MessageUtils.getMessage(messages.getTempFlyRemove()
                        .replace("%player%", player.getName())
                        .replace("%time%", args[2])
                        .replace("%prefix%", messages.getPrefix())));
                break;

            case "check":
                // Comprobar si el jugador tiene permiso para 'check'
                if (!sender.hasPermission("lf.tempfly.check") && !sender.hasPermission("lf.tempfly.*")) {
                    sender.sendMessage(MessageUtils.getMessage(messages.getNoPermission()
                    .replace("%prefix%", messages.getPrefix())));
                    return true;
                }
                int remainingTime = playerTimeManager.getRemainingTime(player);
                sender.sendMessage(MessageUtils.getMessage(messages.getTempFlyCheck()
                        .replace("%player%", player.getName())
                        .replace("%time%", String.valueOf(remainingTime))
                        .replace("%prefix%", messages.getPrefix())));
                break;

            default:
                sender.sendMessage(MessageUtils.getMessage(messages.getBadUsageTempfly()
                .replace("%prefix%", messages.getPrefix())));
                break;
        }

        return true;
    }

    private int parseTime(String timeArg) {
        int timeInSeconds = 0;
        String[] parts = timeArg.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
        for (int i = 0; i < parts.length; i += 2) {
            int value = Integer.parseInt(parts[i]);
            String unit = parts[i + 1].toLowerCase();
            switch (unit) {
                case "s":
                case "sec":
                    timeInSeconds += value;
                    break;
                case "m":
                case "min":
                    timeInSeconds += value * 60;
                    break;
                case "h":
                case "hour":
                    timeInSeconds += value * 3600;
                    break;
                case "d":
                case "day":
                    timeInSeconds += value * 86400;
                    break;
                case "month":
                    timeInSeconds += value * 2592000;
                    break;
                case "y":
                case "year":
                    timeInSeconds += value * 31536000;
                    break;
            }
        }
        return timeInSeconds;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> actions = new ArrayList<>();
            actions.add("add");
            actions.add("set");
            actions.add("remove");
            actions.add("check");
            return actions;
        }

        if (args.length == 2) {
            List<String> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                players.add(player.getName());
            }
            return players;
        }

        if (args.length == 3 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("remove"))) {
            List<String> timeSuggestions = new ArrayList<>();
            timeSuggestions.add("10s");
            timeSuggestions.add("27m11s");
            timeSuggestions.add("1m");
            timeSuggestions.add("4h27m");
            timeSuggestions.add("1h");
            timeSuggestions.add("1d4h");
            timeSuggestions.add("1d");
            timeSuggestions.add("2month1d");
            timeSuggestions.add("1month");
            timeSuggestions.add("1y2month");
            timeSuggestions.add("1y");
            timeSuggestions.add("1y2month1d4h27m11s");
            return timeSuggestions;
        }

        return null;
    }
}
