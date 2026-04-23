package com.dianxin.tori.api.commands.text;

import com.dianxin.tori.api.commands.CommandReplyConfig;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.ApiStatus;

/**
 * The core interface for legacy text-based commands (e.g., "!ping").
 * Note: Discord heavily restricts the Message Content Intent. It is highly recommended
 * to use Slash Commands instead.
 */
@ApiStatus.Obsolete(since = "Discord Message Content Intent restrictions")
public interface ITextCommand {
    /**
     * Handles the incoming text command interaction.
     *
     * @param event       The {@link MessageReceivedEvent} triggered by Discord.
     * @param args        An array of arguments parsed from the message.
     * @param replyConfig The configuration used for custom error or rejection messages.
     */
    void handle(MessageReceivedEvent event, String[] args, CommandReplyConfig replyConfig);
}