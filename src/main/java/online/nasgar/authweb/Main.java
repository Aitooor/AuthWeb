package online.nasgar.authweb;

import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import me.yushust.message.MessageHandler;
import me.yushust.message.MessageProvider;
import me.yushust.message.bukkit.BukkitMessageAdapt;
import me.yushust.message.source.MessageSourceDecorator;
import online.nasgar.authweb.commands.WebCommands;
import online.nasgar.authweb.data.Configuration;
import online.nasgar.authweb.data.PlayerData;
import online.nasgar.authweb.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;
    @Getter
    private static PaperCommandManager cmdManager;
    @Getter
    private static MessageHandler messageHandler;
    @Getter
    private static PlayerData playerData;
    @Getter
    private static Configuration configuration;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        playerData = new PlayerData();
        configuration = new Configuration();
        Utils.init();
        messages();
        commands();
        playerData.load();
    }

    @Override
    public void onDisable() {
        playerData.save();
    }

    private void commands() {
        cmdManager = new PaperCommandManager(getInstance());
        cmdManager.enableUnstableAPI("help");

        cmdManager.setFormat(MessageType.HELP, ChatColor.DARK_AQUA, ChatColor.AQUA, ChatColor.GRAY, ChatColor.DARK_GRAY);
        cmdManager.setFormat(MessageType.SYNTAX, ChatColor.DARK_AQUA, ChatColor.AQUA, ChatColor.GRAY, ChatColor.DARK_GRAY);
        cmdManager.setFormat(MessageType.INFO, ChatColor.DARK_AQUA, ChatColor.AQUA, ChatColor.GRAY, ChatColor.DARK_GRAY);
        cmdManager.registerCommand(new WebCommands());
    }

    private void messages() {
        MessageProvider messageProvider = MessageProvider
                .create(
                        MessageSourceDecorator
                                .decorate(BukkitMessageAdapt.newYamlSource(
                                        this, "lang_%lang%.yml"))
                                .addFallbackLanguage("en")
                                .addFallbackLanguage("es")
                                .get(),
                        config -> {
                            config.specify(Player.class)
                                    .setLinguist(player -> player.spigot().getLocale().split("_")[0])
                                    .setMessageSender((sender, mode, message) -> sender.sendMessage(message));
                            config.specify(CommandSender.class)
                                    .setLinguist(commandSender -> "en")
                                    .setMessageSender((sender, mode, message) -> sender.sendMessage(message));
                            config.addInterceptor(s -> ChatColor.translateAlternateColorCodes('&', s));
                        }
                );

        messageHandler = MessageHandler.of(messageProvider);
    }
}
