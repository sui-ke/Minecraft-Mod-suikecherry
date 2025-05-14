package suike.suikecherry.proxy;

import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.tileentity.HangingSignTileEntity;
import suike.suikecherry.tileentity.render.TileEntityRenderer;
import suike.suikecherry.tileentity.render.HangingSignTileEntityRender;
import suike.suikecherry.tileentity.HangingSignTileEntity;

import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends CommonProxy {
    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
    }

    @Override
    public void registerTileEntity() {
        PacketHandler.registerClientPackets();

        registerTileEntityRenderer(HangingSignTileEntity.class, new HangingSignTileEntityRender(), "hanging_sign");
    }

    public void registerTileEntityRenderer(Class<? extends TileEntity> tileEntityClass, TileEntityRenderer tileEntityRenderer, String name) {
        registerTileEntity(tileEntityClass, name);
        ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, tileEntityRenderer);
    }

    @Override
    public void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String name) {
        super.registerTileEntity(HangingSignTileEntity.class, name);
    }
}