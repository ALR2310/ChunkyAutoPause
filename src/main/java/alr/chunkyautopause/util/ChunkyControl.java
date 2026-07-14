package alr.chunkyautopause.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

public final class ChunkyControl {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ChunkyControl.class);

    private ChunkyControl() {
    }

    public static void pause(MinecraftServer server) {
        LOGGER.info("Chunky Auto Pause: player joined, pausing chunky");
        runCommand(server, "chunky pause");
    }

    public static void resume(MinecraftServer server) {
        LOGGER.info("Chunky Auto Pause: last player left, resuming chunky");
        runCommand(server, "chunky continue");
    }

    private static void runCommand(MinecraftServer server, String command) {
        CommandSourceStack source = server.createCommandSourceStack();
        try {
            server.getCommands().getDispatcher().execute(command, source);
        } catch (CommandSyntaxException e) {
            LOGGER.warn("Chunky Auto Pause: failed to run '{}' - is Chunky installed?", command, e);
        }
    }
}
