package top.ilhyc.plugins.customrecoverableitem;

import com.google.gson.Gson;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class APIHooker {
    public static Economy economy;
    public static Gson gson;

    public static Economy setupEconomy() {//经济api 配置
        if (PluginInitial.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return null;
        } else {
            RegisteredServiceProvider<Economy> rsp = PluginInitial.plugin.getServer().getServicesManager().getRegistration(Economy.class);
            return economy = rsp.getProvider();
        }
    }

    public static Gson setupGson(){
        return gson=new Gson();
    }
}
