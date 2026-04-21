package com.dianxin.tori.api.config;

import com.dianxin.core.api.config.yaml.FileConfiguration;

@SuppressWarnings("unused")
public interface ServerConfiguration {
    FileConfiguration getConfig();

    boolean isIgnoreErrorsOnRestAction();
}