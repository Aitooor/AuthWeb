package online.nasgar.authweb.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import online.nasgar.authweb.Main;
import online.nasgar.authweb.utils.Utils;
import online.nasgar.authweb.utils.ValidationResult;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CommandAlias("web")
public class WebCommands extends BaseCommand {

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
                    //TODO: ver porque no funciona en spigot 1.8.8 pero si en el resto
                    HttpClient httpclient = HttpClients.createDefault();
                    HttpPost httppost = new HttpPost("https://62bb8a51eff39ad5ee10f8a7.mockapi.io/Store/users");

                    List<NameValuePair> params = new ArrayList<>(3);
                    params.add(new BasicNameValuePair("uuid", sender.getUniqueId() + ""));
                    params.add(new BasicNameValuePair("username", sender.getName()));
                    params.add(new BasicNameValuePair("password", password));

                    httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                    httpclient.execute(httppost);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskAsynchronously(Main.getInstance());

        Main.getPlayerData().addPlayer(sender.getUniqueId());
        Main.getMessageHandler().send(sender, "registration.success");
    }

    //TODO: Sacar este comando para producción
    @Subcommand("test")
    @CommandPermission("authweb.command.test")
    public void test(Player sender) {
        Main.getPlayerData().getRegisteredPlayers().remove(sender.getUniqueId());
    }
}
