package suike.suikecherry.proxy;

import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.particle.ModParticle;
import suike.suikecherry.particle.CherryParticle;
import suike.suikecherry.tileentity.HangingSignTileEntity;
import suike.suikecherry.client.render.tileentity.TileEntityRender;
import suike.suikecherry.client.render.tileentity.HangingSignTileEntityRender;

import net.minecraft.item.Item;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends CommonProxy {
    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
    }

    @Override
    public void register() {
        PacketHandler.registerClientPackets();

        registerGui();
        registerParticle();

        registerTileEntityRender(HangingSignTileEntity.class, new HangingSignTileEntityRender(), "hanging_sign");
    }

    public void registerTileEntityRender(Class<? extends TileEntity> tileEntityClass, TileEntityRender tileEntityRender, String name) {
        super.registerTileEntity(tileEntityClass, name);
        ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, tileEntityRender);
    }

    public static void registerParticle() {
        Minecraft.getMinecraft().effectRenderer.registerParticle(ModParticle.CHERRY_PARTICLE_ID, new CherryParticle.CherryParticleFactory());
    }
}