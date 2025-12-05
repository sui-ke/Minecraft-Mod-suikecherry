package suike.suikecherry.client.gui;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.ModBlockSmithingTable;
import suike.suikecherry.block.ModBlockSmithingTable.SmithingTableContainer;
import suike.suikecherry.client.render.entity.RenderPlayerModel;

import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.inventory.GuiContainer;

public class GuiSmithingTable extends GuiContainer {
    private static final ResourceLocation SMITHING_GUI = new ResourceLocation(SuiKe.MODID, "textures/gui/container/smithing.png");
    private final String smithingFont;
    private final String playerInventoryFont;
    private final RenderPlayerModel renderPlayer;

    public GuiSmithingTable(EntityPlayer player) {
        super(new SmithingTableContainer(player));
        this.ySize = 168;
        this.smithingFont = I18n.format("itemGroup.suikecherry.smithing");
        this.playerInventoryFont = player.inventory.getDisplayName().getUnformattedText();
        this.renderPlayer = new RenderPlayerModel(() -> ((SmithingTableContainer) this.inventorySlots).getOutputStack());
    }

    @Override
    public void initGui() {
        super.initGui();
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
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.renderPlayer.renderPlayerModel(x + 143, y + 73, 30, mouseX, mouseY, this.guiLeft, this.guiTop);
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
        this.renderPlayer.restorePlayerEquipment();
    }
}