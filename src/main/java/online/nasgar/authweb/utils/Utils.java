package online.nasgar.authweb.utils;

import online.nasgar.authweb.Main;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Utils {

    private static Pattern passwordRegex;
    private static List<String> unsafePasswords;

    public static void init() {
        passwordRegex = safePatternCompile(Main.getConfiguration().getPasswordRegex());
        unsafePasswords = Main.getConfiguration().getUnsafePasswords();
    }

    public static ValidationResult isValidPassword(String password, String username) {
        String passLow = password.toLowerCase();
        if (!passwordRegex.matcher(passLow).matches()) {
            return new ValidationResult("password.forbidden_characters");
        } else if (passLow.equalsIgnoreCase(username)) {
            return new ValidationResult("password.name_in_password");
        } else if (password.length() < Main.getConfiguration().getMinLength() ||
                password.length() > Main.getConfiguration().getMaxLength()) {
            return new ValidationResult("password.wrong_length");
        } else if (unsafePasswords.contains(passLow)) {
            return new ValidationResult("password.unsafe_password");
        }
        return new ValidationResult();
    }

    /**
     * Compile Pattern sneaky without throwing Exception.
     *
     * @param pattern pattern string to compile
     *
     * @return the given regex compiled into Pattern object.
     */
    public static Pattern safePatternCompile(String pattern) {
        try {
            return Pattern.compile(pattern);
        } catch (Exception e) {
            Main.getInstance().getLogger().warning("Failed to compile pattern '" + pattern + "' - defaulting to allowing everything");
            return Pattern.compile(".*?");
        }
    }
}
