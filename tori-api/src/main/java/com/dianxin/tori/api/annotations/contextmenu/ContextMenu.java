package com.dianxin.tori.api.annotations.contextmenu;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated class serves as a Context Menu command.
 * <p>
 * Context Menu commands are a special type of Discord command that can be invoked
 * directly on a user or message by right-clicking on them. These commands take no
 * arguments and are useful for providing a quick way to perform actions on a specific target.
 * <p>
 * Example Usage:
 * <pre>{@code
 * @ContextMenu(interactionName = "View Profile")
 * public class ViewProfileMenu extends ModernBaseUserContextMenu { ... }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ContextMenu {

    /**
     * The name of the interaction that will be displayed in Discord's right-click context menu.
     *
     * @return The interaction name.
     */
    String interactionName();
}