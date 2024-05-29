package lf.jaime.files;

import lf.jaime.LightFly;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final CustomConfig configFile;
    private LightFly plugin;
    private boolean preventInGamemode;
    private String lang;


    public ConfigManager(LightFly plugin){
        configFile = new CustomConfig("config.yml", null, plugin);
        LoadConfig();
    }

    public void LoadConfig(){
        configFile.registerConfig();
        FileConfiguration fileConfiguration = configFile.getConfig();
        lang = fileConfiguration.getString("lang");
        preventInGamemode = fileConfiguration.getBoolean("prevent_fly_in_creative");
    }

    public boolean isPreventInGamemode() {
        return preventInGamemode;
    }

    public String getLang() {
        return lang;
    }

}
