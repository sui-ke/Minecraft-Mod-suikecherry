package suike.suikecherry.proxy;

import suike.suikecherry.SuiKe;
import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.tileentity.HangingSignTileEntity;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
    public void registerItemRenderer(Item item, int meta, String id) {}

    public void registerTileEntity() {
        PacketHandler.registerServerPackets();

        registerTileEntity(HangingSignTileEntity.class, "hanging_sign");
    }

    public void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String name) {
        GameRegistry.registerTileEntity(HangingSignTileEntity.class, new ResourceLocation(SuiKe.MODID, name));
    }
}