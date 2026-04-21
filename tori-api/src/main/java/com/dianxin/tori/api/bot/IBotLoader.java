package com.dianxin.tori.api.bot;

import java.util.List;

public interface IBotLoader {
    void loadBots() throws Exception;
    List<JavaDiscordBot> getActiveBots();
    void shutdownAll();
}
