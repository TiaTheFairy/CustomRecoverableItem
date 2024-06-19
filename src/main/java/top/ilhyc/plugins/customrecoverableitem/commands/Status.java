package top.ilhyc.plugins.customrecoverableitem.commands;

import java.util.HashSet;
import java.util.Set;

public class Status{
    private Set<MainCommand.StatusType> status = new HashSet<>();

    public boolean isTakenable(){
        return status.contains(MainCommand.StatusType.TAKING);
    }

    public Set<MainCommand.StatusType> getStatus() {
        return status;
    }
}