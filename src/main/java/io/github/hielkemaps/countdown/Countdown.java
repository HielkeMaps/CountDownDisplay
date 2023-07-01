package io.github.hielkemaps.countdown;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.meta.FireworkMeta;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class Countdown {

    public final String name;
    public final LocalDateTime timestamp;
    private final TextDisplay entity;
    public final String endCommand;

    public Countdown(String name, long time, TextDisplay entity, String command) {
        this.name = name;
        timestamp = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC);
        this.entity = entity;
        this.endCommand = command;
    }

    public void update() {
        long seconds = java.time.LocalDateTime.now().until(timestamp, ChronoUnit.SECONDS);
        if (seconds <= 0) {
            Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), this::remove, 0);
            return;
        }
        String text = String.format("%02d:%02d:%02d", seconds / 3600, (seconds / 60) % 60, seconds % 60);
        entity.setText(text);
    }

    private void remove() {
        if (!endCommand.equals("")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), endCommand);
        }

        Firework fw = (Firework) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().trail(true).withFade(Color.RED).withColor(Color.ORANGE).with(FireworkEffect.Type.BURST).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();
        entity.remove();
        Main.countdowns.remove(name);
    }
}
