package top.ilhyc.plugins.customrecoverableitem;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecoveryRule {
    protected double base;
    protected double perEnchantment;
    protected Map<String,Double> overrides = new HashMap<>();
    protected long expired;
    protected List<String> lore = new ArrayList<>();
    protected CustomRecoverableItem plugin;

    public RecoveryRule(CustomRecoverableItem plugin){
        this.plugin = plugin;
    }

    public RecoveryRule loadFromConfig(){
        String path = "default.cost.";
        base = plugin.getConfig().getDouble(path+"base");
        perEnchantment = plugin.getConfig().getDouble(path+"per-enchantment-level");
        expired = plugin.getConfig().getLong("default.time");
        lore= plugin.getConfig().getStringList("default.lore").stream().map(CustomRecoverableItem::Auto).collect(Collectors.toList());
        overrides.clear();
        plugin.getConfig().getStringList(path+"override").stream().map(a->a.split(":")).filter(a->a.length>1).forEach(a->{overrides.put(a[0].toLowerCase(),Double.parseDouble(a[1]));});
        return this;
    }

    public double getBase() {
        return base;
    }

    public double getPerEnchantment() {
        return perEnchantment;
    }

    public JavaPlugin getSource() {
        return plugin;
    }

    public double price(RecoverItem item){
        ItemStack is = item.getPrimitive();
        if(is==null||is.getType()== Material.AIR||is.getItemMeta()==null){
            return 0;
        }
        double amount = base;
        amount = amount+(is.getItemMeta().hasEnchants()?countAmount(is.getEnchantments()):0);
        return amount;
    }

    public boolean cost(OfflinePlayer player, RecoverItem item){
        ItemStack is = item.getPrimitive();
        if(is==null||is.getType()== Material.AIR||is.getItemMeta()==null){
            return false;
        }
        double amount = base;
        amount = amount+(is.getItemMeta().hasEnchants()?countAmount(is.getEnchantments()):0);
        return APIHooker.economy.withdrawPlayer(player,amount).transactionSuccess();
    }

    public boolean outOfTime(RecoverItem item){
        long time = item.getStartTime();
        return time+expired*3600*1000L<System.currentTimeMillis();
    }

    protected double countAmount(Map<Enchantment,Integer> map){
        double amount = 0;
        for(Map.Entry<Enchantment,Integer> enchant:map.entrySet()){
            String name = enchant.getKey().getName().toLowerCase();
            Double amounts = overrides.get(name);
            amount = amount+(amounts==null?perEnchantment:amounts)*enchant.getValue();
        }
        return amount;
    }
}
