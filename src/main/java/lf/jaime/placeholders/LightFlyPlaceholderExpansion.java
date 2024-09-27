package lf.jaime.placeholders;

import lf.jaime.LightFly;
import lf.jaime.utils.PlayerTimeManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LightFlyPlaceholderExpansion extends PlaceholderExpansion {
    private final LightFly plugin;

    public LightFlyPlaceholderExpansion(LightFly plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Jaime"; //
    }

    @Override
    public @NotNull String getIdentifier() {
        return "lightfly"; //
    }

    @Override
    public @NotNull String getVersion() {
        return Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("LightFly")).getDescription().getVersion(); //
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return null;
        }

        PlayerTimeManager playerTimeManager = plugin.getPlayerTimeManager();

        // Placeholder existente: tempfly_time
        if (params.equals("tempfly_time")) {
            return String.valueOf(playerTimeManager.getRemainingTime(player));
        }

        // Placeholder existente: tempfly_time_<player>
        if (params.startsWith("tempfly_time_")) {
            String playerName = params.substring("tempfly_time_".length());
            Player targetPlayer = Bukkit.getPlayer(playerName);
            if (targetPlayer == null) { return null; }
            return String.valueOf(playerTimeManager.getRemainingTime(targetPlayer));
        }

        // Placeholder existente: isflying
        if (params.equals("isflying")) {
            return String.valueOf(player.isFlying());
        }

        // Placeholder existente: isflying_<player>
        if (params.startsWith("isflying_")) {
            String playerName = params.substring("isflying_".length());
            Player targetPlayer = Bukkit.getPlayer(playerName);
            if (targetPlayer == null) { return null; }
            return String.valueOf(targetPlayer.isFlying());
        }

        // Placeholder existente: flymode
        if (params.equals("flymode")) {
            return String.valueOf(player.getAllowFlight());
        }

        // Placeholder existente: flymode_<player>
        if (params.startsWith("flymode_")) {
            String playerName = params.substring("flymode_".length());
            Player targetPlayer = Bukkit.getPlayer(playerName);
            if (targetPlayer == null) { return null; }
            return String.valueOf(targetPlayer.getAllowFlight());
        }

        // NUEVO Placeholder: tempfly_time_automatic_format (Formato automático)
        if (params.equals("tempfly_automatic_format_time")) {
            int timeLeftInSeconds = playerTimeManager.getRemainingTime(player);
            return formatTimeAutomatically(timeLeftInSeconds);
        }

        // NUEVO Placeholder: tempfly_time_custom_format_<pattern> (Formato personalizado)
        if (params.startsWith("tempfly_custom_format_time_")) {
            String pattern = params.substring("tempfly_custom_format_time_".length());
            int timeLeftInSeconds = playerTimeManager.getRemainingTime(player);
            return formatCustomTime(timeLeftInSeconds, pattern);
        }

        return null;
    }

    // NUEVO Método auxiliar para dar formato automáticamente en años, meses, días, horas, minutos, segundos
    private String formatTimeAutomatically(int totalSeconds) {
        int years = totalSeconds / 31536000;
        int months = (totalSeconds % 31536000) / 2592000;
        int days = (totalSeconds % 2592000) / 86400;
        int hours = (totalSeconds % 86400) / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder timeString = new StringBuilder();
        if (years > 0) timeString.append(years).append(" years ");
        if (months > 0) timeString.append(months).append(" months ");
        if (days > 0) timeString.append(days).append(" days ");
        if (hours > 0) timeString.append(hours).append(" hours ");
        if (minutes > 0) timeString.append(minutes).append(" minutes ");
        if (seconds > 0) timeString.append(seconds).append(" seconds");

        return timeString.toString().trim();
    }

    // NUEVO Método auxiliar para aplicar un formato de tiempo personalizado
    private String formatCustomTime(int totalSeconds, String pattern) {
        int days = (int) TimeUnit.SECONDS.toDays(totalSeconds);
        long hours = TimeUnit.SECONDS.toHours(totalSeconds) - (days * 24);
        long minutes = TimeUnit.SECONDS.toMinutes(totalSeconds) - (TimeUnit.SECONDS.toHours(totalSeconds) * 60);
        long seconds = totalSeconds - (int) TimeUnit.SECONDS.toMinutes(totalSeconds) * 60;

        String formattedTime = pattern
                .replace("yyyy", String.valueOf(days / 365))
                .replace("MM", String.valueOf((days % 365) / 30))
                .replace("dd", String.valueOf(days % 30))
                .replace("hh", String.format("%02d", hours))
                .replace("mm", String.format("%02d", minutes))
                .replace("ss", String.format("%02d", seconds));

        return formattedTime;
    }
}
