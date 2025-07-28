package suike.suikecherry.event;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.EntityMountEvent;

@Mod.EventBusSubscriber
public class MountBoatEvent {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onMountEvent(EntityMountEvent event) {
        // 检查是否是下马事件
        if (event.isDismounting()) {
            Entity passenger = event.getEntityMounting();
            // 乘客是玩家
            if (passenger instanceof EntityPlayer) {
                Entity vehicle = event.getEntityBeingMounted();
                // 如果是船
                if (vehicle instanceof EntityBoat) {
                    vehicle.motionX = 0;
                    vehicle.motionY = 0;
                    vehicle.motionZ = 0;

                    passenger.setPositionAndUpdate(
                        vehicle.posX,
                        vehicle.getEntityBoundingBox().maxY + 0.3,
                        vehicle.posZ
                    );
                    passenger.motionY = 0;
                    passenger.onGround = true;
                }
            }
        }
    }
}