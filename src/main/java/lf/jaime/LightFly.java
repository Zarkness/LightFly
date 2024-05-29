package lf.jaime;

import lf.jaime.commands.Fly;
import lf.jaime.commands.MainCommand;
import lf.jaime.commands.TempFly;
import lf.jaime.files.ConfigManager;
import lf.jaime.files.MessagesManager;
import lf.jaime.utils.PlayerTimeManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class LightFly extends JavaPlugin {
    private MessagesManager messagesManager;
    private ConfigManager configManager;
    private PlayerTimeManager playerTimeManager;

    public void onEnable(){
        configManager = new ConfigManager(this);
        messagesManager = new MessagesManager(this, configManager.getLang() + ".yml");
        playerTimeManager = new PlayerTimeManager(this);
        registerCommands();
        Metrics metrics = new Metrics(this, 22065);

    }

    public void onDisable(){
        playerTimeManager.shutdown();
    }


    private void registerCommands(){
        Objects.requireNonNull(getCommand("fly")).setExecutor(new Fly(this));
        Objects.requireNonNull(getCommand("lightfly")).setExecutor(new MainCommand(this));
        Objects.requireNonNull(getCommand("tempfly")).setExecutor(new TempFly(this));
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }
    public ConfigManager getConfigManager() {
        return configManager;
    }
    public PlayerTimeManager getPlayerTimeManager() {
        return playerTimeManager;
    }
}
