package uni.projects.remarketbackend.utils.dataVerification;

import uni.projects.remarketbackend.models.account.Account;

import java.util.regex.Pattern;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */
public class NormalDataVerification implements DataVerification {

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 32;

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 32;

    @Override
    public boolean verifyUser(Account account) {
        if (!verifyEmail(account.getEmail()))
            return false;
        if (!verifyUsername(account.getUsername()))
            return false;
        if (!verifyPassword(account.getPassword()))
            return false;

        return true;
    }

    @Override
    public boolean verifyUsername(String username) { //TODO include some regex
        return username.length() >= MIN_USERNAME_LENGTH && username.length() <= MAX_USERNAME_LENGTH;
    }

    @Override
    public int passwordStrength(String password) { //TODO check for repeating characters, words, etc
        int strength = 0;
        if (password.length() >= MIN_PASSWORD_LENGTH) strength += 2;
        if (password.matches(".*[a-z].*")) strength += 2;
        if (password.matches(".*[A-Z].*")) strength += 2;
        if (password.matches(".*[0-9].*")) strength += 2;
        if (password.matches(".*[!@#$%^&*()].*")) strength += 2;
        return Math.min(strength, 10);
    }

    @Override
    public boolean verifyEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    @Override
    public boolean verifyPassword(String password) { //TODO include regex to check for example for spaces
        return password.length() >= MIN_PASSWORD_LENGTH && password.length() <= MAX_PASSWORD_LENGTH;
    }

}
