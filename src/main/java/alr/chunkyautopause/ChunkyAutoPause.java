package alr.chunkyautopause;

import alr.chunkyautopause.util.ChunkyControl;
import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

@Mod(ChunkyAutoPause.MODID)
public class ChunkyAutoPause {
    public static final String MODID = "chunkyautopause";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int TICKS_PER_SECOND = 20;

    // Ticks remaining before Chunky resumes; -1 means no resume is scheduled.
    private int resumeCountdownTicks = -1;

    public ChunkyAutoPause(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || !Config.enabled) return;

        if (server.getPlayerList().getPlayerCount() == 1) {
            resumeCountdownTicks = -1;
            ChunkyControl.pause(server);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || !Config.enabled) return;

        int playerCount = server.getPlayerList().getPlayerCount();
        LOGGER.info("Chunky Auto Pause: player left, playerCount={}", playerCount);
        // The leaving player may or may not have been removed from the list yet
        // by the time this event fires, so treat both 0 and 1 as "server about to be empty".
        if (playerCount <= 1) {
            resumeCountdownTicks = Config.resumeDelaySeconds * TICKS_PER_SECOND;
            LOGGER.info("Chunky Auto Pause: resume countdown started ({} ticks)", resumeCountdownTicks);
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        if (resumeCountdownTicks < 0) return;

        MinecraftServer server = event.getServer();
        if (!Config.enabled || server.getPlayerList().getPlayerCount() > 0) {
            resumeCountdownTicks = -1;
            return;
        }

        if (resumeCountdownTicks == 0) {
            ChunkyControl.resume(server);
            resumeCountdownTicks = -1;
        } else {
            if (resumeCountdownTicks % TICKS_PER_SECOND == 0) {
                LOGGER.info("Chunky Auto Pause: resuming in {}s", resumeCountdownTicks / TICKS_PER_SECOND);
            }
            resumeCountdownTicks--;
        }
    }
}
