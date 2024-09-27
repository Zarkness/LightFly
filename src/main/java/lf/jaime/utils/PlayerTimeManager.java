package lf.jaime.utils;

import lf.jaime.LightFly;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerTimeManager {
    private final ConcurrentHashMap<Player, Integer> playerTimers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Player, Boolean> isPaused = new ConcurrentHashMap<>(); // Estado de pausa
    private final LightFly plugin;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public PlayerTimeManager(LightFly plugin) {
        this.plugin = plugin;
        startTimerTask();
    }

    private void startTimerTask() {
        scheduler.scheduleAtFixedRate(() -> {
            for (Player player : playerTimers.keySet()) {
                int timeLeft = playerTimers.get(player);
                
                if (!player.isFlying()) {
                    isPaused.put(player, true);  // Pausa el tiempo si el jugador no está volando
                    continue;
                } else if (isPaused.getOrDefault(player, false)) {
                    isPaused.put(player, false);  // Reanuda el tiempo cuando vuelve a volar
                }

                try {
                    if (timeLeft > 0) {
                        playerTimers.put(player, timeLeft - 1);
                    } else {
                        playerTimers.remove(player);
                        player.setAllowFlight(false);
                    }
                } catch (NullPointerException e) {
                    playerTimers.remove(player);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void addPlayer(Player player, int seconds) {
        playerTimers.put(player, seconds);
        isPaused.put(player, false);  // Inicializa el estado de pausa en falso
    }

    public void removePlayer(Player player) {
        playerTimers.remove(player);
        isPaused.remove(player);
    }

    public int getRemainingTime(Player player) {
        return playerTimers.getOrDefault(player, 0);
    }

    public String formatTime(int totalSeconds) {
        int years = totalSeconds / 31536000;
        int months = (totalSeconds % 31536000) / 2592000;
        int days = (totalSeconds % 2592000) / 86400;
        int hours = (totalSeconds % 86400) / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder formattedTime = new StringBuilder();

        if (years > 0) formattedTime.append(years).append("y ");
        if (months > 0) formattedTime.append(months).append("m ");
        if (days > 0) formattedTime.append(days).append("d ");
        if (hours > 0) formattedTime.append(hours).append("h ");
        if (minutes > 0) formattedTime.append(minutes).append("min ");
        if (seconds > 0 || formattedTime.length() == 0) formattedTime.append(seconds).append("s");

        return formattedTime.toString().trim();
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
