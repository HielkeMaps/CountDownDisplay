package io.github.hielkemaps.countdown;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.LongArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;

import java.util.Set;

public class Commands {

    public Commands() {
        new CommandAPICommand("countdown")
                .withPermission(CommandPermission.OP)

                .withSubcommand(new CommandAPICommand("test")
                        .withArguments(new StringArgument("timer"))
                        .executesPlayer((p, args) -> {
                            String timer = (String) args.get("timer");

                            boolean containsKey = Main.countdowns.containsKey(timer);
                            if (containsKey) {
                                p.sendMessage(Component.text("Running command for timer " + timer));
                                Main.countdowns.get(timer).runCommand();
                            } else {
                                throw CommandAPI.failWithString("Timer not found");
                            }
                        }))
                .withSubcommand(new CommandAPICommand("new")
                        .withArguments(new StringArgument("name"))
                        .withArguments(new LongArgument("timestamp"))
                        .withArguments(new GreedyStringArgument("command"))
                        .executesPlayer((p, args) -> {

                            String name = (String) args.get("name");
                            long timestamp = (long) args.get("timestamp");
                            String command = (String) args.get("command");

                            TextDisplay display = (TextDisplay) p.getWorld().spawnEntity(p.getLocation(), EntityType.TEXT_DISPLAY);
                            display.addScoreboardTag("time_" + timestamp);
                            display.addScoreboardTag("name_" + name);
                            display.addScoreboardTag("countdown");
                            display.customName(Component.text(command));

                            Countdown c = new Countdown(name, timestamp, display, command);

                            if (Main.countdowns.containsKey(name)) {
                                for (TextDisplay e : p.getWorld().getEntitiesByClass(TextDisplay.class)) {
                                    Set<String> tags = e.getScoreboardTags();
                                    if (tags.contains("countdown") && tags.contains("name_" + name)) {
                                        e.remove(); //remove old entity
                                        break;
                                    }
                                }
                            }

                            Main.countdowns.put(name, c);

                            Main.countdowns.clear();
                            Main.findCountdowns();
                            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                                Main.updateCountdowns();
                                p.sendMessage(Main.Prefix + "Added new countdown");
                            });
                        }))

                .withSubcommand(new CommandAPICommand("refresh")
                        .executesPlayer((p, args) -> {

                            Main.countdowns.clear();
                            Main.findCountdowns();
                            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                                Main.updateCountdowns();
                                p.sendMessage(Main.Prefix + "Refreshed countdowns!");
                            });
                        }))

                .withSubcommand(new CommandAPICommand("list")
                        .executesPlayer((p, args) -> {

                            p.sendMessage(Main.Prefix + "Countdowns:");
                            Main.countdowns.forEach((s, countdown) -> p.sendMessage("- " + s + " [" + countdown.endCommand + "]"));
                        }))

                .register();
    }
}
