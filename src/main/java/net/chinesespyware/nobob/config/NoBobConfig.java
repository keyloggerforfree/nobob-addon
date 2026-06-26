package net.chinesespyware.nobob.config;

import net.chinesespyware.nobob.NoBob;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Info;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;

public class NoBobConfig extends Config {

    @Info(
        text = "A mod that makes the Minecraft bobbing animation more subtle.",
        type = InfoType.INFO,
        size = 2
    )
    boolean _info = false;

    public NoBobConfig() {
        super(new Mod(NoBob.NAME, ModType.UTIL_QOL), NoBob.MODID + ".json");
        initialize();
    }
}
