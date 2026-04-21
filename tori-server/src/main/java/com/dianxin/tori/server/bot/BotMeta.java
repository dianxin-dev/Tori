package com.dianxin.tori.server.bot;

import com.dianxin.tori.api.bot.IBotMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public record BotMeta(String botName, String botDescription, String botVersion, String botAuthor,
                      List<String> botContributors, String mainClassPath) implements IBotMeta {
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
}
