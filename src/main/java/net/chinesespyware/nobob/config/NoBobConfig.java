package net.chinesespyware.nobob.config;

import net.chinesespyware.nobob.NoBob;
import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.Info;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;

public class NoBobConfig extends Config {

    @Info(title = "A mod that makes the Minecraft bobbing animation more subtle.")
    public static String info = "";

    // OneConfig v1 has no built-in mod toggle, so the switch is declared here.
    @Switch(title = "Enabled")
    public static boolean enabled = true;

    public static final NoBobConfig INSTANCE = new NoBobConfig();

    public NoBobConfig() {
        super(NoBob.MODID + ".json", NoBob.NAME, Category.QOL);
    }
}
