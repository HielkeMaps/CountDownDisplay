package io.github.hielkemaps.countdown;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class Countdown {

    public final long key;
    public final LocalDateTime timestamp;
    private final Entity entity;

    public Countdown(long time, Entity entity){
        key = time;
        timestamp = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC);
        this.entity = entity;
    }

    public void update() {
        long seconds = java.time.LocalDateTime.now().until(timestamp, ChronoUnit.SECONDS);
        if(seconds <= 0){
            Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), this::remove,0);
            return;
        }
        String name = String.format("%02d:%02d:%02d", seconds / 3600, (seconds / 60) % 60, seconds % 60);
        entity.setCustomName(name);

    }

    private void remove(){
        Firework fw = (Firework) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().trail(true).withFade(Color.RED).withColor(Color.ORANGE).with(FireworkEffect.Type.BURST).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();
        entity.remove();
        Main.countdowns.remove(key);
    }
}
