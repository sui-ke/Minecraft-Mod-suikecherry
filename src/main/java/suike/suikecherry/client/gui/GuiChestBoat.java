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
    private final IInventory playerInventory;
    private final IInventory chestInventory;

    public GuiChestBoat(ModEntityChestBoat chestBoat, EntityPlayer player) {
        super(chestBoat.getContainerChestBoat(player));
        this.playerInventory = player.field_71071_by;
        this.chestInventory = chestBoat.chestInventory;
        this.ySize = 114 + 3 * 18;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(chestInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        fontRenderer.drawString(playerInventory.getDisplayName().getUnformattedText(), 8, ySize - 95, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        int textureWidth = 176;
        int textureHeight = 222;

        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        this.drawTexturedModalRect(x, y, 0, 0, xSize, 3 * 18 + 17);
        this.drawTexturedModalRect(x, y + 3 * 18 + 17, 0, 126, xSize, 96);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}