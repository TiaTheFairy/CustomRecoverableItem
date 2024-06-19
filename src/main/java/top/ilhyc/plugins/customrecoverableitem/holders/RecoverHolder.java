package top.ilhyc.plugins.customrecoverableitem.holders;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import top.ilhyc.plugins.customrecoverableitem.CustomRecoverableItem;
import top.ilhyc.plugins.customrecoverableitem.PluginInitial;
import top.ilhyc.plugins.customrecoverableitem.RecoverItem;
import top.ilhyc.plugins.customrecoverableitem.RecoveryRule;
import top.ilhyc.plugins.customrecoverableitem.data.PlayerData;
import top.ilhyc.plugins.customrecoverableitem.permissions.PermissionManager;

import java.util.*;

public class RecoverHolder implements InventoryHolder,Unclickable{
    private Player player;
    protected List<RecoverItem> queues;

    public RecoverHolder(Player player){
        this.player = player;
        this.queues = PlayerData.getPluginData(player).getRecoverItems();
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this,54, CustomRecoverableItem.Auto(PluginInitial.plugin.getConfig().getString("default.gui.title")));
        Integer amount = PermissionManager.getPermissionObject("recoverable.size",getPlayer(), a->{
            try {
                return Integer.parseInt(a);
            }catch (NumberFormatException e){
                return -1;
            }
        });
//        queues.stream().forEach(a->{
//            if(a.outOfTime(CustomRecoverableItem.recoveryRule)){
//                queues.remove(a);
//                return;
//            }
//            inventory.addItem(a.toItemStack());
//        });

        for(RecoverItem recoveryItem:queues){
            if(recoveryItem.outOfTime(CustomRecoverableItem.recoveryRule)){
                queues.remove(recoveryItem);
                continue;
            }
            inventory.addItem(recoveryItem.toItemStack());
        }
        if(amount!=null) {
            for (int var1 = amount; var1 < 54; var1++) {
                ItemStack holder = new ItemStack(Material.valueOf(PluginInitial.plugin.getConfig().getString("default.gui.holder.type")));
                ItemMeta meta = holder.getItemMeta();
                meta.setDisplayName(CustomRecoverableItem.Auto(PluginInitial.plugin.getConfig().getString("default.gui.holder.name")));
                List<String> lores = PluginInitial.plugin.getConfig().getStringList("default.gui.holder.lore");
                if (!lores.isEmpty()) {
                    List<String> coloredLore = new ArrayList<>();
                    for (String line : lores) {
                        coloredLore.add(CustomRecoverableItem.Auto(line));
                    }
                    meta.setLore(coloredLore);
                }

                holder.setItemMeta(meta);
                inventory.setItem(var1, holder);
            }
        }
        return inventory;
    }

    public Player getPlayer() {
        return player;
    }

    public List<RecoverItem> getQueues() {
        return queues;
    }

    public void open(){
        getPlayer().openInventory(getInventory());
    }
}
