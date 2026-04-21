package com.dianxin.tori.api.commands.usercontext;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

public interface IUserContextMenu {
    void execute(UserContextInteractionEvent event);
}
