package com.dianxin.tori.api;

import com.dianxin.core.api.console.commands.ConsoleCommandManager;
import com.dianxin.core.api.v2.scheduler.Scheduler;
import com.dianxin.tori.api.bot.IBotLoader;
import com.dianxin.tori.api.config.ServerConfiguration;

public interface ToriServer {
    ServerConfiguration getConfig();
    ConsoleCommandManager getConsoleCommandManager();
    Scheduler getScheduler();
    IBotLoader getBotLoader();
}
