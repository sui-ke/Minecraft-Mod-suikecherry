package suike.suikecherry.entity.boat;

import java.util.Map;
import java.util.HashMap;

import suike.suikecherry.data.BoatData;
import suike.suikecherry.item.ModItemBoat;

import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;

// 船类
public abstract class ModEntityBoat extends EntityBoat {
    private final String boatName;
    private final ModItemBoat item;
    private final boolean isRaft;
    private final ResourceLocation texture;

    private static final Map<Class<? extends ModEntityBoat>, BoatData> BOT_DATA = new HashMap<>();

    public ModEntityBoat(World world) {
        super(world);
        BoatData boatData = BOT_DATA.get(this.getClass());
        this.item = boatData.getModItemBoat();
        this.boatName = boatData.getBoatName();
        this.texture = boatData.getTextureFile();
        this.isRaft = this.boatName.contains("raft");
        if (this.isRaft) {
            this.setSize(1.375F, 0.5F);
        }
    }

    public static void setBoatData(Class<? extends ModEntityBoat> boatClass, ModItemBoat item, String boatName) {
        BOT_DATA.put(boatClass, new BoatData(item, boatName));
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public boolean isRaft() {
        return this.isRaft;
    }

    public String getBoatName() {
        return this.boatName;
    }

    @Override
    public Item getItemBoat() {
        return this.item;
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        super.setRotation(yaw, pitch);
    }

    @Override
    public double getMountedYOffset() {
        return this.isRaft ? 0.24D : super.getMountedYOffset(); 
    }

    /*@Override
    public void removePassenger(Entity passenger) {
        super.removePassenger(passenger); 
        if (passenger instanceof EntityPlayer) {
            EntityBoat boat = (EntityBoat) this;

            double x = boat.posX;
            double y = boat.getEntityBoundingBox().maxY + 0.1;
            double z = boat.posZ;

            passenger.setPositionAndUpdate(x, y, z);
        }
    }*/
}