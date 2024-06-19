package top.ilhyc.plugins.customrecoverableitem;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RecoverItem {
    private ItemStack primitive;
    protected long startTime;

    protected RecoverItem(ItemStack is){
        this.primitive = is.clone();
        startTime = System.currentTimeMillis();
    }

    protected RecoverItem(ItemStack is,long time){
        this.primitive = is.clone();
        startTime = time;
    }

    public long getStartTime() {
        return startTime;
    }

    public ItemStack getPrimitive() {
        return primitive;
    }

    public ItemStack toItemStack(){
        ItemStack iss = primitive.clone();
        NBTItem nbtItem = new NBTItem(iss);
        NBTCompound nbtCompound = nbtItem.getOrCreateCompound("RecoverableItem");
        nbtCompound.setItemStack("primitive",primitive);
        nbtCompound.setLong("startTime",startTime);
        nbtItem.applyNBT(iss);
        ItemMeta im = iss.getItemMeta();
        List<String> lore = im.getLore();
        if(lore==null){
            lore = new ArrayList<>();
        }
        lore.addAll(convertLore(CustomRecoverableItem.recoveryRule));
        im.setLore(lore);
        iss.setItemMeta(im);
        return iss;
    }

    protected List<String> convertLore(RecoveryRule rule){
        List<String> list = new ArrayList<>();
        for(String s:rule.lore){
            list.add(s.replace("%price%",rule.price(this)+""));
        }
        return list;
    }

    public boolean recover(Player player,RecoveryRule rule){
        return rule.cost(player,this);
    }

    public boolean outOfTime(RecoveryRule rule){
        return rule.outOfTime(this);
    }

    public static RecoverItem getRecoverItem(ItemStack is){
        if(is==null||is.getType()== Material.AIR||is.getItemMeta()==null){
            return null;
        }
        NBTItem nbtItem = new NBTItem(is);
        NBTCompound nbtCompound = nbtItem.getCompound("RecoverableItem");
        if(nbtCompound==null){
            return null;
        }
        ItemStack recovereon = nbtCompound.getItemStack("primitive");
        long time = nbtCompound.getLong("startTime");
        return new RecoverItem(recovereon,time);
    }

    public static RecoverItem constructRecoverItem(ItemStack is,long startTime){
        if(is==null||is.getType()== Material.AIR||is.getItemMeta()==null){
            return null;
        }
        return new RecoverItem(is,startTime);
    }
}
