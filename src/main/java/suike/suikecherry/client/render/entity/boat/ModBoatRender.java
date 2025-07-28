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
    private static final RaftModel RAFT_MODEL = new RaftModel();
    private static final ModelChest CHEST_MODEL = new ModelChest();

    private static ResourceLocation chesTexture;
    private static final Minecraft MC = Minecraft.getMinecraft();
    private static final ResourceLocation chestDefaultTexture = new ResourceLocation("textures/entity/chest/normal.png");
    private static final ResourceLocation chestChristmasTexture = new ResourceLocation("textures/entity/chest/christmas.png");

    public ModBoatRender(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityBoat entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (((ModEntityBoat) entity).isRaft()) {
            this.renderRaft(entity, x, y, z, entityYaw, partialTicks);
        } else {
            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }

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

    private void renderRaft(EntityBoat entity, double x, double y, double z, float entityYaw, float partialTicks) {        
        GlStateManager.pushMatrix();
        this.setupTranslation(x, y, z);
        this.setupRotation(entity, entityYaw, partialTicks);
        this.bindEntityTexture(entity);

        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);

        RAFT_MODEL.render(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();

        if (!this.renderOutlines) {
            this.renderName(entity, x, y, z);
        }
    }

    private void renderChest(ModEntityChestBoat boat, float partialTicks) {
        MC.getTextureManager().bindTexture(chesTexture);

        GlStateManager.scale(0.875, 0.875, 0.875);
        GlStateManager.translate(-0.5, boat.isRaft() ? -0.56 : -0.59, 0.46);

        float lidAngle = boat.chestPart.getLidAngle(partialTicks);
        float adjustedLidAngle = lidAngle > 0 ? 
            1.0f - (float) Math.pow(1.0f - lidAngle, 3) : 
            0.0f;

        CHEST_MODEL.chestLid.rotateAngleX = -(adjustedLidAngle * ((float) Math.PI / 2f));
        CHEST_MODEL.renderAll();
    }

    @Override
    public void renderMultipass(EntityBoat boat, double x, double y, double z, float yaw, float partialTicks) {
        if (!((ModEntityBoat) boat).isRaft()) {
            super.renderMultipass(boat, x, y, z, yaw, partialTicks);
        }
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