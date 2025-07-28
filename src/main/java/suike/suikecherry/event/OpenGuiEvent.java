package suike.suikecherry.event;

import suike.suikecherry.SuiKe;
import suike.suikecherry.client.gui.GuiChestBoat;
import suike.suikecherry.entity.boat.ModEntityChestBoat;
import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.packet.packets.OpenChestBoatPacket;

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

import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(Side.CLIENT)
public class OpenGuiEvent {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGuiOpen(GuiOpenEvent event) {
        // 未按下 CTRL
        if (!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && event.getGui() instanceof GuiInventory) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (player.isRiding()) {
                Entity ridingEntity = player.getRidingEntity();
                if (ridingEntity instanceof ModEntityChestBoat) {
                    ModEntityChestBoat boatEntity = (ModEntityChestBoat) ridingEntity;

                    event.setGui(new GuiChestBoat(boatEntity, player));
                    PacketHandler.INSTANCE.sendToServer(
                        new OpenChestBoatPacket(boatEntity.getEntityId())
                    );
                }
            }
        }
    }
}