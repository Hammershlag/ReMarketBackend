package uni.projects.remarketbackend.models;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 25.04.2025
 */
public enum Roles {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    STUFF("ROLE_STUFF"),
    SELLER("ROLE_SELLER");

    private final String role;

    Roles(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
