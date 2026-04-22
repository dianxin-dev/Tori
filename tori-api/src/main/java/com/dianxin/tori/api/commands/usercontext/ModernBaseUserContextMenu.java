package com.dianxin.tori.api.commands.usercontext;

import com.dianxin.tori.api.annotations.commands.*;
import com.dianxin.tori.api.bot.IBotMeta;
import com.dianxin.tori.api.commands.CommandReplyConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract base class for modern User Context Menus.
 * <p>
 * Subclasses extending this class must be annotated with {@link com.dianxin.tori.api.annotations.contextmenu.ContextMenu}
 * to provide the necessary metadata (like the interaction name) to the {@link com.dianxin.tori.api.commands.CommandRegistrar}.
 */
@SuppressWarnings("unused")
public abstract class ModernBaseUserContextMenu implements IUserContextMenu {
    private final Logger logger;
    private final JDA jda;
    private final IBotMeta botMeta;

    /**
     * Constructs a new ModernBaseUserContextMenu.
     *
     * @param jda  The {@link JDA} instance running this command.
     * @param meta The {@link IBotMeta} containing the bot's metadata.
     */
    public ModernBaseUserContextMenu(JDA jda, IBotMeta meta) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.jda = jda;
        this.botMeta = meta;
    }
    /**
     * Retrieves the logger instance for the current command.
     * * @return The {@link Logger} instance.
     */
    protected Logger getLogger() {
        return logger;
    }

    /**
     * Retrieves the JDA instance associated with this command.
     * * @return The {@link JDA} instance.
     */
    protected JDA getJda() {
        return jda;
    }

    /**
     * Handles the lifecycle and validation of the user context menu execution.
     * Parses the command's class annotations to enforce restrictions before
     * delegating to {@link #execute(UserContextInteractionEvent)}.
     *
     * @param event       The {@link UserContextInteractionEvent} triggered by Discord.
     * @param replyConfig The configuration used for custom error or rejection messages.
     */
    @Override
    public final void handle(UserContextInteractionEvent event, CommandReplyConfig replyConfig) {
        if (!checkOwnerOnly(event, replyConfig)) return;
        if (!checkDMOnly(event, replyConfig)) return;
        if (!checkPrivateChannelOnly(event, replyConfig)) return;
        if (!checkGuildOnly(event, replyConfig)) return;
        if (!checkUserPermissions(event, replyConfig)) return;
        if (!checkBotPermissions(event, replyConfig)) return;

        applyDeferIfNeeded(event);

        try {
            execute(event);
        } catch (Exception e) {
            logger.error("❌ An error occured when trying to handle command `{}`!", event.getName(), e);
        }

        logDebug(event);
    }

    // =========================================
    // begin of checker

    private boolean checkOwnerOnly(UserContextInteractionEvent event, CommandReplyConfig replyConfig) {
        if (!getClass().isAnnotationPresent(OwnerOnly.class)) return true;

        if(!event.getUser().getId().equals(botMeta.botOwnerId())) {
            event.reply(replyConfig.getOwnerOnlyMessage()).setEphemeral(true).queue();
            return false;
        }

        return true;
    }

    private boolean checkDMOnly(UserContextInteractionEvent event, CommandReplyConfig replyConfig) {
        if(!getClass().isAnnotationPresent(DirectMessageOnly.class)) return true;

        if(event.getGuild() != null) {
            event.reply(replyConfig.getDmOnlyMessage()).setEphemeral(true).queue();
            return false;
        }

        return true;
    }

    private boolean checkPrivateChannelOnly(UserContextInteractionEvent event, CommandReplyConfig replyConfig) {
        if(!getClass().isAnnotationPresent(PrivateChannelOnly.class)) return true;
        if (event.getChannelType() == ChannelType.PRIVATE) return true;
        event.reply(replyConfig.getPrivateChannelOnlyMessage()).setEphemeral(true).queue();
        return false;
    }

    private boolean checkGuildOnly(UserContextInteractionEvent event, CommandReplyConfig replyConfig) {
        if (!getClass().isAnnotationPresent(GuildOnly.class)) return true;

        if (event.getGuild() == null) {
            event.reply(replyConfig.getGuildOnlyMessage()).setEphemeral(true).queue();
            return false;
        }
        return true;
    }

    private boolean checkUserPermissions(UserContextInteractionEvent event, CommandReplyConfig replyConfig) {
        RequirePermissions ann = getClass().getAnnotation(RequirePermissions.class);
        if (ann == null) return true;

        Member member = event.getMember();
        if (member == null) {
            event.reply("⚠️ Cannot handle command when member not found.").setEphemeral(true).queue();
            return false;
        }

        for (Permission p : ann.value()) {
            if (!member.hasPermission(p)) {
                event.reply(replyConfig.getMissingUserPermissionMessage(p)).setEphemeral(true).queue();
                return false;
            }
        }
        return true;
    }

    private boolean checkBotPermissions(UserContextInteractionEvent event, CommandReplyConfig replyConfig) {
        RequireSelfPermissions ann = getClass().getAnnotation(RequireSelfPermissions.class);
        if (ann == null) return true;

        Guild guild = event.getGuild();
        if (guild == null) return false;

        Member self = guild.getSelfMember();

        for (Permission p : ann.value()) {
            if (!self.hasPermission(p)) {
                event.reply(replyConfig.getMissingBotPermissionMessage(p)).setEphemeral(true).queue();
                return false;
            }
        }
        return true;
    }

    private void applyDeferIfNeeded(UserContextInteractionEvent event) {
        if (getClass().isAnnotationPresent(DeferReply.class)) {
            event.deferReply().queue();
        }
    }

    private void logDebug(UserContextInteractionEvent event) {
        if (!getClass().isAnnotationPresent(DebugCommand.class)) return;

        logger.debug("[Command] {} by {} | {}",
                event.getName(),
                event.getUser().getAsTag(),
                event.getCommandString()
        );
    }

    /**
     * The core execution logic of the user context menu command.
     * Developers must implement this method to define what the command actually does after passing all checks.
     *
     * @param event The valid {@link UserContextInteractionEvent} passed through all pre-execution checks.
     */
    protected abstract void execute(UserContextInteractionEvent event);
}