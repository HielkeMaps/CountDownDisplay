package io.github.hielkemaps.countdown;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.TextDisplay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HypeCount {

    public final String name;
    public final String id;
    private final TextDisplay entity;
    public final String command;
    private int hypeCount = 0;

    public HypeCount(String name, String id, String command, TextDisplay entity) {
        this.name = name;
        this.id = id;
        this.command = command;

        this.entity = entity;
    }

    private int getRequestCount(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
        return jsonResponse.get("count").getAsInt();
    }

    public void update() {
        try {
            URL apiUrl = new URL("https://count.cab/get/" + id);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                int count = getRequestCount(connection);
                if (count > hypeCount) {
                    hypeCount = count;
                    entity.text(Component.text(hypeCount));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHype(){
        if (!command.equals("")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                URL apiUrl = new URL("https://count.cab/hit/" + id);
                HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    hypeCount = getRequestCount(connection);
                    entity.text(Component.text(hypeCount));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void remove() {
        entity.remove();
        Main.hypeCounts.remove(name);
    }
}
