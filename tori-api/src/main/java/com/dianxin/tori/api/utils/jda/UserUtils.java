package com.dianxin.tori.api.utils.jda;

import com.dianxin.tori.api.bot.JavaDiscordBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;

/**
 * Utility class cho {@link User}.
 * <p>
 * Class này tập trung vào các thao tác đọc thông tin, format dữ liệu
 * và cung cấp các giá trị an toàn để hiển thị.
 */
@Deprecated(forRemoval = true)
@ApiStatus.ScheduledForRemoval(inVersion = "2.2.5")
@SuppressWarnings("unused")
public final class UserUtils {
    @NotNull private static final JDA jda = ToriServices.getJda();

    private UserUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Mention user (ex: {@code <@123456789>}).
     */
    @NotNull
    public static String mention(@NotNull User user) {
        return user.getAsMention();
    }

    /**
     * get ID of user.
     */
    @NotNull
    public static String id(@NotNull User user) {
        return user.getId();
    }

    /**
     * get username (get rootname, not discriminator).
     */
    @NotNull
    public static String username(@NotNull User user) {
        return user.getName();
    }

    /**
     * get global name of user (nullable).
     *
     * @return global name or {@code null} if that user is not set
     */
    @Nullable
    public static String globalName(@NotNull User user) {
        return user.getGlobalName();
    }

    /**
     * Get safe nickname of user.
     * <p>
     * Priority:
     * <ol>
     *   <li>Global name</li>
     *   <li>Username</li>
     * </ol>
     *
     * @return main nickname of user
     */
    @NotNull
    public static String displayName(@NotNull User user) { // relative User#getEffectiveName
        String global = user.getGlobalName();
        return global != null ? global : user.getName();
    }

    /**
     * Kiểm tra người dùng có global name hay không.
     */
    public static boolean hasGlobalName(@NotNull User user) {
        return user.getGlobalName() != null;
    }

    /**
     * @deprecated Discord đã bỏ discriminator. Dùng {@link #username(User)}.
     */
    @NotNull
    @Deprecated
    public static String tag(@NotNull User user) {
        return user.getAsTag();
    }

    /**
     * Lấy tag đầy đủ của người dùng (vd: username hoặc global name + ID).
     * <p>
     * Hữu ích cho log/debug.
     */
    @NotNull
    public static String debugTag(@NotNull User user) {
        return displayName(user) + " (" + user.getId() + ")";
    }

    @Nullable
    public static String avatarLink(@NotNull User user) {
        return user.getAvatarUrl();
    }

    @Nullable
    public static String avatarId(@NotNull User user) {
        return user.getAvatarId();
    }

    @NotNull
    public static String effectiveAvatarLink(@NotNull User user) {
        return user.getEffectiveAvatarUrl();
    }

    @NotNull
    public static String defaultAvatarLink(@NotNull User user) {
        return user.getDefaultAvatarUrl();
    }

    @NotNull
    public static String defaultAvatarId(@NotNull User user) {
        return user.getDefaultAvatarId();
    }

    public static boolean hasPrivateChannel(@NotNull User user) {
        return user.hasPrivateChannel();
    }

    @NotNull
    public static RestAction<@Nullable String> bannerLink(@NotNull User user) {
        return user.retrieveProfile().map(User.Profile::getBannerUrl);
    }

    @NotNull
    public static RestAction<@Nullable String> bannerId(@NotNull User user) {
        return user.retrieveProfile().map(User.Profile::getBannerId);
    }

    /**
     * Retrieve {@link User} thông qua Discord REST API.
     *
     * @param id user id
     * @return {@link RestAction} thành công với {@link User} (non-null),
     *         hoặc thất bại nếu user không tồn tại / không truy cập được
     */
    @NotNull
    public static RestAction<User> retrieveUser(@NotNull String id) {
        return jda.retrieveUserById(id);
    }

    /**
     * Lấy {@link User} từ cache.
     *
     * @param id user id
     * @return {@link User} nếu có trong cache, ngược lại null
     */
    @Nullable
    public static User getCachedUser(@NotNull String id) {
        return jda.getUserById(id);
    }

    /**
     * Thời điểm tài khoản Discord được tạo.
     *
     * <p>Dựa trên Snowflake ID, không cần REST call.</p>
     */
    @NotNull
    public static OffsetDateTime createdTime(@NotNull User user) {
        return user.getTimeCreated();
    }
}
