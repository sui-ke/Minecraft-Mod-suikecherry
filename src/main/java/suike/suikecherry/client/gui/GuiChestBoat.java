package suike.suikecherry.client.gui;

import suike.suikecherry.entity.boat.ModEntityChestBoat;
import suike.suikecherry.entity.boat.ModEntityChestBoat.ChestInventory;
import suike.suikecherry.entity.boat.ModEntityChestBoat.ContainerChestBoat;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.inventory.GuiContainer;

public class GuiChestBoat extends GuiContainer {
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final String chestFont;
    private final String playerInventoryFont;

    public GuiChestBoat(ModEntityChestBoat chestBoat, EntityPlayer player) {
        super(chestBoat.getContainerChestBoat(player));
        this.chestFont = chestBoat.chestInventory.getDisplayName().getUnformattedText();
        this.playerInventoryFont = player.inventory.getDisplayName().getUnformattedText();
        this.ySize = 168;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(this.chestFont, 8, 6, 4210752);
        fontRenderer.drawString(this.playerInventoryFont, 8, 73, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        this.drawTexturedModalRect(x, y, 0, 0, xSize, 3 * 18 + 17);
        this.drawTexturedModalRect(x, y + 3 * 18 + 17, 0, 126, xSize, 96);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}