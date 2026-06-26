package net.chinesespyware.nobob.handler;

import net.chinesespyware.nobob.NoBob;
import cc.polyfrost.oneconfig.events.event.Stage;
import cc.polyfrost.oneconfig.events.event.TickEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class NoBobHandler {

    /**
     * Zeroes the player's walked-distance every tick when enabled.
     * Minecraft's view-bob is driven by distanceWalked; keeping it at 0 prevents
     * the bobbing animation from playing while vanilla "View Bobbing" stays on.
     */
    @Subscribe
    public void onTick(TickEvent event) {
        if (event.stage != Stage.END) return;
        if (!NoBob.config.enabled) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        player.distanceWalkedModified = 0.0F;
        player.prevDistanceWalkedModified = 0.0F;
    }
}
