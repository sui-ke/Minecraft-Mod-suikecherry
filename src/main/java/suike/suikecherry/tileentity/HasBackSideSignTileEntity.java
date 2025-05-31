package suike.suikecherry.tileentity;

import java.util.Map;
import java.util.HashMap;

import suike.suikecherry.SuiKe;
import suike.suikecherry.api.IBlockSign;
import suike.suikecherry.api.IBlockHangingSign;
import suike.suikecherry.block.ModBlockSign;
import suike.suikecherry.block.ModBlockSignWall;
import suike.suikecherry.block.ModBlockHangingSign;
import suike.suikecherry.block.ModBlockHangingSignAttached;
import suike.suikecherry.client.gui.GuiID;
import suike.suikecherry.client.gui.GuiEditSign;
import suike.suikecherry.data.RenderPosition;
import suike.suikecherry.data.SignGuiTexturesData;
import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.packet.packets.SignTextUpdatePacket;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import com.google.common.collect.ImmutableMap;

public class HasBackSideSignTileEntity extends TileEntitySign {
    // 背面文本
    public final ITextComponent[] backText = new ITextComponent[] {new TextComponentString(""),  new TextComponentString(""), new TextComponentString(""), new TextComponentString("")};

    // public boolean waxed = false;
    public boolean isUsing = false;
    private int editedSlot = 0;
    private int hasTextSides = -1;

    public int getTextSides() {
        return this.hasTextSides;
    }

    public int getEditedSlot() {
        return this.editedSlot;
    }

    public void setEditedSlot(int editedSlot) {
        this.editedSlot = editedSlot;
    }

    public Block getParentBlock() {
        return this.getParentBlockState().getBlock();
    }
    public IBlockState getParentBlockState() {
        return this.world.getBlockState(this.pos);
    }

    public SignGuiTexturesData getEditedBlockType() {
        String type = this.getParentBlock().getRegistryName().toString().replace("suikecherry:", "");

        // 判断方块类型
        if (type.contains("hanging")) {
            return new SignGuiTexturesData("hanging_sign", type.substring(0, type.indexOf("_hanging_sign")));
        }

        return new SignGuiTexturesData("sign", type.substring(0, type.indexOf("_sign")));
    }

    @Override
    public boolean getIsEditable() {
        return true; // 强制设置为可编辑
    }

    // 通过面获取文本数组
    public ITextComponent[] getTextForSlot(int slot) {
        return slot == 0 ? this.signText : this.backText;
    }

    // 更新面
    public void upTextSides() {
        boolean has0 = this.hasText(0);
        boolean has1 = this.hasText(1);

        if (has0 && has1) {
            this.hasTextSides = 2; // 两面都有
        } else if (has0) {
            this.hasTextSides = 0; // 仅正面（0号槽位）有
        } else if (has1) {
            this.hasTextSides = 1; // 仅背面（1号槽位）有
        } else {
            this.hasTextSides = -1; // 都没有
        }

        this.removeTextRenderPos(has0 ? -1 : 0);
        this.removeTextRenderPos(has1 ? -1 : 1);
    }

// 检查是否有占位
    public boolean hasText(int slot) {
        // 检查正反面 8 行文本是否非空
        ITextComponent[] targetText = (slot == 0) ? signText : backText;
        for (ITextComponent line : targetText) {
            if (line != null && !line.getUnformattedText().trim().isEmpty()) {
                return true;
            }
        }

        if (this.isUsing) {
            return true; // 此方块正在编辑, 不允许修改
        }

        return false; // 所有行都为空
    }

    // 检查这一面是否有 文本 或 展示的物品
    public boolean hasAny(int slot) {
        return this.hasText(slot);
    }

// 编辑文本
    // 编辑文本: 根据 slot 处理不同面的编辑
    public boolean editText(EntityPlayer player) {
        int slot = this.getEditedSlot();
        if (slot != 0 && slot != 1) return false;

        if (world.isRemote) {
            // 在打开 GUI 之前检查使用状态
            if (this.isUsing) {
                // 正在使用, 不打开 GUI 直接返回
                player.func_146105_b(new TextComponentString(I18n.format("suikecherry.sign.isUsing")), true);
                return false;
            }

            // 客户端直接打开编辑界面
            player.openGui(SuiKe.instance, GuiID.GUI_SIGN_EDIT, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }

        this.setPlayer(player);

        // 发送初始文本数据到客户端
        PacketHandler.INSTANCE.sendTo(
            new SignTextUpdatePacket(pos, slot, this.getTextForSlot(slot), true),
            (EntityPlayerMP) player
        );
        return true;
    }

    // 更新指定面的文本
    public void updateText(int slot, ITextComponent[] text, boolean isLocking) {
        this.isUsing = isLocking;

        if (slot == 0) 
            System.arraycopy(text, 0, this.signText, 0, text.length);
        else if (slot == 1) 
            System.arraycopy(text, 0, this.backText, 0, text.length);
        else return;

        if (world.isRemote) {
            // 本地服务器线程更新文本
            MinecraftServer server = FMLClientHandler.instance().getServer();
            if (server != null) {
                server.addScheduledTask(() -> {
                    World serverWorld = server.getWorld(world.provider.getDimension());
                    TileEntity tile = serverWorld.getTileEntity(pos);
                    if (tile instanceof HasBackSideSignTileEntity) {
                        ((HasBackSideSignTileEntity) tile).forceUpdateText(slot, text);
                    }
                });
            }
            world.markBlockRangeForRenderUpdate(pos, pos);
        }
        else {
            this.markDirty();
            // 广播更新到所有客户端
            world.notifyBlockUpdate(pos, getBlockType().getDefaultState(), getBlockType().getDefaultState(), 3);
            PacketHandler.INSTANCE.sendToAllAround(
                new SignTextUpdatePacket(this.pos, slot, text, isLocking),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 64)
            );
        }
        this.upTextSides();
    }

    public void forceUpdateText(int slot, ITextComponent[] text) {
        if (slot == 0)
            System.arraycopy(text, 0, signText, 0, 4);
        else
            System.arraycopy(text, 0, backText, 0, 4);

        this.markDirty();
        WorldServer worldServer = (WorldServer)world;
        worldServer.getChunkFromBlockCoords(pos).markDirty();
    }

// 存储 & 读取
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        // 读取背面文本
        if (compound.hasKey("BackText")) {
            NBTTagCompound backTextTag = compound.getCompoundTag("BackText");
            for (int i = 0; i < 4; ++i) {
                String s = backTextTag.getString("Line" + (i + 1));
                backText[i] = ITextComponent.Serializer.jsonToComponent(s);
            }
        }
        this.upTextSides();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        // 墙上的告示牌只有正面文本
        if (!(this.getParentBlock() instanceof ModBlockSignWall)) {
            // 保存背面文本
            NBTTagCompound backTextTag = new NBTTagCompound();
            for (int i = 0; i < 4; ++i) {
                String s = ITextComponent.Serializer.componentToJson(this.backText[i]);
                backTextTag.setString("Line" + (i + 1), s);
            }
            compound.setTag("BackText", backTextTag);
        }
        return compound;
    }

// 存储渲染位置
    private RenderPosition textRenderPos_0;
    private RenderPosition textRenderPos_1;

    private RenderPosition getTextRenderPos(int slot) {
        if (slot == 0)
            return textRenderPos_0;
        else if (slot == 1)
            return textRenderPos_1;
        else
        return null;
    }

    private void removeTextRenderPos(int slot) {
        if (slot == 0)
            this.textRenderPos_0 = null;
        else if (slot == 1)
            this.textRenderPos_1 = null;
    }

    private void setTextRenderPos(int slot, RenderPosition renderPos) {
        if (slot == 0)
            this.textRenderPos_0 = renderPos;
        else if (slot == 1)
            this.textRenderPos_1 = renderPos;
    }

// 获取渲染位置
    public RenderPosition getTextRenderPosition(int slot) {
        RenderPosition renderPos = this.getTextRenderPos(slot);

        if (renderPos != null) return renderPos; // 有已存储的渲染位置直接返回, 避免重复获取

        IBlockState state = this.getWorld().getBlockState(this.getPos());
        Block block = state.getBlock();

        if (block instanceof IBlockHangingSign) {
            return hangingSignGetTextRenderPosition(slot, block, state);
        }
        else if (block instanceof IBlockSign) {
            return signGetTextRenderPosition(slot, block, state);
        }

        // 默认返回: 北方 0 槽
        return SIGN_ROTATION_MAP.get(0);
    }

// 告示牌
    private RenderPosition signGetTextRenderPosition(int slot, Block block, IBlockState state) {
        // 正方向告示牌
        if (block instanceof ModBlockSignWall || ((ModBlockSign) block).isCardinalFacing(state, block.getMetaFromState(state))) {
            boolean isInWall = block instanceof ModBlockSignWall;
            boolean isNorthSouth = isInWall
                ? ((ModBlockSignWall) block).isNorthSouth(state.getValue(ModBlockSignWall.FACING))
                : ((ModBlockSign) block).isNorthSouth(block.getMetaFromState(state));

            if (block instanceof ModBlockSign) {
                if (isNorthSouth) {
                    if (slot == 0) {
                        this.setTextRenderPos(slot, SIGN_ROTATION_MAP.get(0)); // 北方
                    } else {
                        this.setTextRenderPos(slot, SIGN_ROTATION_MAP.get(8)); // 南方
                    }
                } else {
                    if (slot == 0) {
                        this.setTextRenderPos(slot, SIGN_ROTATION_MAP.get(12)); // 西方
                    } else {
                        this.setTextRenderPos(slot, SIGN_ROTATION_MAP.get(4)); // 东方 
                    }
                }
            }
            // 墙上的告示牌
            else if (block instanceof ModBlockSignWall) {
                this.setTextRenderPos(
                    slot, 
                    SIGN_ROTATION_WALL_MAP.get(
                        ((ModBlockSignWall) block).getBlockFacing(
                            state.getValue(ModBlockSignWall.FACING)
                        )
                    )
                );
            }

            return this.getTextRenderPos(slot); // 返回刚存储的渲染位置
        }
        // 精确方向告示牌
        else if (block instanceof ModBlockSign) {
            int meta = block.getMetaFromState(state);
            if (slot == 1) {
                meta = (meta + 8) % 16; // 计算对称面meta值
            }
            this.setTextRenderPos(slot, SIGN_ROTATION_MAP.get(meta));
            return this.getTextRenderPos(slot);
        }

        return SIGN_ROTATION_MAP.get(0);
    }

// 悬挂告示牌
    private RenderPosition hangingSignGetTextRenderPosition(int slot, Block block, IBlockState state) {
        // 正方向悬挂告示牌
        if (block instanceof ModBlockHangingSign || ((ModBlockHangingSignAttached) block).isCardinalFacing(state, block.getMetaFromState(state))) {
            boolean isNorthSouth = (block instanceof ModBlockHangingSign) 
                ? ((ModBlockHangingSign) block).isNorthSouth(state.getValue(ModBlockHangingSign.FACING))
                : ((ModBlockHangingSignAttached) block).isNorthSouth(block.getMetaFromState(state));

            if (isNorthSouth) {
                if (slot == 0) {
                    this.setTextRenderPos(slot, HANG_ING_SING_ROTATION_MAP.get(0)); // 北方
                } else {
                    this.setTextRenderPos(slot, HANG_ING_SING_ROTATION_MAP.get(8)); // 南方
                }
            } else {
                if (slot == 0) {
                    this.setTextRenderPos(slot, HANG_ING_SING_ROTATION_MAP.get(12)); // 西方
                } else {
                    this.setTextRenderPos(slot, HANG_ING_SING_ROTATION_MAP.get(4)); // 东方 
                }
            }

            return this.getTextRenderPos(slot); // 返回刚存储的渲染位置
        }
        // 精确方向悬挂告示牌
        else if (block instanceof ModBlockHangingSignAttached) {
            int meta = block.getMetaFromState(state);
            if (slot == 1) {
                meta = (meta + 8) % 16; // 计算对称面meta值
            }
            this.setTextRenderPos(slot, HANG_ING_SING_ROTATION_MAP.get(meta));
            return this.getTextRenderPos(slot);
        }

        return HANG_ING_SING_ROTATION_MAP.get(0);
    }

// 渲染位置
    private static final Map<Integer, RenderPosition> SIGN_ROTATION_MAP = ImmutableMap.<Integer, RenderPosition>builder()
        .put(0, new RenderPosition(0.5, 0.5408, 0f))
        .put(1, new RenderPosition(0.4844, 0.5377, -22.5f))
        .put(2, new RenderPosition(0.4711, 0.5289, -45f))
        .put(3, new RenderPosition(0.4617, 0.5146, -67.5f))
        .put(4, new RenderPosition(0.4592, 0.5, 270f))
        .put(5, new RenderPosition(0.4617, 0.4854, 247.5f))
        .put(6, new RenderPosition(0.4711, 0.4711, 225f))
        .put(7, new RenderPosition(0.4844, 0.4623, 202.5f))
        .put(8, new RenderPosition(0.5, 0.4592, 180f))
        .put(9, new RenderPosition(0.5156, 0.4623, 157.5f))
        .put(10, new RenderPosition(0.5289, 0.4711, 135f))
        .put(11, new RenderPosition(0.5383, 0.4854, 112.5f))
        .put(12, new RenderPosition(0.5408, 0.5, 90f))
        .put(13, new RenderPosition(0.5383, 0.5146, 67.5f))
        .put(14, new RenderPosition(0.5289, 0.5289, 45f))
        .put(15, new RenderPosition(0.5156, 0.5377, 22.5f))
        .build();

    private static final Map<Integer, RenderPosition> SIGN_ROTATION_WALL_MAP = ImmutableMap.<Integer, RenderPosition>builder()
        .put(0, new RenderPosition(0.5, 0.1035, 0f))
        .put(1, new RenderPosition(0.8965, 0.5, 270f))
        .put(2, new RenderPosition(0.5, 0.8965, 180f))
        .put(3, new RenderPosition(0.1035, 0.5, 90f))
        .build();

    private static final Map<Integer, RenderPosition> HANG_ING_SING_ROTATION_MAP = ImmutableMap.<Integer, RenderPosition>builder()
        .put(0, new RenderPosition(0.5, 0.563, 0f))
        .put(1, new RenderPosition(0.4759, 0.5582, -22.5f))
        .put(2, new RenderPosition(0.4554, 0.5446, -45f))
        .put(3, new RenderPosition(0.4418, 0.5241, -67.5f))
        .put(4, new RenderPosition(0.4370, 0.5, 270f))
        .put(5, new RenderPosition(0.4418, 0.4759, 247.5f))
        .put(6, new RenderPosition(0.4554, 0.4554, 225f))
        .put(7, new RenderPosition(0.4759, 0.4418, 202.5f))
        .put(8, new RenderPosition(0.5, 0.4370, 180f))
        .put(9, new RenderPosition(0.5241, 0.4418, 157.5f))
        .put(10, new RenderPosition(0.5446, 0.4554, 135f))
        .put(11, new RenderPosition(0.5582, 0.4759, 112.5f))
        .put(12, new RenderPosition(0.563, 0.5, 90f))
        .put(13, new RenderPosition(0.5582, 0.5241, 67.5f))
        .put(14, new RenderPosition(0.5446, 0.5446, 45f))
        .put(15, new RenderPosition(0.5241, 0.5582, 22.5f))
        .build();

// 其他覆盖方法
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
}