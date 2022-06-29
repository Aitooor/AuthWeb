package online.nasgar.authweb.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import online.nasgar.authweb.Main;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {
    @Getter
    private Set<UUID> registeredPlayers;

    public void addPlayer(UUID player) {
        registeredPlayers.add(player);
    }

    public boolean containsPlayer(UUID player) {
        return registeredPlayers.contains(player);
    }

    public void load() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File file = new File(Main.getInstance().getDataFolder(), "players.json");
        if (!file.exists()) {
            registeredPlayers = new HashSet<>();
            return;
        }

        try (Reader reader = new FileReader(file)) {
            registeredPlayers = gson.fromJson(reader, new TypeToken<Set<UUID>>(){}.getType());
            if (registeredPlayers == null)
                registeredPlayers = new HashSet<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Writer writer = new FileWriter(new File(Main.getInstance().getDataFolder(), "players.json"))) {
            gson.toJson(registeredPlayers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
