package com.dianxin.tori.api.annotations.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the framework should automatically defer the reply for the annotated command.
 * <p>
 * This will immediately trigger a "Bot is thinking..." state in Discord, granting the bot
 * more time (up to 15 minutes) to process complex logic before sending the final response.
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DeferReply {
}