package suike.suikecherry.client.render.entity.boat;

import java.util.Calendar;

import suike.suikecherry.entity.boat.ModEntityBoat;
import suike.suikecherry.entity.boat.ModEntityChestBoat;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.client.renderer.entity.RenderManager;

public class ModBoatRender extends RenderBoat {
    private static final ModelChest CHEST_MODEL = new ModelChest();

    private static ResourceLocation chesTexture;
    private static final ResourceLocation chestDefaultTexture = new ResourceLocation("textures/entity/chest/normal.png");
    private static final ResourceLocation chestChristmasTexture = new ResourceLocation("textures/entity/chest/christmas.png");

    public ModBoatRender(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityBoat entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        if (entity instanceof ModEntityChestBoat) {
            ModEntityChestBoat boat = (ModEntityChestBoat) entity;
            if (boat.getEntityId() != entity.getEntityId()) {
                return;
            }

            GlStateManager.pushMatrix();
            this.setupTranslation(x, y, z);
            this.setupRotation(boat, entityYaw, partialTicks);

            Vec3d offset = boat.chestPart.getChestOffset();
            GlStateManager.translate(offset.x, -offset.y, offset.z);
            this.renderChest(boat, partialTicks);
            GlStateManager.popMatrix();
        }
    }

    private static final Minecraft MC = Minecraft.getMinecraft();
    private void renderChest(ModEntityChestBoat boat, float partialTicks) {
        MC.getTextureManager().bindTexture(chesTexture);

        GlStateManager.scale(0.875, 0.875, 0.875);
        GlStateManager.translate(-0.5, -0.59, 0.46);

        float lidAngle = boat.chestPart.getLidAngle(partialTicks);
        float adjustedLidAngle = lidAngle > 0 ? 
            1.0f - (float) Math.pow(1.0f - lidAngle, 3) : 
            0.0f;

        CHEST_MODEL.chestLid.rotateAngleX = -(adjustedLidAngle * ((float) Math.PI / 2f));
        CHEST_MODEL.renderAll();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBoat boat) {
        return ((ModEntityBoat) boat).getTexture();
    }

    public static void setChestTexture() {
        Calendar calendar = Calendar.getInstance();
        boolean isChristmas = calendar.get(2) + 1 == 12 &&
                                calendar.get(5) >= 24 &&
                                calendar.get(5) <= 26;

        chesTexture = isChristmas ? chestChristmasTexture : chestDefaultTexture;
    }
}