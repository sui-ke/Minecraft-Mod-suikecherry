package suike.suikecherry.tileentity;

import java.util.Arrays;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;

public class HasBackSideSignTileEntity extends TileEntitySign {
    // 背面文本
    public final ITextComponent[] backText = new ITextComponent[] {new TextComponentString(""),  new TextComponentString(""), new TextComponentString(""), new TextComponentString("")};

    public int editingSide = 0;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        // 读取背面文本
        for (int i = 0; i < 4; ++i) {
            String s = compound.getString("BackText" + (i + 1));
            ITextComponent itextcomponent = ITextComponent.Serializer.jsonToComponent(s);
            this.backText[i] = itextcomponent != null ? itextcomponent : new TextComponentString("");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        // 保存背面文本
        for (int i = 0; i < 4; ++i) {
            String s = ITextComponent.Serializer.componentToJson(this.backText[i]);
            compound.setString("BackText" + (i + 1), s);
        }
        return compound;
    }

    @Override
    public boolean getIsEditable() {
        return true; // 强制设置为可编辑
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.getPos(), 1, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.writeToNBT(new NBTTagCompound());
        writeToNBT(tag);
        return tag;
    }

    // 获取当前编辑面的文本数组
    public ITextComponent[] getTextForEditing() {
        return editingSide == 0 ? this.signText : this.backText;
    }

    public boolean hasText(int slot) {
        // 检查正反面 8 行文本是否非空
        if (slot == 0) {
            for (ITextComponent line : this.signText) {
                if (line != null && !line.getUnformattedText().trim().isEmpty()) {
                    return true;
                }
            }
        } else {
            for (ITextComponent line : this.backText) {
                if (line != null && !line.getUnformattedText().trim().isEmpty()) {
                    return true;
                }
            }
        }

        return false; // 所有行都为空
    }

    public boolean hasAny(int slot) {
        return this.hasText(slot);
    }
}