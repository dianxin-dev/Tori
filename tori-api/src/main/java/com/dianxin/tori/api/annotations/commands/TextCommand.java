package com.dianxin.tori.api.annotations.commands;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as an annotation-driven legacy text command.
 * <p>
 * Classes annotated with {@code @TextCommand} should extend
 * {@link com.dianxin.tori.api.commands.text.ModernBaseTextCommand} to be properly
 * registered and routed by the {@link com.dianxin.tori.api.commands.TextCommandRegistrar}.
 * <p>
 * <b>Note:</b> Due to Discord's strict restrictions on the Message Content Intent,
 * the use of legacy text commands is generally discouraged in favor of Slash Commands.
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TextCommand {

    /**
     * The primary name used to trigger this command.
     * <p>
     * This string should <b>not</b> include the command prefix.
     * For example, if your bot prefix is "!" and you want the command to be "!ping",
     * you should set this value to simply {@code "ping"}.
     *
     * @return The main command name.
     */
    @NotNull String commandName();

    /**
     * An optional array of alternative names that can also be used to trigger this command.
     * <p>
     * For example, if the primary command name is {@code "serverinfo"},
     * you might provide aliases like {@code {"si", "info"}}.
     *
     * @return An array of command aliases, or an empty array if no aliases are defined.
     */
    @NotNull String[] aliases() default {};
}