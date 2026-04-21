package com.dianxin.tori.api.bot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

/**
 * Represents the metadata of a Discord bot running on the Tori Server.
 * <p>
 * The data provided by this interface is typically parsed directly from
 * the bot's {@code bot.yml} configuration file located in its resources.
 */
@SuppressWarnings("unused")
public interface IBotMeta {

    /**
     * Gets the name of the bot.
     *
     * @return The bot's name.
     */
    @NotNull String botName();

    /**
     * Gets a brief description of what the bot does.
     *
     * @return The description of the bot, or null/empty if not specified.
     */
    @UnknownNullability String botDescription();

    /**
     * Gets the current version of the bot.
     *
     * @return The version string (e.g., "1.0.0").
     */
    @NotNull String botVersion();

    /**
     * Gets the name of the primary author or developer of the bot.
     *
     * @return The author's name.
     */
    @NotNull String botAuthor();

    /**
     * Gets a list of additional contributors who helped develop the bot.
     *
     * @return A list of contributor names, or an empty list if there are none.
     */
    @NotNull List<String> botContributors();

    /**
     * Gets the fully qualified class path to the bot's main class
     * (the class that extends {@link JavaDiscordBot}).
     *
     * @return The main class path (e.g., "com.example.bot.MainBotClass").
     */
    @NotNull String mainClassPath();

    /**
     * Gets the official website or promotional page for the bot.
     *
     * @return The URL of the bot's website, or {@code null} if not provided.
     */
    @Nullable String botWebsite();

    /**
     * Gets the Discord User ID of the bot's primary owner.
     * <p>
     * This is typically used by the framework to verify owner-exclusive permissions,
     * such as restricting access to commands annotated with {@code @OwnerOnly}.
     *
     * @return The snowflake ID of the bot owner.
     */
    @NotNull String botOwnerId();
}