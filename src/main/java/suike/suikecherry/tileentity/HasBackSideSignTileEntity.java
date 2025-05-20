package suike.suikecherry.tileentity;

import java.util.Map;
import java.util.HashMap;

import suike.suikecherry.SuiKe;
import suike.suikecherry.block.ModBlockSignWall;
import suike.suikecherry.block.ModBlockHangingSign;
import suike.suikecherry.block.ModBlockHangingSignAttached;
import suike.suikecherry.client.gui.GuiID;
import suike.suikecherry.client.gui.GuiEditSign;
import suike.suikecherry.packet.PacketHandler;
import suike.suikecherry.packet.SignTextUpdatePacket;

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

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class HasBackSideSignTileEntity extends TileEntitySign {
    // 背面文本
    public final ITextComponent[] backText = new ITextComponent[] {new TextComponentString(""),  new TextComponentString(""), new TextComponentString(""), new TextComponentString("")};

    public boolean isUsing;
    private int editedSlot = 0;
    private int hasTextSides = -1;

    public int getEditedSlot() {
        return this.editedSlot;
    }

    public void setEditedSlot(int editedSlot) {
        this.editedSlot = editedSlot;
    }

    public Map<String, String> getEditedBlockType() {
        String blockName = this.world.getBlockState(this.pos).getBlock().getRegistryName().toString();
        String type = blockName.replace("suikecherry:", "");

        Map<String, String> result = new HashMap<>();
        // 判断是否包含 "_hanging_sign" 或 "_sign"
        if (type.contains("_hanging_sign")) {
            result.put("hanging_sign", type.substring(0, type.indexOf("_hanging_sign")));
        }
        else if (type.contains("_sign")) {
            result.put("sign", type.substring(0, type.indexOf("_sign")));
        }
        else {
            result.put("none", type);
        }

        return result;
    }

    public HasBackSideSignTileEntity() {
        for (int i = 0; i < 4; i++) {
            signText[i] = new TextComponentString("");
            backText[i] = new TextComponentString("");
        }
        this.isUsing = false;
    }

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
        if (!(this.world.getBlockState(this.pos).getBlock() instanceof ModBlockSignWall)) {
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
    public ITextComponent[] getTextForEditing(int slot) {
        return slot == 0 ? this.signText : this.backText;
    }

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

// 编辑文本
    // 更新指定面的文本
    public void updateText(int slot, ITextComponent[] text, boolean isLocking) {
        this.isUsing = isLocking;

        if (slot == 0) 
            System.arraycopy(text, 0, this.signText, 0, text.length);
        else if (slot == 1) 
            System.arraycopy(text, 0, this.backText, 0, text.length);
        else return;

        this.upTextSides();
        if (world.isRemote) {
            // 本地服务器线程更新
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
    }

    public void forceUpdateText(int slot, ITextComponent[] text) {
        if (slot == 0) System.arraycopy(text, 0, signText, 0, 4);
        else System.arraycopy(text, 0, backText, 0, 4);

        this.markDirty();
        WorldServer worldServer = (WorldServer)world;
        worldServer.getChunkFromBlockCoords(pos).markDirty();
    }

    // 编辑文本: 根据 slot 处理不同面的编辑
    public boolean editText(EntityPlayer player) {
        int slot = this.getEditedSlot();
        if (slot != 0 && slot != 1) return false;

        if (world.isRemote) {
            // 在打开 GUI 之前检查使用状态
            if (this.isUsing) {
                // 正在使用, 不打开 GUI 直接返回
                player.sendMessage(new TextComponentString(I18n.format("suikecherry.sign.isUsing")));
                return false;
            }

            // 客户端直接打开编辑界面
            player.openGui(SuiKe.instance, GuiID.GUI_SIGN_EDIT, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }

        this.setPlayer(player);

        // 发送初始文本数据到客户端
        PacketHandler.INSTANCE.sendTo(
            new SignTextUpdatePacket(pos, slot, this.getTextForEditing(slot), true),
            (EntityPlayerMP) player
        );
        return true;
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
        if (slot == -1) return;
        if (slot == 0 || slot == 2)
            this.textRenderPos_0 = null;
        else if (slot == 1 || slot == 2)
            this.textRenderPos_1 = null;
    }

    private void setTextRenderPos(int slot, RenderPosition renderPos) {
        if (slot == 0)
            this.textRenderPos_0 = renderPos;
        else if (slot == 1)
            this.textRenderPos_1 = renderPos;
    }

// 获取渲染位置
    private static final double x = 0.5, z = 0.5781;
    private static final RenderPosition defaultTextRenderPos = new RenderPosition(0.5, 0.5781, 180f);;
    private static final Map<Integer, RenderPosition> ROTATION_MAP = new HashMap<>();

    static {
        ROTATION_MAP.put(1, new RenderPosition(0.4701, 0.5722, 157.5f));
        ROTATION_MAP.put(2, new RenderPosition(0.4448, 0.5552, 135f));
        ROTATION_MAP.put(3, new RenderPosition(0.4278, 0.5299, 112.5f));
        ROTATION_MAP.put(5, new RenderPosition(0.4278, 0.4701, 67.5f));
        ROTATION_MAP.put(6, new RenderPosition(0.4448, 0.4448, 45f));
        ROTATION_MAP.put(7, new RenderPosition(0.4701, 0.4278, 22.5f));
        ROTATION_MAP.put(9, new RenderPosition(0.5299, 0.4278, -22.5f));
        ROTATION_MAP.put(10, new RenderPosition(0.5552, 0.4448, -45f));
        ROTATION_MAP.put(11, new RenderPosition(0.5722, 0.4701, -67.5f));
        ROTATION_MAP.put(13, new RenderPosition(0.5722, 0.5299, -112.5f));
        ROTATION_MAP.put(14, new RenderPosition(0.5552, 0.5552, -135f));
        ROTATION_MAP.put(15, new RenderPosition(0.5299, 0.5722, -157.5f));
    }

    public RenderPosition getTextRenderPosition(int slot) {
        RenderPosition renderPos = getTextRenderPos(slot);

        if (renderPos != null) return renderPos; // 有已存储的渲染位置直接返回, 避免重复获取

        IBlockState state = this.getWorld().getBlockState(this.getPos());
        Block block = state.getBlock();

        // 获取方块方向
        boolean isNorthSouth = (block instanceof ModBlockHangingSign) 
            ? ((ModBlockHangingSign) block).getBlockFacing(state.getValue(ModBlockHangingSign.FACING))
            : ((ModBlockHangingSignAttached) block).isNorthSouth(block.getMetaFromState(state));

        if (block instanceof ModBlockHangingSign || ((ModBlockHangingSignAttached) block).isCardinalFacing(state)) {
            if (isNorthSouth) {
                if (slot == 0) {
                    this.setTextRenderPos(slot, defaultTextRenderPos); // 北方
                } else {
                    this.setTextRenderPos(slot, new RenderPosition(x, 0.4218, 0f)); // 南方
                }
            } else {
                if (slot == 0) {
                    this.setTextRenderPos(slot, new RenderPosition(z, x, 270f)); // 西方 // 交换 x z
                } else {
                    this.setTextRenderPos(slot, new RenderPosition(0.4218, x, 90f)); // 东方 
                }
            }

            return this.getTextRenderPos(slot); // 返回刚存储的渲染位置
        } else if (block instanceof ModBlockHangingSignAttached) {
            int meta = block.getMetaFromState(state);
            if (slot == 1) {
                meta = (meta + 8) % 16; // 计算对称面meta值
            }
            this.setTextRenderPos(slot, ROTATION_MAP.get(meta));
            return this.getTextRenderPos(slot);
        }

        // 默认返回: 北方 0 槽
        return defaultTextRenderPos;
    }

    public static class RenderPosition{
        private final double x, z;
        private final float rotation;

        public RenderPosition(double x, double z, float rotation) {
            this.x = x;
            this.z = z;
            this.rotation = rotation;
        }

        public double getX() {
            return this.x;
        }

        public double getZ() {
            return this.z;
        }

        public float getRotation() {
            return this.rotation;
        }
    }
}