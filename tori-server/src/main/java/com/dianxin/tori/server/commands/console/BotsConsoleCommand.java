package com.dianxin.tori.server.commands.console;

import com.dianxin.core.api.console.commands.AbstractConsoleCommand;
import com.dianxin.tori.api.bot.IBotMeta;
import com.dianxin.tori.api.bot.JavaDiscordBot;
import com.dianxin.tori.server.Main;

import java.util.List;

public class BotsConsoleCommand extends AbstractConsoleCommand {
    public BotsConsoleCommand() {
        super("bots"); // nhập chữ "bots" trên console để hiện list bot đang có
    }

    @Override
    public void execute(String[] args) {
        // Lấy danh sách bot từ BotLoader thông qua MultiBotServer
        List<JavaDiscordBot> bots = Main.getServer().getBotLoader().getActiveBots();
        if (bots.isEmpty()) {
            getLogger().info("📦 Hiện tại không có bot nào đang hoạt động trên hệ thống.");
            return;
        }

        // Dùng StringBuilder để format danh sách cho đẹp
        StringBuilder sb = new StringBuilder();
        sb.append("📦 Các Bot đang hoạt động (").append(bots.size()).append("):\n");

        for (JavaDiscordBot bot : bots) {
            IBotMeta meta = bot.getMeta();
            sb.append("  ✅ ").append(meta.botName())
                    .append(" v").append(meta.botVersion())
                    .append(" | Tác giả: ").append(meta.botAuthor());

            // Nếu có mô tả thì in thêm
            if (meta.botDescription() != null && !meta.botDescription().isBlank()) {
                sb.append(" - ").append(meta.botDescription());
            }
            sb.append("\n");
        }

        // In toàn bộ kết quả ra màn hình Console
        getLogger().info(sb.toString());
    }
}
