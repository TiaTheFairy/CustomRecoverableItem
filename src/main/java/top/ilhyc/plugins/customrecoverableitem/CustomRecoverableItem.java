package top.ilhyc.plugins.customrecoverableitem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import top.ilhyc.plugins.customrecoverableitem.commands.MainCommand;
import top.ilhyc.plugins.customrecoverableitem.data.PlayerData;

public final class CustomRecoverableItem extends JavaPlugin {
    public static PluginInitial pi;
    public static RecoveryRule recoveryRule;
    @Override
    public void onEnable() {
        // Plugin startup logic
        pi = new PluginInitial(this);
        pi.onEnable();
        pi.loadConfig();
        pi.loadPlayerData();
        loadHookers();
        loadData();
        Bukkit.getPluginManager().registerEvents(new MainListener(this),this);
        getCommand("customrecoverableitem").setExecutor(new MainCommand(this));
    }

    public static void loadData(){
        recoveryRule = new RecoveryRule(PluginInitial.plugin).loadFromConfig();
    }

    public static void loadHookers(){
        APIHooker.setupEconomy();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        pi.onDisable();
        PlayerData.saveAll();
    }

    public static String Auto(String s){
        return ChatColor.translateAlternateColorCodes('&',s);
    }
}
