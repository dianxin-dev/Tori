package com.dianxin.tori.api.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audio.AudioModuleConfig;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.EnumSet;

@SuppressWarnings({"unused", "EmptyMethod"})
public abstract class JavaDiscordBot {
    private JDA jda;
    private IBotMeta meta;
    private final Logger logger;
    private volatile boolean started = false;

    private File dataFolder;

    public JavaDiscordBot() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    final void internalSetMeta(IBotMeta meta) {
        this.meta = meta;

        // Khởi tạo và tạo thư mục NGAY LẬP TỨC khi bot được nạp meta
        // Thư mục sẽ có dạng: plugins/bots/BotName
        this.dataFolder = new File("bots", meta.botName());
        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs();
        }
    }

    protected abstract String getBotToken();

    public synchronized void start() throws InterruptedException {
        if (started) {
            throw new IllegalStateException("Bot đã được start rồi!");
        }

        started = true;

        JDABuilder jdaBuilder;
        EnumSet<GatewayIntent> intents = getIntents();
        Activity activity = getActivity();
        AudioModuleConfig audioModuleConfig = getAudioModuleConfig();

        String botToken = getBotToken();

        if(intents == null) {
            jdaBuilder = JDABuilder.createDefault(botToken);
        } else {
            jdaBuilder = JDABuilder.createDefault(botToken, intents);
        }

        if(activity != null) {
            jdaBuilder.setActivity(activity);
        }

        if(audioModuleConfig != null) {
            jdaBuilder.setAudioModuleConfig(audioModuleConfig);
        }

        this.jda = jdaBuilder
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build()
                .awaitReady();

        logger.info("✅ Bot {} đã khởi động thành công.", meta.botName());
        logger.info("Link mời bot: {}", jda.getInviteUrl());

        // Đăng ký lệnh console custom
        registerConsoleCommands();

        onEnable();
    }

    public void onEnable() { }

    public void onDisable() { }

    public void onShutdown() {
        onDisable();
        logger.info("⏹ Đang tắt bot {}...", meta.botName());
        jda.getPresence().setStatus(OnlineStatus.OFFLINE);
        jda.shutdown();
    }

    public File getDataFolder() {
        if (this.dataFolder == null) {
            // Chặn đứng trường hợp Dev gọi hàm này trong constructor của Bot
            // (khi mà core chưa kịp nạp BotMeta vào)
            throw new IllegalStateException("Không thể gọi getDataFolder() khi BotMeta chưa được khởi tạo! Hãy gọi nó trong onEnable().");
        }
        return this.dataFolder;
    }

    protected EnumSet<GatewayIntent> getIntents() {
        return null;
    }

    protected Activity getActivity() {
        return null;
    }

    protected AudioModuleConfig getAudioModuleConfig() { return null; }

    protected void registerConsoleCommands() {
        // Bot subclasses override
    }

    public JDA getJda() {
        return jda;
    }

    public User getSelf() {
        return jda.getSelfUser();
    }

    public String getBotInviteLink() {
        return jda.getInviteUrl();
    }

    public String getBotInviteLink(Permission... permissions) {
        return jda.getInviteUrl(permissions);
    }

    public @NotNull IBotMeta getMeta() {
        return meta;
    }

    public Logger getLogger() {
        return logger;
    }
}
