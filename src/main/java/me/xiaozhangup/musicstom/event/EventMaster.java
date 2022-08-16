package me.xiaozhangup.musicstom.event;

import me.xiaozhangup.musicstom.event.display.MOTD;
import me.xiaozhangup.musicstom.event.player.Login;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;

public class EventMaster {

    public static void setup() {
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        MOTD.with(globalEventHandler);
        Login.with(globalEventHandler);
    }

}
