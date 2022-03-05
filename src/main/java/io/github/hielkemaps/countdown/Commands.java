package io.github.hielkemaps.countdown;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.LiteralArgument;
import org.bukkit.Bukkit;

public class Commands {

    public Commands(){
        new CommandAPICommand("countdowns").withPermission(CommandPermission.OP).withArguments(new LiteralArgument("refresh")).executesPlayer( (p, args) -> {

            Main.findCountdowns();

            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                Main.updateCountdowns();
                p.sendMessage(Main.Prefix +  "Refreshed countdowns!");
            });
        }).register();
    }
}
