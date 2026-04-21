package com.dianxin.tori.api.commands.usercontext;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseUserContextMenu implements IUserContextMenu {
    protected final Logger logger;

    private final String title;
    private final JDA jda;

    public BaseUserContextMenu(String title, Command.Type type, JDA jda) {
        this.title = title;
        this.jda = jda;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    protected JDA getJda() {
        return jda;
    }

    public String getTitle() {
        return title;
    }

    public void execute(UserContextInteractionEvent event) {

    }
}
