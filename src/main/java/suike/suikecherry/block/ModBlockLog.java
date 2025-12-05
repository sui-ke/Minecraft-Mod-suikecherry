package suike.suikecherry.block;

import suike.suikecherry.SuiKe;
import suike.suikecherry.item.ItemBase;
import suike.suikecherry.sound.Sound;
import suike.suikecherry.config.ConfigValue;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemAxe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.world.World;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Enchantments;
import net.minecraft.enchantment.EnchantmentHelper;

//原木类
public class ModBlockLog extends BlockLog {
    public ModBlockLog(String name) {
        /*设置物品名*/this.setRegistryName(name);
        /*设置物品名key*/this.setUnlocalizedName(SuiKe.MODID + "." + name);
        /*设置创造模式物品栏*/this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        /*设置硬度*/this.setHardness(2.0F);
        /*设置抗爆性*/this.setResistance(2.0F);
        /*设置挖掘等级*/this.setHarvestLevel("axe", 0);
        /*设置不透明度*/this.setLightOpacity(255);
        /*设置声音*/this.setSoundType(ModBlockPlanks.EnumType.getEnumType(name).getWoodSound());

        /*添加到BLOCKS列表*/BlockBase.BLOCKS.add(this);
        /*添加到ITEMS列表*/BlockBase.ITEMBLOCKS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));

        /*设置朝向*/this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumAxis.Y));
    }

    public ModBlockLog(String name, ModBlockLog log) {
        this(name);
        if (log != null) log.setStrippedLog(this);
    }

    private ModBlockLog strippedLog;
    private void setStrippedLog(ModBlockLog strippedLog) {
        if (this.strippedLog == null) this.strippedLog = strippedLog;
    }

//方块属性
    public static final IProperty<EnumAxis> AXIS = BlockLog.LOG_AXIS;

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS);
    }
    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState();
        switch (meta & 12) {
            case 0:
                state = state.withProperty(AXIS, EnumAxis.Y);
                break;
            case 4:
                state = state.withProperty(AXIS, EnumAxis.X);
                break;
            case 8:
                state = state.withProperty(AXIS, EnumAxis.Z);
                break;
            default:
                state = state.withProperty(AXIS, EnumAxis.NONE);
        }
        return state;
    }
    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        switch ((EnumAxis) state.getValue(AXIS)) {
            case X:
                meta |= 4;
                break;
            case Z:
                meta |= 8;
                break;
            case NONE:
                meta |= 12;
        }
        return meta;
    }

//方块交互
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!ConfigValue.stripLog) return false;

        ModBlockLog block = (ModBlockLog) state.getBlock();

        if (block.strippedLog != null) {
            // 检查是否是右键点击
            if (/*event.getEntityPlayer().isSneaking() && */hand == EnumHand.MAIN_HAND) {
                // 检查手持物品
                ItemStack itemStack = player.getHeldItem(hand);
                Item item = itemStack.getItem();
                if (item != null && item instanceof ItemAxe) {
                    IBlockState newState = block.strippedLog.getDefaultState().withProperty(AXIS, state.getValue(AXIS));

                    /*替换方块*/world.setBlockState(pos, newState);

                    /*播放去皮音效*/Sound.playSound(world, pos, "item.axe.strip");

                    int efficiencyLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, itemStack);

                    boolean damage = true;
                    if (efficiencyLevel > 0) {
                        if (Math.random() * 10 < efficiencyLevel) {
                            damage = false;
                        }
                    }

                    if (damage) {
                        /*给斧子减去一点耐久*/itemStack.damageItem(1, player);
                    }

                    return true;
                }
            }
        }

        return false;
    }
}