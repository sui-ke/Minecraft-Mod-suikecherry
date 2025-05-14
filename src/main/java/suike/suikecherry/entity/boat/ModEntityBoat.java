package suike.suikecherry.entity.boat;

import java.util.Map;
import java.util.HashMap;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ModItemBoat;
import suike.suikecherry.entity.boat.ModEntityChestBoat;
import suike.suikecherry.entity.boat.ModEntityChestBoat.ModChestBoatRender;

import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.client.renderer.entity.RenderManager;

import net.minecraftforge.fml.client.registry.IRenderFactory;

// 船类
public abstract class ModEntityBoat extends EntityBoat {
    private ModItemBoat item;
    private String boatName;
    private ResourceLocation texture;

    private static final Map<Class<? extends ModEntityBoat>, BoatData> BOT_MAP = new HashMap<>();

    public ModEntityBoat(World world) {
        super(world);
        BoatData boatData = BOT_MAP.get(this.getClass());
        this.item = boatData.getModItemBoat();
        this.boatName = boatData.getBoatName();
        this.texture = boatData.getTextureFile();
    }

    public static void setBoatData(Class<? extends ModEntityBoat> boatClass, ModItemBoat item, String boatName) {
        BOT_MAP.put(boatClass, new BoatData(item, boatName));
    }

    public String getBoatName() {
        return this.boatName;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    @Override
    public Item getItemBoat() {
        return this.item;
    }

    public void setRotation(float yaw, float pitch) {
        super.setRotation(yaw, pitch);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
    }

    public static class ModBoatRenderFactory implements IRenderFactory<ModEntityBoat> {
        @Override
        public Render<? super ModEntityBoat> createRenderFor(RenderManager manager) {
            return new DynamicBoatRender(manager);
        }

        private static class DynamicBoatRender extends RenderBoat {
            private final ModBoatRender normalRender;
            private final ModChestBoatRender chestRender;

            public DynamicBoatRender(RenderManager manager) {
                super(manager);
                this.normalRender = new ModBoatRender(manager);
                this.chestRender = new ModChestBoatRender(manager);
            }

            @Override
            public void doRender(EntityBoat entity, double x, double y, double z, float entityYaw, float partialTicks) {
                if (entity instanceof ModEntityChestBoat) {
                    chestRender.doRender(entity, x, y, z, entityYaw, partialTicks);
                } else {
                    normalRender.doRender(entity, x, y, z, entityYaw, partialTicks);
                }
            }

            @Override
            protected ResourceLocation getEntityTexture(EntityBoat entity) {
                return normalRender.getEntityTexture(entity);
            }
        }
    }

    public static class ModBoatRender extends RenderBoat {
        public ModBoatRender(RenderManager renderManager) {
            super(renderManager);
        }

        @Override
        public void doRender(EntityBoat entity, double x, double y, double z, float entityYaw, float partialTicks) {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }

        @Override
        protected ResourceLocation getEntityTexture(EntityBoat boat) {
            if (boat instanceof ModEntityBoat) {
                return ((ModEntityBoat) boat).getTexture();
            }
            return super.getEntityTexture(boat);
        }
    }

    private static class BoatData {
        private final String boatName;
        private final ModItemBoat item;
        private final ResourceLocation textureFile;

        public BoatData(ModItemBoat item, String boatName) {
            this.item = item;
            this.boatName = boatName;
            this.textureFile = new ResourceLocation(SuiKe.MODID, "textures/entity/boat/" + boatName.replace("chest_", "") + ".png");
        }

        public ModItemBoat getModItemBoat() {
            return this.item;
        }

        public String getBoatName() {
            return this.boatName;
        }

        public ResourceLocation getTextureFile() {
            return this.textureFile;
        }
    }
}