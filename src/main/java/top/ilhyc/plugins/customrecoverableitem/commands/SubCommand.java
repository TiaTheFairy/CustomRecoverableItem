package top.ilhyc.plugins.customrecoverableitem.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommand {
    String commandName = "";
    int paramsLength = 0;

    String commandName();

    int paramsLength();

    String[] permissions() default {};

    boolean opped() default false;

    boolean isPlayer() default false;

    String targeted() default "";
}
