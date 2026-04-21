package com.dianxin.tori.api;

import com.dianxin.core.api.console.commands.ConsoleCommandManager;
import com.dianxin.tori.api.config.ServerConfiguration;

public final class ToriProvider {
    private static ToriServer serverInstance;

    public static void setServer(ToriServer instance) {
        if (serverInstance != null) {
            throw new UnsupportedOperationException("ToriServer has already been set!");
        }
        serverInstance = instance;
    }

    private static ToriServer get() {
        if (serverInstance == null) {
            throw new IllegalStateException("ToriServer is not initialized!");
        }
        return serverInstance;
    }

    public static ServerConfiguration getConfig() {
        return get().getConfig();
    }

    public static ConsoleCommandManager getConsoleCommandManager() {
        return get().getConsoleCommandManager();
    }
}
