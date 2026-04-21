package com.dianxin.tori.server;

import com.dianxin.tori.api.config.ServerConfiguration;
import com.dianxin.tori.server.config.MainServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ToriBootstrap {
    private static final Logger log = LoggerFactory.getLogger(ToriBootstrap.class);

    public static ServerConfiguration init() {
        log.info("Loading configuration...");

        File configFile = new File("config.yml");
        if (!configFile.exists()) {
            try (InputStream in = ToriBootstrap.class.getClassLoader().getResourceAsStream("config.yml")) {
                if (in == null) {
                    log.error("❌ Cannot find default config.yml in resources folder!");
                } else {
                    Files.copy(in, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    log.info("Save default config successful.");
                }
            } catch (IOException e) {
                log.error("Cannot copy default config.yml into root path!", e);
            }
        }

        try {
            ServerConfiguration config = new MainServerConfiguration(configFile);
            log.info("Read config file successful!");
            return config;
        } catch (IOException e) {
            log.error("An error occured when trying to load file config!", e);
            System.exit(1); // stop now
            return null;
        }
    }
}
