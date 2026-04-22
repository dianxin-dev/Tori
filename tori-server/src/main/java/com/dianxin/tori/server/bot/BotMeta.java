package com.dianxin.tori.server.bot;

import com.dianxin.tori.api.bot.IBotMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class BotMeta implements IBotMeta {
    private final String botName;
    private final String botDescription;
    private final String botVersion;
    private final String botAuthor;
    private final List<String> botContributors;
    private final String mainClassPath;
    private final String botWebsite;
    private final String botOwnerId;

    public BotMeta(String botName, String botDescription, String botVersion, String botAuthor,
                   List<String> botContributors, String mainClassPath, String botWebsite, String botOwnerId) {
        this.botName = botName;
        this.botDescription = botDescription;
        this.botVersion = botVersion;
        this.botAuthor = botAuthor;
        this.botContributors = botContributors;
        this.mainClassPath = mainClassPath;
        this.botWebsite = botWebsite;
        this.botOwnerId = botOwnerId;
    }

    @Override
    public @NotNull String botName() {
        return botName;
    }

    @Override
    public @UnknownNullability String botDescription() {
        return botDescription;
    }

    @Override
    public @NotNull String botVersion() {
        return botVersion;
    }

    @Override
    public @NotNull String botAuthor() {
        return botAuthor;
    }

    @Override
    public @NotNull List<String> botContributors() {
        return botContributors;
    }

    @Override
    public @NotNull String mainClassPath() {
        return mainClassPath;
    }

    @Override
    public @Nullable String botWebsite() {
        return botWebsite;
    }

    @Override
    public @NotNull String botOwnerId() {
        return botOwnerId;
    }
}
