package com.dianxin.tori.api.annotations.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Restricts the execution of the annotated command exclusively to the bot's defined owner(s).
 * Unauthorized users attempting to invoke this command will be denied access.
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OwnerOnly {
}