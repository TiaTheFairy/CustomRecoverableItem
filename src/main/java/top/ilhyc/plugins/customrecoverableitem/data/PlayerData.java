package top.ilhyc.plugins.customrecoverableitem.data;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import top.ilhyc.plugins.customrecoverableitem.CustomRecoverableItem;
import top.ilhyc.plugins.customrecoverableitem.PluginInitial;
import top.ilhyc.plugins.customrecoverableitem.RecoverItem;
import top.ilhyc.plugins.customrecoverableitem.permissions.PermissionManager;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerData{
    private OfflinePlayer player;
    private List<RecoverItem> recoverItems;
    private static Map<UUID,PlayerData> playerDatas = new HashMap<>();
    private static boolean isSql = false;
    private PlayerData(OfflinePlayer player){
        this.player = player;
        recoverItems = new ArrayList<>();
        if(!isSql) {
            PluginData pd = PluginData.getPlayerData(player.getName());
            String path = "default.";
            List<?> list = pd.getFc().getList(path + "recoverItems");
            if (list != null) {
                for(ItemStack is:((List<ItemStack>) list)){
                    RecoverItem recoverItem = RecoverItem.getRecoverItem(is);
                    recoverItems.add(recoverItem);
                }
            }
        }
        playerDatas.put(player.getUniqueId(), this);
    }

    public Player getPlayer() {
        return player.getPlayer();
    }

    public void save(){
        if(!isSql){
            String path = "default.";
            PluginData pd = PluginData.getPlayerData(player.getName());
            pd.set(path+"recoverItems",recoverItems.stream().map(RecoverItem::toItemStack).collect(Collectors.toList()));
            pd.save();
            PluginInitial.log("Player data saved for " + player.getName());
        }
    }

    public List<RecoverItem> getRecoverItems() {
        return recoverItems;
    }

    public boolean addBrokenItem(ItemStack is){
        Integer amount = PermissionManager.getPermissionObject("recoverable.size",getPlayer(),a->{
            try {
                return Integer.parseInt(a);
            }catch (NumberFormatException e){
                return -1;
            }
        });
        if(amount!=null&&amount<=recoverItems.size()){
            getPlayer().sendMessage(CustomRecoverableItem.Auto(PluginInitial.plugin.getConfig().getString("default.message.full")));
            recoverItems.remove(0);
        }
        recoverItems.add(RecoverItem.constructRecoverItem(is,System.currentTimeMillis()));
        getPlayer().sendMessage(CustomRecoverableItem.Auto(PluginInitial.plugin.getConfig().getString("default.message.success")));
        return true;
    }

    public static PlayerData getPluginData(OfflinePlayer player){ //wo yong de s
        PlayerData playerData = playerDatas.get(player.getUniqueId());
        if(playerData==null){
            return new PlayerData(player);
        }
        return playerData;
    }

    public static void saveAll(){
        playerDatas.values().forEach(PlayerData::save);
    }

    public static void setSqlEnabled(boolean enabled){
        isSql = enabled;
    }

    public static boolean isEnabled(){
        return isSql;
    }
}
