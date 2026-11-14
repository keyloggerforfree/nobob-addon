package net.chinesespyware.nobob;

import net.chinesespyware.nobob.config.NoBobConfig;
import net.chinesespyware.nobob.handler.NoBobHandler;
import net.fabricmc.api.ClientModInitializer;

public class NoBob implements ClientModInitializer {

    public static final String MODID = "@MOD_ID@";
    public static final String NAME = "@MOD_NAME@";
    public static final String VERSION = "@MOD_VERSION@";

    @Override
    public void onInitializeClient() {
        NoBobConfig.INSTANCE.preload();
        NoBobHandler.register();
    }
}
