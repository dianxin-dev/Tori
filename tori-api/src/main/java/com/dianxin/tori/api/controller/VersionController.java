package com.dianxin.tori.api.controller;

import com.dianxin.core.api.utils.VersionManager;
import com.dianxin.tori.api.base.Constants;

public final class VersionController {
    private VersionController() { }

    public static void checkCompatibilityOrThrow() {
        int javaVersion = VersionManager.getJavaVersionRunning();

        if (javaVersion < Constants.JAVA_REQUIRED_VERSION) {
            throw new UnsupportedOperationException(
                    "Java version is incompatible, must use Java " + Constants.JAVA_REQUIRED_VERSION +
                            "or higher instead " + javaVersion + "!"
            );
        }
    }
}
