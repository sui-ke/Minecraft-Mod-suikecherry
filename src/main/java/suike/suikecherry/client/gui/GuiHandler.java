package suike.suikecherry.client.gui;

import suike.suikecherry.entity.boat.ModEntityChestBoat;
import suike.suikecherry.entity.boat.ModEntityChestBoat.ContainerChestBoat;
import suike.suikecherry.tileentity.HasBackSideSignTileEntity;
import suike.suikecherry.block.ModBlockSmithingTable.SmithingTableContainer;

import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GuiID.GUI_CHEST_BOAT) {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof ModEntityChestBoat) {
                // 给服务端容器
                return ((ModEntityChestBoat) entity).getContainerChestBoat(player);
            }
        }
        if (ID == GuiID.GUI_SMITHING_TABLE) {
            return new SmithingTableContainer(player);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GuiID.GUI_CHEST_BOAT) {
            Entity entity = world.getEntityByID(x);
            if (entity instanceof ModEntityChestBoat) {
                // 给客户端 GUI 及容器
                return new GuiChestBoat((ModEntityChestBoat) entity, player);
            }
        }
        if (ID == GuiID.GUI_SIGN_EDIT) {
            BlockPos pos = new BlockPos(x, y, z);
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof HasBackSideSignTileEntity) {
                // 打开告示牌 GUI
                return new GuiEditSign((HasBackSideSignTileEntity) tile);
            }
        }
        if (ID == GuiID.GUI_SMITHING_TABLE) {
            return new GuiSmithingTable(player);
        }

        return null;
    }
}