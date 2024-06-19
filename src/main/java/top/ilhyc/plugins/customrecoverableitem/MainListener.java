package top.ilhyc.plugins.customrecoverableitem;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import top.ilhyc.plugins.customrecoverableitem.data.PlayerData;
import top.ilhyc.plugins.customrecoverableitem.holders.RecoverHolder;
import top.ilhyc.plugins.customrecoverableitem.holders.Unclickable;

import java.util.List;
import java.util.Stack;

public class MainListener implements Listener {
    protected CustomRecoverableItem plugin;

    public MainListener(CustomRecoverableItem plugin){
        this.plugin = plugin;
    }
    @EventHandler
    public void onClick(InventoryClickEvent e){
        Inventory inventory = e.getView().getTopInventory();
        if(inventory==null){
            return;
        }
        InventoryHolder inventoryHolder = inventory.getHolder();
        if(inventoryHolder==null){
            return;
        }
        if(inventoryHolder instanceof Unclickable){
            e.setCancelled(true);
        }
        int slot = e.getRawSlot();
        if(inventoryHolder instanceof RecoverHolder){
            RecoverHolder recoverHolder = (RecoverHolder) inventoryHolder;
            RecoverItem recoverItem = RecoverItem.getRecoverItem(e.getCurrentItem());
            if(recoverItem==null){
                return;
            }
            ItemStack is = recoverItem.getPrimitive();
            if(!recoverItem.recover(recoverHolder.getPlayer(),CustomRecoverableItem.recoveryRule)){
                recoverHolder.getPlayer().sendMessage(CustomRecoverableItem.Auto(plugin.getConfig().getString("default.message.poor")));
                recoverHolder.getPlayer().closeInventory();
                return;
            }
            recoverHolder.getPlayer().getInventory().addItem(is);
            recoverHolder.getQueues().remove(slot);
            recoverHolder.getPlayer().closeInventory();
        }
    }

    @EventHandler
    public void onBroken(PlayerItemBreakEvent e){
        List<String> allowedTypes = plugin.getConfig().getStringList("default.items");
        ItemStack item = e.getBrokenItem();
        String itemType = item.getType().toString().toLowerCase();
        if(allowedTypes.stream().anyMatch(itemType::contains)){
            ItemStack is = e.getBrokenItem().clone();
            is.setDurability((short) 0);
            PlayerData.getPluginData(e.getPlayer()).addBrokenItem(is);
        }
    }
}
