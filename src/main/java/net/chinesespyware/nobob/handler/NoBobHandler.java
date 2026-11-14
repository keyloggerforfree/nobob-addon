package net.chinesespyware.nobob.handler;

import net.chinesespyware.nobob.config.NoBobConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.polyfrost.oneconfig.api.event.v1.events.TickEvent;

public final class NoBobHandler {

    private NoBobHandler() {
    }

    public static void register() {
        EventManager.register(TickEvent.End.class, NoBobHandler::onTickEnd);
    }

    /**
     * Zeroes the player's walked distance at the end of every tick when enabled.
     * Minecraft's view bob is driven by walkDistance/lastWalkDistance (MCP:
     * distanceWalkedModified/prevDistanceWalkedModified); keeping them at 0 stops
     * the bobbing animation while vanilla "View Bobbing" stays on.
     */
    private static void onTickEnd() {
        if (!NoBobConfig.enabled) return;

        LocalClientPlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;

        player.walkDistance = 0.0F;
        player.lastWalkDistance = 0.0F;
    }
}
