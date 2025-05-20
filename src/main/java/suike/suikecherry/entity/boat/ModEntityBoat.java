package suike.suikecherry.entity.boat;

import java.util.Map;
import java.util.HashMap;

import suike.suikecherry.item.ModItemBoat;

import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.item.EntityBoat;

// 船类
public abstract class ModEntityBoat extends EntityBoat {
    private final String boatName;
    private final ModItemBoat item;
    private final ResourceLocation texture;

    private static final Map<Class<? extends ModEntityBoat>, BoatData> BOT_DATA = new HashMap<>();

    public ModEntityBoat(World world) {
        super(world);
        BoatData boatData = BOT_DATA.get(this.getClass());
        this.item = boatData.getModItemBoat();
        this.boatName = boatData.getBoatName();
        this.texture = boatData.getTextureFile();
    }

    public static void setBoatData(Class<? extends ModEntityBoat> boatClass, ModItemBoat item, String boatName) {
        BOT_DATA.put(boatClass, new BoatData(item, boatName));
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public void setRotation(float yaw, float pitch) {
        super.setRotation(yaw, pitch);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    @Override
    public Item getItemBoat() {
        return this.item;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }
}