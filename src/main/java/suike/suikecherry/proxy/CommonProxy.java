package suike.suikecherry.proxy;

import suike.suikecherry.SuiKe;
import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.client.gui.GuiHandler;
import suike.suikecherry.tileentity.HangingSignTileEntity;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonProxy {
    public void registerItemRenderer(Item item, int meta, String id) {}

    public void register() {
        PacketHandler.registerServerPackets();

        registerGui();

        registerTileEntity(HangingSignTileEntity.class, "hanging_sign");
    }

    public void registerGui() {
        NetworkRegistry.INSTANCE.registerGuiHandler(SuiKe.instance, new GuiHandler());
    }

    public void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String name) {
        GameRegistry.registerTileEntity(HangingSignTileEntity.class, new ResourceLocation(SuiKe.MODID, name));
    }
}