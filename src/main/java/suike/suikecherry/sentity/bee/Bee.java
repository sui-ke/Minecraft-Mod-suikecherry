package suike.suikecherry.sentity.bee;

import java.lang.reflect.Field;

import thedarkcolour.futuremc.entity.bee.EntityBee;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;

public class Bee {
    public static void spawnBee(World world, BlockPos beeHivePos, BlockPos spawnPos) {
        EntityBee bee = new EntityBee(world);

        /*设置蜜蜂生成位置*/bee.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());

        try {
            //为蜜蜂绑定蜂巢
            Field hivePosField = EntityBee.class.getDeclaredField("hivePos");
            hivePosField.setAccessible(true);
            hivePosField.set(bee, beeHivePos);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        /*生成蜜蜂*/world.spawnEntity(bee);
    }
}