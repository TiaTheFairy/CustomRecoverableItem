package top.ilhyc.plugins.customrecoverableitem.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import top.ilhyc.plugins.customrecoverableitem.CustomRecoverableItem;
import top.ilhyc.plugins.customrecoverableitem.PluginInitial;
import top.ilhyc.plugins.customrecoverableitem.data.PlayerData;
import top.ilhyc.plugins.customrecoverableitem.holders.RecoverHolder;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MainCommand implements CommandExecutor, TabCompleter {
    public static Map<UUID,Status> statusMap = new HashMap<>();

    private final CustomRecoverableItem plugin;

    public MainCommand(CustomRecoverableItem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        SubCommandParser subCommandParser = new SubCommandParser(getClass(),this,sender,command,label,args);
        try {
            subCommandParser.parse();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @SubCommand(paramsLength = 0, commandName = "open",permissions = {})
    public void start(CommandSender sender,Command command,String label,String[] args){
        Player player = null;
        if(sender instanceof Player){
            player = (Player) sender;
        }
        if(args.length>1&&sender.isOp()){
            player = Bukkit.getPlayer(args[1]);
        }
        if(player==null){
            sender.sendMessage(CustomRecoverableItem.Auto("&a其玩家不在线罢"));
            return;
        }
        new RecoverHolder(player).open();
    }

    @SubCommand(paramsLength = 0, commandName = "speculate",permissions = {},opped = true)
    public void speculate(CommandSender sender,Command command,String label,String[] args){
        Player player = null;
        Player target = null;
        if(sender instanceof Player){
            player = (Player) sender;
        }
        if(args.length>1&&sender.isOp()){
            target= Bukkit.getPlayer(args[1]);
        }
        if(args.length>2&&sender.isOp()){
            player = Bukkit.getPlayer(args[2]);
        }
        if(target==null){
            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            onlinePlayers.removeIf(player1 -> player1.isOp());
            target = onlinePlayers.get(ThreadLocalRandom.current().nextInt(onlinePlayers.size()));
        }
        if(player==null){
            sender.sendMessage(CustomRecoverableItem.Auto("&a其玩家不在线罢"));
            player.sendTitle("目标玩家不存在或不在线", target.getDisplayName(),5,100,20);
            return;
        }
        player.setSpectatorTarget(null);
        player.teleport(target, PlayerTeleportEvent.TeleportCause.PLUGIN);
        Player finalPlayer = player;
        Player finalTarget = target;
        Bukkit.getScheduler().scheduleSyncDelayedTask(PluginInitial.plugin,()->{
            finalPlayer.setSpectatorTarget(finalTarget);
            finalPlayer.sendTitle("正在观察玩家", finalTarget.getDisplayName(),5,100,20);
        },8);
    }

    @SubCommand(paramsLength = 0, commandName = "save",permissions = {},opped = true)
    public void save(CommandSender sender,Command command,String label,String[] args){
        PlayerData.saveAll();
        sender.sendMessage(CustomRecoverableItem.Auto("&a其插件已保存罢!"));
    }

    @SubCommand(paramsLength = 0, commandName = "reload",permissions = {},opped = true)
    public void reloads(CommandSender sender,Command command,String label,String[] args){
        PluginInitial.plugin.reloadConfig();
        CustomRecoverableItem.loadData();
        sender.sendMessage(CustomRecoverableItem.Auto("&a其插件已重载!"));
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings==null||strings.length<=1){
            return Arrays.stream(getClass().getMethods()).filter(a->a.isAnnotationPresent(SubCommand.class)).filter(a->commandSender.isOp()||!a.getAnnotation(SubCommand.class).opped()).map(a->a.getAnnotation(SubCommand.class).commandName()).filter(a->strings==null||(strings.length==1&&a.startsWith(strings[0]))).collect(Collectors.toList());
        }
        return null;
    }

    public enum StatusType{
        TAKING,
        REMOVING;

        public void commit(Player player){
            Status status = statusMap.getOrDefault(player.getUniqueId(),new Status());
            status.getStatus().add(this);
            statusMap.put(player.getUniqueId(),status);
        }

        public void remove(Player player){
            Status status = statusMap.getOrDefault(player.getUniqueId(),new Status());
            status.getStatus().remove(this);
            statusMap.put(player.getUniqueId(),status);
        }

        public boolean equip(Player player){
            Status status = statusMap.get(player.getUniqueId());
            if(status==null){
                return false;
            }
            return status.getStatus().contains(this);
        }
    }
}

