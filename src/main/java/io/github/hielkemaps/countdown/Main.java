package io.github.hielkemaps.countdown;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Set;

public class Main extends JavaPlugin {

    private static Plugin instance;
    public static HashMap<String, Countdown> countdowns = new HashMap<>();
    public static HashMap<String, HypeCount> hypeCounts = new HashMap<>();
    public static String Prefix = "[Countdowns] ";

    public Main() {
        instance = this;
    }

    public static Plugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        new Commands();

        //Find countdowns in world after 5 seconds
        this.getServer().getScheduler().runTaskLater(instance, Main::findCountdowns, 300);
        this.getServer().getScheduler().runTaskLater(instance, Main::findHypeCounts, 300);

        //Update countdowns every second
        this.getServer().getScheduler().runTaskTimerAsynchronously(instance, Main::updateCountdowns, 0, 20);

        //Update hype count every second
        this.getServer().getScheduler().runTaskTimerAsynchronously(instance, Main::updateHypeCounts, 0, 20);
    }

    public static void findHypeCounts() {
        Bukkit.getLogger().info(Prefix + "Searching for hype counts...");
        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(entity -> {
            if (entity.getType() == EntityType.TEXT_DISPLAY) {
                Set<String> tags = entity.getScoreboardTags();
                if (tags.contains("hypeCount")) {
                    String name = null;
                    String id= null;
                    for (String tag : tags) {
                        if (tag.startsWith("name_")) {
                            name = tag.substring(5);
                        }
                    }
                    if (name != null) {
                        if (!Main.hypeCounts.containsKey(name)) {
                            String command = entity.getCustomName();
                            Main.hypeCounts.put(name, new HypeCount(name, command, (TextDisplay) entity));
                            Bukkit.getLogger().info(Prefix + "Adding new hype count " + name);
                        }
                    }
                }
            }
        }));
    }

    public static void findCountdowns() {

        Bukkit.getLogger().info(Prefix + "Searching for countdowns...");

        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(entity -> {
            if (entity.getType() == EntityType.TEXT_DISPLAY) {
                Set<String> tags = entity.getScoreboardTags();
                if (tags.contains("countdown")) {
                    Long timestamp = null;
                    String name = null;

                    for (String tag : tags) {
                        if (tag.startsWith("time_")) {
                            try {
                                timestamp = Long.valueOf(tag.substring(5));
                            } catch (NumberFormatException e) {
                                Bukkit.getLogger().warning(Prefix + "Can't add countdown: " + tag.substring(5) + " is not a long!");
                            }
                        }

                        if (tag.startsWith("name_")) {
                            name = tag.substring(5);
                        }
                    }

                    if (timestamp != null && name != null) {

                        if (!Main.countdowns.containsKey(name)) {
                            String command = entity.getCustomName();
                            Main.countdowns.put(name, new Countdown(name, timestamp, (TextDisplay) entity, command));
                            Bukkit.getLogger().info(Prefix + "Adding new countdown " + name);
                        }
                    }
                }
            }
        }));
    }


    public static void updateCountdowns() {
        for (Countdown c : countdowns.values()) {
            c.update();
        }
    }

    public static void updateHypeCounts() {
        for (HypeCount count : hypeCounts.values()) {
            count.update();
        }
    }
}