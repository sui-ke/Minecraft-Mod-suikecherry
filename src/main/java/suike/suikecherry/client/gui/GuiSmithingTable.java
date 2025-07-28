package suike.suikecherry.client.gui;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.ModBlockSmithingTable;
import suike.suikecherry.block.ModBlockSmithingTable.SmithingTableContainer;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.EntityEquipmentSlot;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class GuiSmithingTable extends GuiContainer {
    private static final ResourceLocation SMITHING_GUI = new ResourceLocation(SuiKe.MODID, "textures/gui/container/smithing.png");
    private final EntityPlayer player;
    private final String smithingFont;
    private final String playerInventoryFont;
    private final RenderManager renderManager;

    // 记录物品
    private ItemStack[] originalArmorInventory = new ItemStack[4];
    private ItemStack originalMainHandItem;
    private int originalCurrentItem;

    public GuiSmithingTable(EntityPlayer player) {
        super(new SmithingTableContainer(player));
        this.ySize = 168;
        this.player = player;
        this.smithingFont = I18n.format("itemGroup.suikecherry.smithing");
        this.playerInventoryFont = player.field_71071_by.getDisplayName().getUnformattedText();
        this.renderManager = Minecraft.getMinecraft().getRenderManager();
    }

    @Override
    public void initGui() {
        super.initGui();
        this.savePlayerEquipment();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(this.smithingFont, 45, 18, 4210752);
        fontRenderer.drawString(this.playerInventoryFont, 8, 73, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        GlStateManager.color(1, 1, 1, 1);
        this.mc.getTextureManager().bindTexture(SMITHING_GUI);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
        this.renderPlayerModel(x + 143, y + 73, 30, mouseX, mouseY);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

// 关闭 GUI
    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        this.restorePlayerEquipment();
    }

// 渲染玩家模型
    private void renderPlayerModel(int x, int y, int scale, int mouseX, int mouseY) {
        // 保存原始装备和角度
        this.savePlayerEquipment();
        this.updatePlayerEquipment();
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
        // 清空原有装备
        for (int i = 0; i < this.player.field_71071_by.armorInventory.size(); i++) {
            this.player.field_71071_by.armorInventory.set(i, ItemStack.EMPTY);
        }
        this.player.field_71071_by.mainInventory.set(this.player.field_71071_by.currentItem, ItemStack.EMPTY);

        // 获取输出槽物品
        ItemStack outputStack = ((SmithingTableContainer) this.inventorySlots).getOutputStack();

        if (!outputStack.isEmpty()) {
            if (outputStack.getItem() instanceof ItemArmor) {
                // 如果是盔甲: 装备到对应槽位
                ItemArmor armor = (ItemArmor) outputStack.getItem();
                int slot = armor.armorType.getIndex();
                this.player.field_71071_by.armorInventory.set(slot, outputStack);
            } else {
                // 如果是工具: 装备到主手
                this.player.field_71071_by.mainInventory.set(this.player.field_71071_by.currentItem, outputStack);
            }
        }
    }

// 存储装备
    private void savePlayerEquipment() {
        // 保存盔甲槽
        for (int i = 0; i < this.player.field_71071_by.armorInventory.size(); i++) {
            this.originalArmorInventory[i] = this.player.field_71071_by.armorInventory.get(i);
        }
        // 保存主手物品
        this.originalMainHandItem = this.player.field_71071_by.getCurrentItem();
        this.originalCurrentItem = this.player.field_71071_by.currentItem;
    }

// 还原装备
    private void restorePlayerEquipment() {
        // 还原盔甲槽
        for (int i = 0; i < this.player.field_71071_by.armorInventory.size(); i++) {
            this.player.field_71071_by.armorInventory.set(i, this.originalArmorInventory[i]);
        }
        // 还原主手物品
        this.player.field_71071_by.setInventorySlotContents(this.player.field_71071_by.currentItem, this.originalMainHandItem);
        this.player.field_71071_by.currentItem = this.originalCurrentItem;
    }
}