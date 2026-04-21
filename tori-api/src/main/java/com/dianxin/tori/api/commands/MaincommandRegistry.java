package com.dianxin.tori.api.commands;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.ApiStatus;

@Deprecated(forRemoval = true)
@ApiStatus.ScheduledForRemoval(inVersion = "2.2.5")
@SuppressWarnings("unused")
public interface MaincommandRegistry {
    CommandData getCommand();
}

