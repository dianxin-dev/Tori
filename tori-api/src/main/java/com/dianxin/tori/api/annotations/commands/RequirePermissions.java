package com.dianxin.tori.api.annotations.commands;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the Discord {@link Permission}s that the executing user must possess
 * in the current channel or guild to invoke the annotated command.
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequirePermissions {

    /**
     * An array of {@link Permission}s required by the user.
     *
     * @return The required permissions.
     */
    Permission[] value();
}