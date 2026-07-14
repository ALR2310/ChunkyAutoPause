package alr.chunkyautopause;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = ChunkyAutoPause.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Whether Chunky should automatically pause when a player joins and resume after the resume delay once the server is empty.")
            .define("enabled", true);

    private static final ModConfigSpec.IntValue RESUME_DELAY_SECONDS = BUILDER
            .comment("How many seconds to wait after the last player leaves before resuming Chunky, if no one rejoins in the meantime.")
            .defineInRange("resumeDelaySeconds", 120, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean enabled;
    public static int resumeDelaySeconds;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        enabled = ENABLED.get();
        resumeDelaySeconds = RESUME_DELAY_SECONDS.get();
    }
}
