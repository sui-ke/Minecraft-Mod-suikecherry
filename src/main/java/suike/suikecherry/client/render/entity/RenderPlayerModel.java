package suike.suikecherry.client.render.entity;

import java.util.function.Supplier;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class RenderPlayerModel {

    private final EntityPlayer player;
    private final RenderManager renderManager;
    private final Supplier<ItemStack> handItem;

    // 记录物品
    private ItemStack[] originalArmorInventory = new ItemStack[4];
    private ItemStack originalMainHandItem;
    private int originalCurrentItem;

    public RenderPlayerModel() {
        this(null);
    }
    public RenderPlayerModel(Supplier<ItemStack> handItem) {
        this.player = Minecraft.getMinecraft().player;
        this.renderManager = Minecraft.getMinecraft().getRenderManager();
        this.handItem = handItem;
        this.savePlayerEquipment();
    }

// 渲染玩家模型
    public void renderPlayerModelFixed(int x, int y, int scale) {
        int fakeMouseX = x + 143;
        int fakeMouseY = y + 74;
        int fakeGuiLeft = 0;
        int fakeGuiTop = 0;
        this.renderPlayerModel(x, y, scale, fakeMouseX, fakeMouseY, fakeGuiLeft, fakeGuiTop);
    }

    public void renderPlayerModel(int x, int y, int scale, int mouseX, int mouseY, int guiLeft, int guiTop) {
        // 保存原始装备
        this.savePlayerEquipment();
        this.updatePlayerEquipment();

        // 保存原始角度
        float originalYaw = this.player.rotationYaw;
        float originalPitch = this.player.rotationPitch;
        float originalYawHead = this.player.rotationYawHead;
        float originalPrevYawHead = this.player.prevRotationYawHead;
        float originalRenderYawOffset = this.player.renderYawOffset;

        // 设置模型旋转 (跟随鼠标)
        float yaw = -((float)Math.atan((mouseX - guiLeft - 143) / 80.0F)) * 20.0F;
        float pitch = (float)Math.atan((mouseY - guiTop - 74) / 40.0F) * 20.0F;
        player.rotationYaw = yaw;
        player.rotationPitch = pitch;
        player.rotationYawHead = yaw;
        player.prevRotationYawHead = yaw;
        player.renderYawOffset = yaw;

        int currentFramebuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GlStateManager.translate(x, y, 50.0F);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);

        // 渲染玩家实体
        this.renderManager.setPlayerViewY(180.0F);
        this.renderManager.setRenderShadow(false);
        this.renderManager.renderEntity(player, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        this.renderManager.setRenderShadow(true);

        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
        OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, currentFramebuffer);

        // 恢复状态
        this.player.rotationYaw = originalYaw;
        this.player.rotationPitch = originalPitch;
        this.player.rotationYawHead = originalYawHead;
        this.player.prevRotationYawHead = originalPrevYawHead;
        this.player.renderYawOffset = originalRenderYawOffset;
        this.restorePlayerEquipment();
    }

// 更新玩家装备
    private void updatePlayerEquipment() {
        if (this.handItem == null) return;

        // 清空原有装备
        for (int i = 0; i < this.player.inventory.armorInventory.size(); i++) {
            this.player.inventory.armorInventory.set(i, ItemStack.EMPTY);
        }
        this.player.inventory.mainInventory.set(this.player.inventory.currentItem, ItemStack.EMPTY);

        // 获取输出槽物品
        ItemStack outputStack = this.handItem.get();

        if (!outputStack.isEmpty()) {
            if (outputStack.getItem() instanceof ItemArmor) {
                // 如果是盔甲: 装备到对应槽位
                ItemArmor armor = (ItemArmor) outputStack.getItem();
                int slot = armor.armorType.getIndex();
                this.player.inventory.armorInventory.set(slot, outputStack);
            } else {
                // 如果是工具: 装备到主手
                this.player.inventory.mainInventory.set(this.player.inventory.currentItem, outputStack);
            }
        }
    }

// 存储装备
    private void savePlayerEquipment() {
        if (this.handItem == null) return;

        // 保存盔甲槽
        for (int i = 0; i < this.player.inventory.armorInventory.size(); i++) {
            this.originalArmorInventory[i] = this.player.inventory.armorInventory.get(i);
        }
        // 保存主手物品
        this.originalMainHandItem = this.player.inventory.getCurrentItem();
        this.originalCurrentItem = this.player.inventory.currentItem;
    }

// 还原装备
    public void restorePlayerEquipment() {
        if (this.handItem == null) return;

        // 还原盔甲槽
        for (int i = 0; i < this.player.inventory.armorInventory.size(); i++) {
            this.player.inventory.armorInventory.set(i, this.originalArmorInventory[i]);
        }
        // 还原主手物品
        this.player.inventory.setInventorySlotContents(this.player.inventory.currentItem, this.originalMainHandItem);
        this.player.inventory.currentItem = this.originalCurrentItem;
    }
}