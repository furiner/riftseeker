package net.arcadiasedge.riftseeker.loaders;

import dev.jorel.commandapi.CommandAPI;
import net.arcadiasedge.exodus.loaders.ExodusLoader;
import net.arcadiasedge.riftseeker.commands.RiftseekerCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandLoader extends ExodusLoader<RiftseekerCommand> {
    public CommandLoader(JavaPlugin plugin) {
        super(RiftseekerCommand.class, plugin);

        this.basePackage = "net.arcadiasedge.riftseeker.commands";
    }

    @Override
    protected void load(Class<?> object) {
        if (!RiftseekerCommand.class.isAssignableFrom(object)) {
            return;
        }

        if (object.getSimpleName().equals("RiftseekerCommand")) {
            return;
        }

        CommandAPI.registerCommand(object);
    }

    @Override
    protected void unload(Class<? extends RiftseekerCommand> object) {
        // TODO: Implement this
        return;
    }
}
