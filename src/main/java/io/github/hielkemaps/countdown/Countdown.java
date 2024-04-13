package io.github.hielkemaps.countdown;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.meta.FireworkMeta;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class Countdown {

    public final String name;
    public final Instant timestamp;
    private final TextDisplay entity;
    public final String endCommand;

    public Countdown(String name, long time, TextDisplay entity, String command) {
        this.name = name;
        timestamp = Instant.ofEpochSecond(time);

        this.entity = entity;
        this.endCommand = command;
    }

    public void update() {
        Instant now = Instant.now();
        long seconds = Duration.between(now, timestamp).getSeconds();

        if (seconds <= 0) {
            Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), this::remove, 0);
            return;
        }

        String text = "";

        long days = seconds / (3600 * 24);
        long hours = (seconds / 3600) % 24;
        long minutes = (seconds / 60) % 60;
        long remainingSeconds = seconds % 60;

        if (days > 0) {
            text += String.format("%dd ", days);
        }

        if (days > 0 || hours > 0) {
            text += String.format("%02dh ", hours);
        }

        if (days > 0 || hours > 0 || minutes > 0) {
            text += String.format("%02dm ", minutes);
        }

        text += String.format("%02ds", remainingSeconds);
        entity.text(Component.text(text));
    }

    public void runCommand() {
        if (!endCommand.equals("")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), endCommand);
        }
    }

    private void remove() {
        runCommand();

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
