package suike.suikecherry.inter;

import net.minecraft.util.EnumFacing;

public interface ICardinal {
    // 根据方块朝向和点击面获取编辑的面
    default int getSlotFromFacings(EnumFacing blockFacing, EnumFacing clickFacing) {
        if (isNorthSouth(blockFacing)) {
            // 方块南北朝向，南北面编辑
            if (clickFacing == EnumFacing.SOUTH) return 0;
            if (clickFacing == EnumFacing.NORTH) return 1;
        } else {
            // 方块东西朝向，东西面编辑
            if (clickFacing == EnumFacing.EAST) return 0;
            if (clickFacing == EnumFacing.WEST) return 1;
        }

        return -1;
    }

    // 获取面朝的方向
    default int getBlockFacing(EnumFacing blockFacing) {
        switch (blockFacing) {
            case WEST:
                return 1;
            case EAST:
                return 3;
            case SOUTH:
                return 0;
            case NORTH:
            default:
                return 2;
        }
    }

    // 检查南北
    default boolean isNorthSouth(EnumFacing blockFacing) {
        switch (blockFacing) {
            case WEST:
            case EAST:
                return false; // 东西方向
            case NORTH:
            case SOUTH:
            default:
                return true; // 南北方向
        }
    }

    default boolean isSouthWest(EnumFacing blockFacing) {
        switch (blockFacing) {
            case WEST:
            case SOUTH:
                return true; // 西南方向
            case EAST:
            case NORTH:
            default:
                return false; // 东北方向
        }
    }
}