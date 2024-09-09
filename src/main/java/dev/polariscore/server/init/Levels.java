package dev.polariscore.server.init;

import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator;
import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.DimensionType;

public class Levels {
    public static InstanceContainer OVERWORLD;

    public static void init() {
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        // Overworld
        OVERWORLD = instanceManager.createInstanceContainer(DimensionType.OVERWORLD);

        OVERWORLD.setChunkLoader(new AnvilLoader("world"));
        OVERWORLD.setChunkSupplier(LightingChunk::new);

        JNoise noise = JNoise.newBuilder().perlin(PerlinNoiseGenerator.newBuilder().build())
                .scale(1 / 4.0)
                .clamp(0.5, 1)
                .build();

        OVERWORLD.setGenerator(unit -> {
            Point start = unit.absoluteStart();
            for (int x = 0; x < unit.size().x(); x++) {
                for (int z = 0; z < unit.size().z(); z++) {
                    Point bottom = start.add(x, 0, z);

                    synchronized (noise) { // Synchronization is necessary for JNoise
                        double height = noise.evaluateNoise(bottom.x(), bottom.z()) * 16;
                        // * 16 means the height will be between -16 and +16
                        unit.modifier().fill(bottom, bottom.add(1, 0, 1).withY(height), Block.STONE);
                    }
                }
            }
        });
    }
}
