package online.nasgar.authweb.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Syntax;
import com.fasterxml.jackson.databind.ObjectMapper;
import online.nasgar.authweb.Main;
import online.nasgar.authweb.utils.Utils;
import online.nasgar.authweb.utils.ValidationResult;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@CommandAlias("web")
public class WebCommands extends BaseCommand {

    //TODO: Poner el uso en los dos idiomas
    @Default
    public void web(Player sender, String password, @Name("confirmPassword") String confirmation) {
        if (Main.getPlayerData().containsPlayer(sender.getUniqueId())) {
            Main.getMessageHandler().send(sender, "registration.name_taken");
            return;
        }

        if (!password.matches(confirmation)) {
            Main.getMessageHandler().send(sender, "password.match_error");
            return;
        }

        ValidationResult result = Utils.isValidPassword(password, sender.getName());
        if (result.hasError()) {
            Main.getMessageHandler().send(sender, result.getMessagePath());
            return;
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    var values = new HashMap<String, String>() {{
                        put("uuid", sender.getUniqueId() + "");
                        put("username", sender.getName());
                        put("password", password);
                    }};

                    byte[] out = new ObjectMapper().writeValueAsString(values).getBytes(StandardCharsets.UTF_8);

                    URL url = new URL(Main.getConfiguration().getUrl());
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    http.setRequestMethod("POST");
                    http.setDoOutput(true);
                    http.setRequestProperty("Content-Type", "application/json");

                    OutputStream stream = http.getOutputStream();
                    stream.write(out);
                    http.getResponseCode();
                    http.disconnect();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskAsynchronously(Main.getInstance());

        Main.getPlayerData().addPlayer(sender.getUniqueId());
        Main.getMessageHandler().send(sender, "registration.success");
    }
}
