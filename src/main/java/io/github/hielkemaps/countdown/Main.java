package io.github.hielkemaps.countdown;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Set;

public class Main extends JavaPlugin {

    private static Plugin instance;
    public static HashMap<Long, Countdown> countdowns = new HashMap<>();
    public static String Prefix = "[FloatingCountdowns] ";

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

        //Update countdowns every second
        this.getServer().getScheduler().runTaskTimerAsynchronously(instance, Main::updateCountdowns, 0, 20);
    }

    public static void findCountdowns() {

        Bukkit.getLogger().info(Prefix + "Searching for countdowns...");

        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(entity -> {
            if (entity.getType() == EntityType.AREA_EFFECT_CLOUD) {
                Set<String> tags = entity.getScoreboardTags();
                if (tags.contains("countdown")) {
                    Long timestamp = null;

                    for (String tag : tags) {
                        if (tag.startsWith("time_")) {
                            try {
                                timestamp = Long.valueOf(tag.substring(5));
                            }catch(NumberFormatException e){
                                Bukkit.getLogger().warning(Prefix + "Can't add countdown: " + tag.substring(5) + " is not a long!");
                            }
                        }
                    }

                    if (timestamp != null) {

                        if (!Main.countdowns.containsKey(timestamp)) {
                            Main.countdowns.put(timestamp, new Countdown(timestamp, entity));
                            Bukkit.getLogger().info(Prefix + "Adding new countdown with timestamp: " + timestamp);
                        }
                    }
                }
            }
        }));
    }


    public static void updateCountdowns() {
        for(Countdown c : countdowns.values()){
            c.update();
        }
    }
}