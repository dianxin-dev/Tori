package com.dianxin.tori.api.commands.usercontext;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

/**
 * The core interface for User Context Menu commands.
 * Classes implementing this interface define the execution logic for commands
 * triggered when a user right-clicks on another user in Discord.
 */
public interface IUserContextMenu {

    /**
     * Executes the user context menu logic.
     *
     * @param event The {@link UserContextInteractionEvent} containing the interaction context,
     * including the user who invoked the command and the target user.
     */
    void execute(UserContextInteractionEvent event);
}