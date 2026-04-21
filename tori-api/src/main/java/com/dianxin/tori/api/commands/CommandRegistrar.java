package com.dianxin.tori.api.commands;

import com.dianxin.core.api.exceptions.InvalidRegistrationNameException;
import com.dianxin.core.api.exceptions.MissingAnnotationException;
import com.dianxin.tori.api.annotations.contextmenu.ContextMenu;
import com.dianxin.tori.api.bot.JavaDiscordBot;
import com.dianxin.tori.api.commands.messagecontext.BaseMessageContextMenu;
import com.dianxin.tori.api.commands.slash.LegacyBaseCommand;
import com.dianxin.tori.api.commands.usercontext.BaseUserContextMenu;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@SuppressWarnings({"removal", "unused"}) // todo
public class CommandRegistrar {
    private static final Logger logger = LoggerFactory.getLogger(CommandRegistrar.class);
    private final JDA jda;
    private final JavaDiscordBot bot;

    // Lưu trữ các lệnh đã đăng ký.
    // Key là tên lệnh (vd: "play", "ban"), Value là class thực thi lệnh đó.
    private final Map<String, LegacyBaseCommand> commands = new HashMap<>();

    private final AtomicBoolean commitedAll = new AtomicBoolean(false);

    public CommandRegistrar(JavaDiscordBot bot) {
        this.jda = bot.getJda();
        this.bot = bot;
    }

    /**
     * Đăng ký một hoặc nhiều lệnh vào bộ nhớ của bot.
     */
    public CommandRegistrar registerCommands(LegacyBaseCommand... cmdInstances) {
        if (commitedAll.get()) {
            throw new IllegalStateException("Không thể đăng ký thêm lệnh sau khi đã commit!");
        }

        for (LegacyBaseCommand cmd : cmdInstances) {
            if (cmd instanceof MaincommandRegistry registry) {
                String commandName = registry.getCommand().getName(); // Tự động lấy tên lệnh từ CommandData để làm Key
                commands.put(commandName, cmd);
            } else {
                throw new IllegalArgumentException("Lệnh " + cmd.getClass().getSimpleName() + " phải implements MaincommandRegistry!");
            }
        }
        return this; // Hỗ trợ chaining (nối chuỗi lệnh)
    }

    /**
     * Gửi toàn bộ danh sách lệnh lên máy chủ Discord.
     */
    public void commitAllCommands(@Nullable Guild guild) {
        if (commitedAll.getAndSet(true)) return; // Tránh gọi 2 lần

        CommandListUpdateAction updateAction = guild == null ? jda.updateCommands() : guild.updateCommands();
        List<CommandData> commandDataList = new ArrayList<>();

        for (LegacyBaseCommand cmd : commands.values()) {
            CommandData data = ((MaincommandRegistry) cmd).getCommand();
            commandDataList.add(data);
        }

        updateAction.addCommands(commandDataList).queue(
                commands -> System.out.println("✅ Đã cập nhật thành công " + commandDataList.size() + " lệnh lên Discord!"),
                error -> System.err.println("❌ Lỗi cập nhật lệnh: " + error.getMessage())
        );
    }

    /**
     * Hàm này sẽ được gọi từ một ListenerAdapter để xử lý khi có người dùng gõ lệnh.
     */
    public void onSlashCommandEvent(SlashCommandInteractionEvent event) {
        String commandName = event.getName();

        LegacyBaseCommand command = commands.get(commandName);
        if (command == null) {
            event.reply("❌ Lệnh này không tồn tại hoặc chưa được nạp vào hệ thống.").setEphemeral(true).queue();
            return;
        }

        // Thực thi lệnh (BaseCommand.handle sẽ tự động check quyền, defer reply các kiểu)
        command.handle(event);
    }

    private final Map<String, BaseUserContextMenu> userContextCmds = new HashMap<>();
    private final Map<String, BaseMessageContextMenu> messageContextCmds = new HashMap<>();

    /**
     * Đăng ký một User Context Menu
     *
     * @param contextMenu Instance của context menu
     *
     * @throws MissingAnnotationException
     * @throws InvalidRegistrationNameException
     */
    public void register(@NotNull BaseUserContextMenu contextMenu) {
        Class<?> tClass = contextMenu.getClass();
        if(!tClass.isAnnotationPresent(ContextMenu.class)) {
            throw new MissingAnnotationException(ContextMenu.class, tClass);
        }

        ContextMenu contextMenu1 = tClass.getAnnotation(ContextMenu.class);
        String interactionName = contextMenu1.interactionName();
        if(interactionName.isEmpty()) {
            throw new InvalidRegistrationNameException("Interaction Name trong " + tClass.getSimpleName() +
                    " không được để trống!");
        }

        jda.updateCommands().addCommands(Commands.context(Command.Type.USER, interactionName)).queue();

        commands.put(interactionName, contextMenu);
        logger.info("✅ Registered user context menu: **{}**", interactionName);
    }

    /**
     * ?
     * Cảnh báo: Khuyến khích nên đăng ký từng context menu thay vì một list để tránh trường hợp throw
     * dẫn đến các phần tử còn lại không được duyệt
     * @param contextMenus
     */
    @ApiStatus.Obsolete // obsolete là annotation, tương tự deprecated nhưng yếu hơn
    public void register(@NotNull BaseUserContextMenu... contextMenus) {
        Stream.of(contextMenus).forEach(this::register);
    }

    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        BaseUserContextMenu menu = commands.get(event.getName());
        if (menu == null) return;

        try {
            if (!menu.beforeExecute(event)) return;
            menu.execute(event);
            menu.afterExecute(event);
        } catch (Exception e) {
            logger.error("❌ Lỗi context menu {}", event.getName(), e);
            event.reply("❌ Có lỗi xảy ra.").setEphemeral(true).queue();
        }
    }

    public void onUserContextEvent(UserContextInteractionEvent event) {}

    public void onMessageContextEvent(MessageContextInteractionEvent event) {}
}
