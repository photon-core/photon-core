package dev.polariscore.server.blocks.placements;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.gamedata.tags.Tag;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class GatePlacementRule extends BlockPlacementRule { // TODO: Waterlogged
    public GatePlacementRule(@NotNull Block block) {
        super(block);
    }

    @Override
    public @NotNull Block blockUpdate(@NotNull UpdateState updateState) {
        return updateState.currentBlock().withProperty("in_wall", inWall(updateState.currentBlock(), updateState.blockPosition(), updateState.instance()));
    }

    @Override
    public @Nullable Block blockPlace(@NotNull PlacementState placementState) {
        float yaw = placementState.playerPosition() != null ? placementState.playerPosition().yaw() : 136;
        BlockFace blockFace = BlockFace.fromYaw(yaw);
        Block block = placementState.block().withProperty("facing", blockFace.name().toLowerCase(Locale.ENGLISH));

        block = block.withProperty("in_wall", inWall(block, placementState.placePosition(), placementState.instance()));
        return block;
    }

    private static String inWall(Block block, Point point, Block.Getter instance) {
        String direction = block.getProperty("facing");
        
        Tag tag = MinecraftServer.getTagManager().getTag(Tag.BasicType.BLOCKS, "minecraft:walls");
        assert tag != null;
        
        if (direction.equals("north") || direction.equals("south")) {
            if (tag.contains(instance.getBlock(point.relative(BlockFace.EAST)).namespace()) ||
                    tag.contains(instance.getBlock(point.relative(BlockFace.WEST)).namespace())) {
                return "true";
            }
        } else if (direction.equals("east") || direction.equals("west")) {
            if (tag.contains(instance.getBlock(point.relative(BlockFace.NORTH)).namespace()) ||
                    tag.contains(instance.getBlock(point.relative(BlockFace.SOUTH)).namespace())) {
                return "true";
            }
        }

        return "false";
    }
}
