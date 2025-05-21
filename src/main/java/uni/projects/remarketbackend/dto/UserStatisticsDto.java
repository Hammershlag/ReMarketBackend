package uni.projects.remarketbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for user statistics")
public class UserStatisticsDto {

    @Schema(description = "Number of active users")
    private long activeUsers;

    @Schema(description = "Number of blocked users")
    private long blockedUsers;

    @Schema(description = "Number of deleted users")
    private long deletedUsers;

    @Schema(description = "Number of new users in the last 7 days")
    private long newUsers;

    @Schema(description = "Number of users who logged in during the last 24 hours")
    private long loggedInLast24h;

    public UserStatisticsDto(long activeUsers, long blockedUsers, long deletedUsers, long newUsers, long loggedInLast24h) {
        this.activeUsers = activeUsers;
        this.blockedUsers = blockedUsers;
        this.deletedUsers = deletedUsers;
        this.newUsers = newUsers;
        this.loggedInLast24h = loggedInLast24h;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public long getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(long blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    public long getDeletedUsers() {
        return deletedUsers;
    }

    public void setDeletedUsers(long deletedUsers) {
        this.deletedUsers = deletedUsers;
    }

    public long getNewUsers() {
        return newUsers;
    }

    public void setNewUsers(long newUsers) {
        this.newUsers = newUsers;
    }

    public long getLoggedInLast24h() {
        return loggedInLast24h;
    }

    public void setLoggedInLast24h(long loggedInLast24h) {
        this.loggedInLast24h = loggedInLast24h;
    }
}
