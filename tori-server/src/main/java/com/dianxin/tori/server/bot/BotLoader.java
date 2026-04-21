package com.dianxin.tori.server.bot;

import com.dianxin.tori.api.bot.IBotLoader;
import com.dianxin.tori.api.bot.IBotMeta;
import com.dianxin.tori.api.bot.JavaDiscordBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.NoSuchFileException;
import java.util.*;

public class BotLoader implements IBotLoader {
    private final Logger logger = LoggerFactory.getLogger(BotLoader.class);
    private final List<JavaDiscordBot> activeBots = new ArrayList<>();

    public void loadBots() throws Exception {
        File botsFolder = new File("bots");
        if (!botsFolder.exists()) botsFolder.mkdirs();

        File[] jarFiles = botsFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            System.out.println("⚠️ This server has no file bot in folder 'bots/'.");
            return;
        }

        for (File jar : jarFiles) {
            logger.info("⏳ Enabling bot file {}...", jar.getName());

            // init ClassLoader for specific bot and turning on
            URL jarUrl = jar.toURI().toURL();
            URLClassLoader botClassLoader = new URLClassLoader(
                    new URL[]{jarUrl},
                    this.getClass().getClassLoader()
            );

            try {
                // get meta
                IBotMeta meta = getBotMetaFromJarFile(botClassLoader, jar);
                logger.info("🚀 Preparing for load bot: {} (v{}) by {}", meta.botName(), meta.botVersion(), meta.botAuthor());

                // use Reflection to find Class and initialize Object
                Class<?> mainClass = Class.forName(meta.mainClassPath(), true, botClassLoader);

                if (!JavaDiscordBot.class.isAssignableFrom(mainClass)) {
                    logger.error("❌ Class '{}' is not inherit JavaDiscordBot!", meta.mainClassPath());
                    continue;
                }

                // Gọi constructor
                JavaDiscordBot botInstance = (JavaDiscordBot) mainClass.getDeclaredConstructor().newInstance();
                botInstance.internalSetMeta(meta); // cannot access package-private

                // 4. Chạy Bot trên Thread riêng biệt
                new Thread(() -> {
                    try {
                        botInstance.start();
                        activeBots.add(botInstance);
                    } catch (Exception e) {
                        logger.error("❌ Lỗi khi khởi động bot {}", meta.botName(), e);
                    }
                }, "BotThread-" + meta.botName()).start();

            } catch (NoSuchFileException e) {
                logger.error("⚠️ Bỏ qua file {} vì không tìm thấy bot.yml", jar.getName());
            } catch (Exception e) {
                logger.error("❌ Lỗi khi nạp bot từ file {}", jar.getName(), e);
            }
        }
    }

    /**
     * Hàm đọc bot.yml được truyền sẵn URLClassLoader vào để tái sử dụng
     */
    private IBotMeta getBotMetaFromJarFile(URLClassLoader childLoader, File jarFile) throws IOException {
        // find file bot.yml in file jar
        URL yamlUrl = childLoader.findResource("bot.yml");
        if (yamlUrl == null) {
            throw new NoSuchFileException("bot.yml not found on file: " + jarFile.getName());
        }

        // read file bot.yml
        try (InputStream is = yamlUrl.openStream()) {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(is);

            String botName = String.valueOf(config.get("name"));
            String desc = config.get("description") != null ? String.valueOf(config.get("description")) : "";
            String version = config.get("version") != null ? String.valueOf(config.get("version")) : "unknown-version";
            String author = String.valueOf(config.get("author"));

            List<String> contributors = new ArrayList<>();
            Object rawContribs = config.get("contributors");
            if (rawContribs instanceof List) {
                // if dev write like list yaml
                for (Object o : (List<?>) rawContribs) contributors.add(String.valueOf(o));
            } else if (rawContribs instanceof String str) {
                // if dev write like string split by comma (,)
                contributors.addAll(Arrays.asList(str.split(",\\s*")));
            }

            String mainClassPath = String.valueOf(config.get("main"));

            if(botName == null) {
                throw new IllegalStateException("Name field ('name') is not specified");
            }

            if(author == null) {
                throw new IllegalStateException("Author field ('author') is not specified");
            }

            if(mainClassPath == null) {
                throw new IllegalStateException("Main class path field ('main') is not specified");
            }

            return new BotMeta(
                    botName,
                    desc == null ? "" : desc,
                    version == null ? "unknown-version" : version,
                    author,
                    contributors.isEmpty() ? List.of() : contributors,
                    mainClassPath
            );
        }
    }

    /**
     * Lấy danh sách các bot đang hoạt động trên Server.
     * Sử dụng unmodifiableList để ngăn các class bên ngoài sửa đổi mảng này.
     */
    public List<JavaDiscordBot> getActiveBots() {
        return Collections.unmodifiableList(activeBots);
    }

    public void shutdownAll() {
        System.out.println("🛑 Disabling all bots...");
        for (JavaDiscordBot bot : activeBots) {
            bot.onShutdown();
        }
        System.out.println("✅ All bots are shutdown successfully!");
    }
}
