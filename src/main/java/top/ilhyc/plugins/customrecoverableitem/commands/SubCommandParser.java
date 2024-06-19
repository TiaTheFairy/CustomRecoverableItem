package top.ilhyc.plugins.customrecoverableitem.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class SubCommandParser {
    private Class<?> classes;
    private CommandSender sender;
    private Command command;
    private String label;
    private String[] args;
    private CommandExecutor executor;

    public SubCommandParser(Class<?> classes,CommandExecutor executor,CommandSender sender, Command command, String label, String[] args){
        this.executor = executor;
        this.classes = classes;
        this.sender = sender;
        this.command = command;
        this.label = label;
        this.args = args;
    }

    public Method parse() throws InvocationTargetException, IllegalAccessException {
        for(Method method:classes.getMethods()){
            if(parseMethod(method)==ParseResult.SUCCESS){
                return method;
            }
        }
        return null;
    }

    public ParseResult parseMethod(Method method) throws InvocationTargetException, IllegalAccessException {
        if(!method.isAnnotationPresent(SubCommand.class)){
            return ParseResult.NOT_SUBCOMMAND;
        }
        String name = method.getAnnotation(SubCommand.class).commandName();
        int params = method.getAnnotation(SubCommand.class).paramsLength();
        String[] permissions = method.getAnnotation(SubCommand.class).permissions();
        boolean opped = method.getAnnotation(SubCommand.class).opped();
        if(opped&&!sender.isOp()){
            return ParseResult.OUT_OP;
        }
        if(args==null||args.length<1||args.length-1<params){
            return ParseResult.FEW_PARAMS;
        }
        if(!name.equalsIgnoreCase(args[0])){
            return ParseResult.WRONG_NAME;
        }
        if(Arrays.stream(permissions).anyMatch(a->!sender.hasPermission(a))){
            return ParseResult.NONE_PERMISSION;
        }
        method.invoke(executor,sender,command,label,args);
        return ParseResult.SUCCESS;
    }

    public enum ParseResult{
        NOT_SUBCOMMAND,
        FEW_PARAMS,
        WRONG_NAME,
        NONE_PERMISSION,
        OUT_OP,
        SUCCESS;
    }
}
