/*package suike.suikecherry.event;

import suike.suikecherry.SuiKe;
import suike.suikecherry.client.gui.GuiChestBoat;
import suike.suikecherry.entity.boat.ModEntityChestBoat;
import suike.suikecherry.packet.PacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.IInventory;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.client.event.GuiOpenEvent;

// @Mod.EventBusSubscriber
public class OpenGuiEvent {
    // @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiInventory) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            // System.out.println("是否骑乘: " + player.isRiding());
            if (player.isRiding()) {
                Entity ridingEntity = player.getRidingEntity();
                if (ridingEntity instanceof ModEntityChestBoat) {
                    ModEntityChestBoat boatEntity = (ModEntityChestBoat) ridingEntity;

                    event.setGui(new GuiChestBoat(boatEntity, player));
                }
            }
        }
    }
}*/