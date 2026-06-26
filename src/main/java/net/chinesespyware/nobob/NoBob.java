package net.chinesespyware.nobob;

import net.chinesespyware.nobob.config.NoBobConfig;
import net.chinesespyware.nobob.handler.NoBobHandler;
import cc.polyfrost.oneconfig.events.EventManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = NoBob.MODID, name = NoBob.NAME, version = NoBob.VERSION)
public class NoBob {

    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";

    public static NoBobConfig config;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new NoBobConfig();
        EventManager.INSTANCE.register(new NoBobHandler());
    }
}
