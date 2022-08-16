package me.xiaozhangup.musicstom;

import de.articdive.jnoise.JNoise;
import me.xiaozhangup.musicstom.event.EventMaster;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;

public class MusicStom {
    public static MinecraftServer minecraftServer = MinecraftServer.init();
    public static InstanceManager instanceManager = MinecraftServer.getInstanceManager();
    public static InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

    public static void main(String[] args) {

        JNoise noise = JNoise.newBuilder()
                .fastSimplex()
                .setFrequency(0.1)
                .build();

        instanceContainer.setGenerator(unit -> {
                    unit.modifier().fillHeight(-64, 1, Block.WATER);
                    Point start = unit.absoluteStart();
                    for (int x = 0; x < unit.size().x(); x++) {
                        for (int z = 0; z < unit.size().z(); z++) {
                            Point bottom = start.add(x, 0, z);

                            synchronized (noise) { // Synchronization is necessary for JNoise
                                double height = noise.getNoise(bottom.x(), bottom.z()) * 16;
                                // * 16 means the height will be between -16 and +16
                                unit.modifier().fill(bottom, bottom.add(1, 0, 1).withY(height), Block.STONE);
                                unit.modifier().fill(bottom.withY(height), bottom.add(1, 0, 1).withY(height + 2D), Block.DIRT);
                                unit.modifier().fill(bottom.withY(height + 2D), bottom.add(1, 0, 1).withY(height + 3D), Block.GRASS_BLOCK);
                            }
                        }
                    }
                    unit.modifier().fillHeight(-64, -63, Block.BEDROCK);
                }
        );

        EventMaster.setup();

        minecraftServer.start("0.0.0.0", 25565);
    }
}
