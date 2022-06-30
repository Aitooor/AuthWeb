package online.nasgar.authweb.data;

import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;
import de.exlll.configlib.format.FieldNameFormatters;
import lombok.Getter;
import online.nasgar.authweb.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Configuration extends BukkitYamlConfiguration {

    @Comment({"", "Regex for invaild characters in the password"})
    private String passwordRegex = "[!-~]*";
    @Comment({"", "Passwords that could not be used due to their insecurity problems"})
    private List<String> unsafePasswords = new ArrayList<>() {{
        add("123456");
        add("password");
        add("qwerty");
        add("12345");
        add("54321");
        add("123456789");
        add("help");
    }};
    @Comment({"", "Minimum allowed password length"})
    private int minLength = 5;
    @Comment({"", "Maximum allowd password length"})
    private int maxLength = 30;
    @Comment({"", "URL where the Post Request has to be send"})
    private String url = "http://127.0.0.1/";

    public Configuration() {
        super(
                new File(Main.getInstance().getDataFolder(), "config.yml").toPath(),
                BukkitYamlProperties.builder().setFormatter(FieldNameFormatters.LOWER_UNDERSCORE).build()
        );
    }
}
