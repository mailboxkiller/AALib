package xyz.aadev.aalib.common.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldHelper {
    private WorldHelper() {
    }

    public static void notifyBlockUpdate(World world, BlockPos blockPos, IBlockState oldState, IBlockState newState) {
        oldState = oldState == null ? world.getBlockState(blockPos) : oldState;

        newState = newState == null ? oldState : newState;

        world.notifyBlockUpdate(blockPos, oldState, newState, 3);
    }
}
