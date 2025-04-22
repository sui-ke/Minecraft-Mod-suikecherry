package suike.suikecherry.sentity.boat;

import suike.suikecherry.sitem.ItemBase;

import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraft.entity.item.EntityBoat;

//船的实体类
public class CherryBoatEntity extends EntityBoat {
    public CherryBoatEntity(World world) {
        super(world);
    }

    @Override
    public Item getItemBoat() {
        return ItemBase.CHERRY_BOAT;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

    public void setCherryBoatRotation(float yaw, float pitch) {
        setRotation(yaw, pitch);
    }
}