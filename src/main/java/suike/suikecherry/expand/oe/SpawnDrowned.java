package suike.suikecherry.expand.oe;

import java.util.Random;

import suike.suikecherry.expand.Examine;

import com.sirsquidly.oe.entity.EntityDrowned;

import net.minecraft.world.World;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.material.Material;

public class SpawnDrowned {
    // 在周围生成溺尸
    public static void spawn(World world, BlockPos center, int minCount, int maxCount, Random rand) {
        int count = minCount + rand.nextInt(maxCount - minCount + 1);

        for (int i = 0; i < count; i++) {
            // 随机角度和半径
            float angle = rand.nextFloat() * 360;
            double radius = 6 + rand.nextDouble() * 4; // 6-10格距离

            // 计算水平位置
            int x = (int) (center.getX() + Math.cos(Math.toRadians(angle)) * radius);
            int z = (int) (center.getZ() + Math.sin(Math.toRadians(angle)) * radius);

            // 随机生成高度
            int y = (int) (center.getY() + 5 + rand.nextInt(8));
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);

            // 向上寻找水空间（最高到海平面+2）
            while (pos.getY() <= world.getSeaLevel() + 2 && 
                world.getBlockState(pos).getMaterial() != Material.WATER) {
                pos.move(EnumFacing.UP);
            }

            // 向下寻找水空间（确保在水底）
            while (pos.getY() > center.getY() - 3 && 
                world.getBlockState(pos).getMaterial() != Material.WATER) {
                pos.move(EnumFacing.DOWN);
            }

            // 创建溺尸实体
            EntityDrowned drowned = new EntityDrowned(world);
            drowned.setPosition(x, pos.getY() + 0.5, z);
            drowned.enablePersistence(); // 防止被清除

            // 添加到世界
            world.spawnEntity(drowned);
        }
    }
}