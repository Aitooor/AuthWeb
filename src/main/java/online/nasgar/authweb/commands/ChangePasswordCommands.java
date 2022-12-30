package online.nasgar.authweb.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
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

@CommandAlias("webchange|webcambiar|webchangepassword|webcambiarcontraseña")
public class ChangePasswordCommands extends BaseCommand {

    @Default
    @Syntax("{@@changepassword.usage}")
    public void web(Player sender, String oldPassword, String newPassword, String confirmNewPassword) {
        if (!Main.getPlayerData().containsPlayer(sender.getUniqueId())) {
            Main.getMessageHandler().send(sender, "changepassword.no_registered");
            return;
        }

        if (oldPassword.matches(newPassword)) {
            Main.getMessageHandler().send(sender, "password.match_error");
            return;
        }

        if (!newPassword.matches(confirmNewPassword)) {
            Main.getMessageHandler().send(sender, "password.no_match_error");
            return;
        }

        ValidationResult result = Utils.isValidPassword(newPassword, sender.getName());
        if (result.hasError()) {
            Main.getMessageHandler().send(sender, result.getMessagePath());
            return;
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                try {
                    HashMap<String, String> values = new HashMap<String, String>() {{
                        put("uuid", sender.getUniqueId() + "");
                        put("username", sender.getName());
                        put("password", newPassword);
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
        Main.getMessageHandler().send(sender, "changepassword.success");
    }
}
