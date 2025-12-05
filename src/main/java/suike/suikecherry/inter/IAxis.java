package suike.suikecherry.inter;

import java.util.List;

import net.minecraft.util.EnumFacing;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;

import com.google.common.collect.Lists;

public interface IAxis {
// 获取方向 编辑的面
    // 根据方块朝向和点击面获取编辑的面
    public static final List<Integer> validOrientations = Lists.newArrayList(0, 4, 8, 12);
    default int getSlotFromFacings(int axis, EnumFacing clickFacing, EntityPlayer player) {
        if (validOrientations.contains(axis)) {
            // 处理正方向
            boolean isNorthSouth = getBlockFacing(axis);

            if (isNorthSouth) {
                if (clickFacing == EnumFacing.SOUTH) return 0;
                if (clickFacing == EnumFacing.NORTH) return 1;
            } else {
                if (clickFacing == EnumFacing.EAST) return 0;
                if (clickFacing == EnumFacing.WEST) return 1;
            }
        }

        // 处理非正方向
        float yaw = player.rotationYaw;
        yaw = (yaw % 360 + 360) % 360; // 规范化到0-360
        float adjustedYaw = (yaw + 180) % 360; // 转换为北向0度
        
        if (axis >= 1 && axis <= 3) {        // 东北方向组
            if (adjustedYaw >= 0 && adjustedYaw < 90) {
                return 0; // 面朝东北看正面
            } else if (adjustedYaw >= 180 && adjustedYaw < 270) {
                return 1; // 面朝西南看背面
            }
        } else if (axis >= 5 && axis <= 7) { // 东南方向组
            if (adjustedYaw >= 90 && adjustedYaw < 180) {
                return 0; // 面朝东南看正面
            } else if (adjustedYaw >= 270) {
                return 1; // 面朝西北看背面
            }
        } else if (axis >= 9 && axis <= 11) { // 西南方向组
            if (adjustedYaw >= 180 && adjustedYaw < 270) {
                return 0; // 面朝西南看正面
            } else if (adjustedYaw >= 0 && adjustedYaw < 90) {
                return 1; // 面朝东北看背面
            }
        } else if (axis >= 13 && axis <= 15) { // 西北方向组
            if (adjustedYaw >= 270) {
                return 0; // 面朝西北看正面
            } else if (adjustedYaw >= 90 && adjustedYaw < 180) {
                return 1; // 面朝东南看背面
            }
        }

        return -1; // 不在有效角度范围内
    }

// 正方向获取
    // 通过元数据获取方向
    default EnumFacing getCardinalFacing(int meta) {
        switch (meta) {
            case 4:
                return EnumFacing.EAST;
            case 8:
                return EnumFacing.SOUTH;
            case 12:
                return EnumFacing.WEST;
            default:
                return EnumFacing.NORTH;
        }
    }

    // 是否是南北
    default boolean isNorthSouth(int meta) {
        switch (meta) {
            case 0:
            case 8:
                return true;
            case 4:
            case 12:
                return false;
            default:
                return false;
        }
    }

    // 是否是正东南西北
    default boolean isCardinalFacing(IBlockState state, int meta) {
        return (meta == 0 || meta == 4 ||
                meta == 8 || meta == 12);
    }

    // 获取正方向方块展示方向
    default boolean getBlockFacing(int axis) {
        int mainGroup = (axis / 4) % 4;
        return mainGroup % 2 == 0;
    }
}